package org.ethan.checkersgame.logic;

import org.ethan.checkersgame.logic.enums.PieceType;
import org.ethan.checkersgame.logic.enums.PlayerColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.ethan.checkersgame.features.FeaturesList.DEBUG_PRINT_LINES;
import static org.ethan.checkersgame.features.FeaturesList.DO_DEBUG_VALIDATIONS;


public class GameStateManager
{
    public static int BOARD_X_LENGTH = 8;
    public static int BOARD_Y_LENGTH = 8;

    // TODO(ethan.lo) is there a more general way of codifying edges
    // blacks start on bottom and red on top
    private static int RED_KING_PROMO_Y_IDX = BOARD_Y_LENGTH - 1;
    private static int BLACK_KING_PROMO_Y_IDX = 0;

    private static byte EMPTY_SPACE_REPR = 0;
    private static byte BLACK_MAN_REPR = 1;
    private static byte BLACK_KING_REPR = 2;
    private static byte RED_MAN_REPR = -1;
    private static byte RED_KING_REPR = -2;

    private static Point NULL_POINT_COORDS = new Point(-1, -1);

    private byte[][] board; /* represents the checker board coordinates
				     	  Board looks like:
						   0 1 2 3 4 5 6 7
						   ________________
						0 | 0 0 0 0 0 0 0 0 
						1 | 0 0 0 0 0 0 0 0 
						2 | 0 0 0 0 0 0 0 0 
						3 | 0 0 0 0 0 0 0 0 
						4 | 0 0 0 0 0 0 0 0 
						5 | 0 0 0 0 0 0 0 0 
						6 | 0 0 0 0 0 0 0 0 
						7 | 0 0 0 0 0 0 0 0 

				     // TODO: representing as shifts might make it faster in the future

				     */


    private PlayerColor turnColor;

    private PlayerColor winColor;

    private boolean isStatemate;

    private Point jumpingPieceCoords = new Point(NULL_POINT_COORDS);


    // Constructor
    public GameStateManager()
    {
        this.board = new byte[BOARD_X_LENGTH][BOARD_Y_LENGTH];
        this.turnColor = PlayerColor.BLACK;
        this.winColor = PlayerColor.NONE;
        isStatemate = false;

        this.resetBoard();
    }

    public PieceType getPieceType(Point pieceCoords)
    {
        final byte rawPieceRepr = getPieceAt(pieceCoords);

        if ( rawPieceRepr == EMPTY_SPACE_REPR )
        {
            return PieceType.NONE;
        }
        else if ( rawPieceRepr == BLACK_MAN_REPR || rawPieceRepr == RED_MAN_REPR )
        {
            return PieceType.MAN;
        }
        else
        {
            return PieceType.KING;
        }
    }

    public PlayerColor getPlayerColor(Point pieceCoords)
    {
        final byte rawPieceRepr = getPieceAt(pieceCoords);

        return getPlayerColor(rawPieceRepr);
    }

    public PlayerColor getPlayerColor(int x, int y)
    {
        final byte rawPieceRepr = getPieceAt(x, y);

        return getPlayerColor(rawPieceRepr);
    }

    private PlayerColor getPlayerColor(byte rawPieceRepr)
    {
        if ( rawPieceRepr == EMPTY_SPACE_REPR )
        {
            return PlayerColor.NONE;
        }
        else if ( rawPieceRepr == RED_MAN_REPR || rawPieceRepr == RED_KING_REPR )
        {
            return PlayerColor.RED;
        }
        else
        {
            return PlayerColor.BLACK;
        }
    }

