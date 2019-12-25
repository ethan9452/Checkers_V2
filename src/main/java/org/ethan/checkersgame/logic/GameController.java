package org.ethan.checkersgame.logic;

import java.awt.Point;


public class GameController
{
    GameStateManager gameStateManager;
    boolean          blackAI = false;
    boolean          redAI   = false;

    Point selectedPieceCoordinates = new Point( -1, -1 );

    String message = ""; // message displayed to player

    // Constructor
    public GameController()
    {
        this.gameStateManager = new GameStateManager();
    }


    // Recieves the board space that the user clicked on (between [0,0] to [7,7]
    // and acts accordingly
    public void recieveClick(Point point)
    {
        validateCoordinates( point );

        // if piece is not selected, and the color is the same as the turn, set that piece to be 'selected_piece'
        if ( !point.equals( selectedPieceCoordinates ) &&
                ((getBoard()[point.x][point.y] < 0) && gameStateManager.getTurn() == gameStateManager.red ||
                        ((this.getBoard()[(int) point.getX()][(int) point.getY()] > 0) && this.gameStateManager.getTurn() == gameStateManager.black)) )
        {
            selectedPieceCoordinates.setLocation( point );
        }

        // if piece is selected, unselect
        if ( point.equals( selectedPieceCoordinates ) )
        {
            selectedPieceCoordinates.setLocation( -1, -1 );
        }

        // if piece is selected and valid move is chosen, make the move
        Move[] possible_moves = gameStateManager.getPossibleMoves( selectedPieceCoordinates ); // get all moves for the selected piece
        for ( int i = 0; i < possible_moves.length; i++ )
        { // loop thru all possible moves
            if ( possible_moves[i].getEndPos().equals( point ) )
            { // if clicked space matches valid move, make the move
                makeMove( possible_moves[i] );
            }
        }


    }

    private void validateCoordinates(Point point)
    {
        if ( point.getX() < 0 || point.getX() > 7 || point.getY() < 0 || point.getY() > 7 )
        {
            throw new IllegalArgumentException( "illegal coordinate" + point );
        }
    }

    // Given a move, alters the board accordingly
    protected void makeMove(Move move)
    {
        int piece = getBoard()[(int) move.getStartPos().getX()][(int) move.getStartPos().getY()];
        gameStateManager.setSpace( (int) move.getEndPos().getX(), (int) move.getEndPos().getY(), piece );
        gameStateManager.setSpace( (int) move.getStartPos().getX(), (int) move.getStartPos().getY(), 0 );

        // Check if move is a jump
        if ( Math.abs( move.getEndPos().getX() - move.getStartPos().getX() ) == 2 )
        {
            // remove jumped piece
            int jumpedX = (int) ((move.getEndPos().getX() + move.getStartPos().getX()) / 2);
            int jumpedY = (int) ((move.getEndPos().getY() + move.getStartPos().getY()) / 2);
            gameStateManager.setSpace( jumpedX, jumpedY, 0 );

            gameStateManager.setJumpInProgress( true );
            gameStateManager.setJumpingPiece( move.getEndPos() );

            gameStateManager.flipTurn(); // If a piece is jumped, don't change turns, so we premtively undo the flip-turn that happens later.

            // If the jumping piece has no more moves, jump is NO LONGER in progress
            if ( gameStateManager.getPossibleMoves( gameStateManager.getJumpingPiece() ).length == 0 )
            {
                gameStateManager.setJumpInProgress( false );
                gameStateManager.flipTurn(); // If jump is over, we need to change turns. (3 flips equals 1 flip)
            }
        }
        gameStateManager.flipTurn();
    }

    // org.ethan.checkersgame.ai.AI methods
    // If both are org.ethan.checkersgame.ai.AI, these 2 methods loop back and forth
    // If not, the method ends, and we wait for mouse input from user
    public void blackAI()
    {  //TODO
        // make some move
        if ( redAI == true )
        {
            redAI();
        }
    }

    public void redAI()
    {  //TODO
        //make some move
        if ( blackAI == true )
        {
            blackAI();
        }
    }


    public int[][] getBoard()
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
                System.out.print( gameStateManager.getBoard()[x][y] );
            }
            System.out.println();
        }
    }

}
