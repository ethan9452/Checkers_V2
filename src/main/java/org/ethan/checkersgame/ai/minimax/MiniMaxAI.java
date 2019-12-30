package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.ai.AI;
import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;
import org.ethan.checkersgame.logic.enums.PieceType;
import org.ethan.checkersgame.logic.enums.PlayerColor;

import static org.ethan.checkersgame.features.FeaturesList.ALPHA_BETA_PRUNING;

public class MiniMaxAI extends AI
{
    protected int depth;

    public MiniMaxAI(PlayerColor color)
    {
        super(color);

        depth = 6; // set to 6
    }

    @Override
    public Move chooseMove(GameStateManager stateManager)
    {
        GameStateManager initialState = new GameStateManager(stateManager);

        MiniMaxRetVal minimaxResult = miniMaxRecurse(initialState, depth, aiColor, Double.MIN_VALUE, Double.MAX_VALUE);

        System.out.println("nodes traversed: " + minimaxResult.getNumNodesTraversedForDebug());

        return minimaxResult.getMove();
    }

    /**
     * on turn, choose max. on opponent's turn, choose min
     *
     * @param gameState
     * @param remainingDepth
     * @param colorOfTurn
     * @param currentMaximizingBest current "best" score for maximizing player (alpha)
     * @param currentMinimizingBest current "best" score for minimizing player (beta)
     * @return The idea of currentMaximizingBest currentMinimizingBest pruning is that the maximizing player won't choose a node lower than it's current best,
     * and vice verse for the minimizing player
     * <p>
     * For example:
     * - we are at a minimizing node and our current min is 4
     * - the parent passes in currentMaximizingBest of 5
     * - this means that the parent WILL NOT choose the path of this node
     * - at this point we can stop computing
     */
    private MiniMaxRetVal miniMaxRecurse(GameStateManager gameState, int remainingDepth, PlayerColor colorOfTurn, double currentMaximizingBest, double currentMinimizingBest)
    {
        if ( remainingDepth == 0 || gameState.isStatemate() || gameState.getWinColor() != PlayerColor.NONE )
        {
            return new MiniMaxRetVal(getRewardAssociatedWithState(gameState), null, 1);
        }

        final boolean isMaximizing = colorOfTurn == aiColor;

        double curBestReward = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move curBestMove = null;

        int numNodesTraversedForDebug = 0;

        for ( Move possibleMove : gameState.getAllPossibleMoves() )
        {
            GameStateManager childState = new GameStateManager(gameState);
            childState.makeMove(possibleMove);

            final MiniMaxRetVal miniMaxRetVal = miniMaxRecurse(childState,
                    remainingDepth - 1,
                    getOppositeColor(colorOfTurn),
                    currentMaximizingBest,
                    currentMinimizingBest);
            final double reward = miniMaxRetVal.getReward();

            numNodesTraversedForDebug += miniMaxRetVal.getNumNodesTraversedForDebug();

            if ( isMaximizing )
            {
                if ( reward > curBestReward )
                {
                    curBestReward = reward;
                    curBestMove = possibleMove;

                    currentMaximizingBest = Math.max(currentMaximizingBest, curBestReward);

                    if ( ALPHA_BETA_PRUNING )
                    {
                        // at this point, the parent MINIMIZING node will never pick this node
                        if ( currentMaximizingBest >= currentMinimizingBest )
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                if ( reward < curBestReward )
                {
                    curBestReward = reward;
                    curBestMove = possibleMove;

                    currentMinimizingBest = Math.min(currentMinimizingBest, curBestReward);

                    if ( ALPHA_BETA_PRUNING )
                    {
                        // at this point, the parent MAXIMIZING node will never pick this node
                        if ( currentMaximizingBest >= currentMinimizingBest )
                        {
                            break;
                        }
                    }
                }
            }


        }

        return new MiniMaxRetVal(curBestReward, curBestMove, numNodesTraversedForDebug);
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

    protected double getRewardAssociatedWithState(GameStateManager stateManager)
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