    public void makeMove(Move move)
    {
        validateMove(move);

        // 1. Move piece
        // 2. possibly promote
        final byte pieceRepr = getPieceAt(move.getStartPos());
        board[move.getStartPos().x][move.getStartPos().y] = EMPTY_SPACE_REPR;

        if ( getPlayerColor(pieceRepr) == PlayerColor.RED && move.getEndPos().y == RED_KING_PROMO_Y_IDX )
        {
            board[move.getEndPos().x][move.getEndPos().y] = RED_KING_REPR;
        }
        else if ( getPlayerColor(pieceRepr) == PlayerColor.BLACK && move.getEndPos().y == BLACK_KING_PROMO_Y_IDX )
        {
            board[move.getEndPos().x][move.getEndPos().y] = BLACK_KING_REPR;
        }
        else
        {
            board[move.getEndPos().x][move.getEndPos().y] = pieceRepr;
        }

        // handle jump stuff
        // remove piece
        // set jumping piece coord
        // flip turn
        if ( isMoveAJump(move) )
        {
            final Point removedPieceCoords = getMidpoint(move.getEndPos(), move.getStartPos());
            board[removedPieceCoords.x][removedPieceCoords.y] = EMPTY_SPACE_REPR;

            jumpingPieceCoords.setLocation(move.getEndPos());

            if ( getPossibleMoves(move.getEndPos()).isEmpty() )
            {
                jumpingPieceCoords.setLocation(NULL_POINT_COORDS);
                flipTurn();
            }
        }
        else
        {
            flipTurn();
        }


        // Check for win condition
        int numBlacks = 0;
        int numReds = 0;
        for ( int x = 0; x < BOARD_X_LENGTH; x++ )
        {
            for ( int y = 0; y < BOARD_Y_LENGTH; y++ )
            {
                final PlayerColor pieceColor = getPlayerColor(x, y);
                if ( pieceColor == PlayerColor.RED )
                {
                    numReds++;
                }
                else if ( pieceColor == PlayerColor.BLACK )
                {
                    numBlacks++;
                }
            }
        }

        if ( numBlacks == 0 )
        {
            winColor = PlayerColor.RED;
        }
        else if ( numReds == 0 )
        {
            winColor = PlayerColor.BLACK;
        }

        // check for stalemate
        // stalemate happens if it is X's turn and X has no moves
        isStatemate = true;
        Point reUsedPoint = new Point();
        for ( int x = 0; x < BOARD_X_LENGTH; x++ )
        {
            for ( int y = 0; y < BOARD_Y_LENGTH; y++ )
            {
                reUsedPoint.setLocation(x, y);
                if ( getPossibleMoves(reUsedPoint).size() > 0 )
                {
                    isStatemate = false;
                }
            }
        }
    }

    private Point getMidpoint(Point a, Point b)
    {
        return new Point(
                (a.x + b.x) / 2,
                (a.y + b.y) / 2
        );
    }

    private void validateMove(Move move)
    {
        if ( DO_DEBUG_VALIDATIONS )
        {
            if ( Math.abs(move.getStartPos().x - move.getEndPos().x) != Math.abs(move.getStartPos().y - move.getEndPos().y) )
            {
                throw new IllegalStateException("invalid move: " + move.getStartPos() + " to " + move.getEndPos());
            }

            if ( Math.abs(move.getStartPos().x - move.getEndPos().x) != 1 && Math.abs(move.getStartPos().x - move.getEndPos().x) != 2 )
            {
                throw new IllegalStateException("invalid move: " + move.getStartPos() + " to " + move.getEndPos());
            }
        }
    }

    private boolean isMoveAJump(Move move)
    {
        return Math.abs(move.getStartPos().x - move.getEndPos().x) == 2 && Math.abs(move.getStartPos().y - move.getEndPos().y) == 2;
    }

    // Function to get possible moves: Given a piece, what are it's possible moves
    public List<Move> getPossibleMoves(Point pieceCoords)
    {
        ArrayList<Move> moves = new ArrayList<>();

        if ( getPlayerColor(pieceCoords) == PlayerColor.NONE )
        {
            return new ArrayList<>();
        }

        if ( getPlayerColor(pieceCoords) != getTurnColor() )
        {
            return new ArrayList<>();
        }

        if ( isJumpInProgress() && !jumpingPieceCoords.equals(pieceCoords) )
        {
            // If we are in a jumping state, only the jumping piece may move
            return new ArrayList<>();
        }

        List<Point> possibleMoveVectors = getPossibleMoveVectors(pieceCoords);

        for ( Point possibleMoveVector : possibleMoveVectors )
        {
            maybeAddPossibleJumpMove(pieceCoords, possibleMoveVector, moves);

            if ( !isJumpInProgress() )
            {
                maybeAddPossibleStepMove(pieceCoords, possibleMoveVector, moves);
            }
        }

        if ( DEBUG_PRINT_LINES )
        {
            System.out.print("possible moves: ");
            for ( Move move : moves )
            {
                System.out.print(" | " + move.getStartPos().x + ", " + move.getStartPos().y + " -> " + move.getEndPos().x + ", " + move.getEndPos().y);
            }
            System.out.println();
        }

        return moves;
    }


    private List<Point> getPossibleMoveVectors(Point pieceCoords)
    {
        List<Point> possibleMoveVectors = new ArrayList<>();
        possibleMoveVectors.add(new Point(-1, getForwardDirectionMultiplier(pieceCoords)));
        possibleMoveVectors.add(new Point(1, getForwardDirectionMultiplier(pieceCoords)));

        if ( getPieceType(pieceCoords) == PieceType.KING )
        {
            possibleMoveVectors.add(new Point(-1, -getForwardDirectionMultiplier(pieceCoords)));
            possibleMoveVectors.add(new Point(1, -getForwardDirectionMultiplier(pieceCoords)));
        }
        return possibleMoveVectors;
    }

