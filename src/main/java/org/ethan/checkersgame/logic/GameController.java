package org.ethan.checkersgame.logic;

import org.ethan.checkersgame.ai.AI;
import org.ethan.checkersgame.ai.minimax.MiniMaxAI;
import org.ethan.checkersgame.ai.minimax.MiniMaxV2AI;
import org.ethan.checkersgame.logic.enums.PlayerColor;
import org.ethan.checkersgame.viewcontrol.RepaintHandle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class GameController
{
    private final RepaintHandle repaintHandle;

    private final GameStateManager gameStateManager;

    AI blackAI = null;
    AI redAI = null;

    private static Point UNSELECTED_POINT = new Point(-1, -1);
    private Point currentSelectedPieceCoords = new Point(UNSELECTED_POINT);


    // Constructor
    public GameController(RepaintHandle repaintHandle)
    {
        this.repaintHandle = repaintHandle;

        this.gameStateManager = new GameStateManager();

        repaintHandle.triggerImmediateRepaint();

        ///// TODO: set ai in ui
        redAI = new MiniMaxAI(PlayerColor.RED);
        blackAI = new MiniMaxV2AI(PlayerColor.BLACK);


//        executeAI();
    }

    public Point getCurrentSelectedPieceCoords()
    {
        return currentSelectedPieceCoords;
    }

    public List<Point> getCurrentSelectedPiecePossibleMoveEndCoords()
    {
        List<Point> moveEnds = new ArrayList<>();
        for ( Move possibleMove : gameStateManager.getPossibleMoves(currentSelectedPieceCoords) )
        {
            moveEnds.add(possibleMove.getEndPos());
        }
        return moveEnds;
    }

    public boolean isAPieceCurrentlySelected()
    {
        return !currentSelectedPieceCoords.equals(UNSELECTED_POINT);
    }

    protected void executeAI()
    {
        if ( !isAHumansTurn() )
        {
            // TODO: come up with a way to animate moves better

            Move aiMove = null;
            if ( gameStateManager.getTurnColor() == PlayerColor.RED )
            {
                aiMove = redAI.chooseMove(gameStateManager);
            }
            else if ( gameStateManager.getTurnColor() == PlayerColor.BLACK )
            {
                aiMove = blackAI.chooseMove(gameStateManager);
            }

            gameStateManager.makeMove(aiMove);

            repaintHandle.triggerImmediateRepaint();

            executeAI();
        }
    }

    public void recieveClick(Point clickedCoords)
    {
//        if ( !isAHumansTurn() )
//        {
//            // If it is an AI's turn, don't recieve any clicks.
//            return;
//        }
//
//        validateCoordinates(clickedCoords);
//
//        if ( currentSelectedPieceCoords.equals(UNSELECTED_POINT) )
//        {
//            if ( gameStateManager.getPlayerColor(clickedCoords) == gameStateManager.getTurnColor() )
//            {
//                currentSelectedPieceCoords.setLocation(clickedCoords);
//            }
//        }
//        else
//        {
//            if ( currentSelectedPieceCoords.equals(clickedCoords) )
//            {
//                currentSelectedPieceCoords.setLocation(UNSELECTED_POINT);
//            }
//            else if ( gameStateManager.getPlayerColor(clickedCoords) == gameStateManager.getTurnColor() )
//            {
//                currentSelectedPieceCoords.setLocation(clickedCoords);
//            }
//            else
//            {
//                List<Move> possibleMoves = gameStateManager.getPossibleMoves(currentSelectedPieceCoords);
//                for ( Move possibleMove : possibleMoves )
//                {
//                    if ( possibleMove.getEndPos().equals(clickedCoords) )
//                    {
//                        gameStateManager.makeMove(possibleMove);
//                        repaintHandle.triggerImmediateRepaint();
//
//                        executeAI();
//                    }
//                }
//
//                currentSelectedPieceCoords.setLocation(UNSELECTED_POINT);
//            }
//        }



executeAI();
    }

    private boolean isAHumansTurn()
    {
        if ( blackAI == null && gameStateManager.getTurnColor() == PlayerColor.BLACK )
        {
            return true;
        }

        if ( redAI == null && gameStateManager.getTurnColor() == PlayerColor.RED )
        {
            return true;
        }

        return false;
    }

    private void validateCoordinates(Point point)
    {
        if ( point.getX() < 0 || point.getX() > 7 || point.getY() < 0 || point.getY() > 7 )
        {
            throw new IllegalArgumentException("illegal coordinate" + point);
        }
    }

    public byte[][] getBoard()
    {
        return gameStateManager.getBoard();
    }

    // Debug
    public void printBoard()
    {
        for ( int y = 0; y < 8; y++ )
        {
            for ( int x = 0; x < 8; x++ )
            {
                System.out.print(gameStateManager.getBoard()[x][y]);
            }
            System.out.println();
        }
    }

    public PlayerColor getWinColor()
    {
        return gameStateManager.getWinColor();
    }

    public boolean isStalemate()
    {
        return gameStateManager.isStatemate();
    }

    public int getTurnNumber()
    {
        return gameStateManager.getTurnNumber();
    }
}
