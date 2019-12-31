package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.enums.PieceType;
import org.ethan.checkersgame.logic.enums.PlayerColor;

/**
 * Modifies reward to be ratio of pieces to enemy pieces (with kings weighted)
 *
 * Motivation is to favor 1 to 1 trades when ahead
 */
public class MiniMaxV2AI extends MiniMaxAI
{
    private static double KING_WEIGHT = 4;
    private static double MAN_WEIGHT = 3;


    public MiniMaxV2AI(PlayerColor color)
    {
        super(color);

        depth = 9;
    }

    protected double getRewardAssociatedWithState(GameStateManager stateManager)
    {
        double piecesWeight = 0;
        double enemyPiecesWeight = 0;

        for ( int x = 0; x < stateManager.getBoard().length; x++ )
        {
            for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
            {
                if ( stateManager.getPieceType(x, y) != PieceType.NONE )
                {
                    if ( stateManager.getPlayerColor(x, y) == aiColor )
                    {
                        if ( stateManager.getPieceType(x, y) == PieceType.KING )
                        {
                            piecesWeight += KING_WEIGHT;
                        }
                        else
                        {
                            piecesWeight += MAN_WEIGHT;
                        }
                    }
                    else
                    {
                        if ( stateManager.getPieceType(x, y) == PieceType.KING )
                        {
                            enemyPiecesWeight += KING_WEIGHT;
                        }
                        else
                        {
                            enemyPiecesWeight += MAN_WEIGHT;
                        }
                    }
                }
            }
        }

        if ( enemyPiecesWeight <= 0 )
        {
            return Double.MAX_VALUE;
        }
        else
        {
            return piecesWeight / enemyPiecesWeight;
        }
    }
}