    private void maybeAddPossibleStepMove(Point pieceCoords, Point possibleMoveVector, List<Move> possibleMoves)
    {
        final Point potentialEndCoords = new Point(pieceCoords.x + possibleMoveVector.x, pieceCoords.y + possibleMoveVector.y);

        if ( isCoordsOnBoard(potentialEndCoords) && !isSpaceOccupied(potentialEndCoords) )
        {
            Move possibleMove = new Move(new Point(pieceCoords), potentialEndCoords);
            possibleMoves.add(possibleMove);
        }
    }

    private void maybeAddPossibleJumpMove(Point pieceCoords, Point possibleMoveVector, List<Move> possibleMoves)
    {
        final Point potenialJumpedPieceCoords = new Point(pieceCoords.x + possibleMoveVector.x, pieceCoords.y + possibleMoveVector.y);
        final Point potentialEndCoords = new Point(pieceCoords.x + 2 * possibleMoveVector.x, pieceCoords.y + 2 * possibleMoveVector.y);

        if ( isCoordsOnBoard(potenialJumpedPieceCoords) &&
                isSpaceOccupied(potenialJumpedPieceCoords) &&
                !isPiecesSameColor(pieceCoords, potenialJumpedPieceCoords) &&
                isCoordsOnBoard(potentialEndCoords) &&
                !isSpaceOccupied(potentialEndCoords) )
        {
            Move possibleMove = new Move(new Point(pieceCoords), potentialEndCoords);
            possibleMoves.add(possibleMove);
        }
    }

    private boolean isJumpInProgress()
    {
        return !jumpingPieceCoords.equals(NULL_POINT_COORDS);
    }

    private int getForwardDirectionMultiplier(Point pieceCoords)
    {
        if ( getPlayerColor(pieceCoords) == PlayerColor.RED )
        {
            return 1;
        }
        else if ( getPlayerColor(pieceCoords) == PlayerColor.BLACK )
        {
            return -1;
        }
        else
        {
            if ( DO_DEBUG_VALIDATIONS )
            {
                throw new IllegalStateException("getDirectionMultiplier() called on empty coord " + pieceCoords);
            }
            return 0;
        }
    }

    private boolean isPiecesSameColor(Point thisCoords, Point thatCoords)
    {
        return getPlayerColor(thatCoords) == getPlayerColor(thisCoords);
    }

    private boolean isCoordsOnBoard(Point coords)
    {
        return coords.x >= 0 && coords.y >= 0 && coords.x < BOARD_X_LENGTH && coords.y < BOARD_Y_LENGTH;
    }

    private boolean isSpaceOccupied(Point coord)
    {
        return getPieceAt(coord) != EMPTY_SPACE_REPR;
    }

    public byte[][] getBoard()
    {
        return board;
    }


    /**
     * sets board with blacks on bottom and red on top
     */
    private void resetBoard()
    {
        for ( int i = 0; i < 4; i++ )
        {
            this.board[i * 2 + 1][0] = RED_MAN_REPR;
        }
        for ( int i = 0; i < 4; i++ )
        {
            this.board[i * 2][1] = RED_MAN_REPR;
        }
        for ( int i = 0; i < 4; i++ )
        {
            this.board[i * 2 + 1][2] = RED_MAN_REPR;
        }
        for ( int i = 0; i < 4; i++ )
        {
            this.board[i * 2][5] = BLACK_MAN_REPR;
        }
        for ( int i = 0; i < 4; i++ )
        {
            this.board[i * 2 + 1][6] = BLACK_MAN_REPR;
        }
        for ( int i = 0; i < 4; i++ )
        {
            this.board[i * 2][7] = BLACK_MAN_REPR;
        }
    }

    public PlayerColor getTurnColor()
    {
        return turnColor;
    }


    private void flipTurn()
    {
        if ( turnColor == PlayerColor.BLACK )
        {
            turnColor = PlayerColor.RED;
        }
        else
        {
            turnColor = PlayerColor.BLACK;
        }
    }

    private byte getPieceAt(int x, int y)
    {
        return board[x][y];
    }

    private byte getPieceAt(Point coords)
    {
        return getPieceAt(coords.x, coords.y);
    }

    public PlayerColor getWinColor()
    {
        return winColor;
    }

    public boolean isStatemate()
    {
        return isStatemate;
    }
}
