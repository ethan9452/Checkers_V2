package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.ai.AI;
import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;
import org.ethan.checkersgame.logic.enums.PieceType;
import org.ethan.checkersgame.logic.enums.PlayerColor;

public class MiniMaxAI extends AI
{
    private final int depth;

    public MiniMaxAI(PlayerColor color)
    {
        super(color);

        depth = 6;
    }

    @Override
    public Move chooseMove(GameStateManager stateManager)
    {
        GameStateManager initialState = new GameStateManager(stateManager);

        MiniMaxRetVal minimaxResult = miniMaxRecurse(initialState, depth, aiColor);

        return minimaxResult.getMove();
    }

    /**
     * on turn, choose max. on opponent's turn, choose min
     *
     * @param gameState
     * @param remainingDepth
     * @param colorOfTurn
     * @return
     */
    private MiniMaxRetVal miniMaxRecurse(GameStateManager gameState, int remainingDepth, PlayerColor colorOfTurn)
    {
        if ( remainingDepth == 0 || gameState.isStatemate() || gameState.getWinColor() != PlayerColor.NONE )
        {
            return new MiniMaxRetVal(getRewardAssociatedWithState(gameState), null);
        }

        final boolean isMaximizing = colorOfTurn == aiColor;

        int curBestReward = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move curBestMove = null;

        for ( Move possibleMove : gameState.getAllPossibleMoves() )
        {
            GameStateManager childState = new GameStateManager(gameState);
            childState.makeMove(possibleMove);

            final MiniMaxRetVal miniMaxRetVal = miniMaxRecurse(childState, remainingDepth - 1, getOppositeColor(colorOfTurn));
            final int reward = miniMaxRetVal.getReward();

            if ( isMaximizing )
            {
                if ( reward > curBestReward )
                {
                    curBestReward = reward;
                    curBestMove = possibleMove;
                }
            }
            else
            {
                if ( reward < curBestReward )
                {
                    curBestReward = reward;
                    curBestMove = possibleMove;
                }
            }
        }

        return new MiniMaxRetVal(curBestReward, curBestMove);
    }

    private PlayerColor getOppositeColor(PlayerColor color)
    {
        if ( color == PlayerColor.RED )
        {
            return PlayerColor.BLACK;
        }
        else if ( color == PlayerColor.BLACK )
        {
            return PlayerColor.RED;
        }
        else
        {
            return null;
        }
    }

    private int getRewardAssociatedWithState(GameStateManager stateManager)
    {
        int reward = 0;

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
                            reward += 2;
                        }
                        else
                        {
                            reward++;
                        }
                    }
                    else
                    {
                        if ( stateManager.getPieceType(x, y) == PieceType.KING )
                        {
                            reward -= 2;
                        }
                        else
                        {
                            reward--;
                        }
                    }
                }
            }
        }

        return reward;
    }
}
