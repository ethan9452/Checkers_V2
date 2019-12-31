package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;
import org.ethan.checkersgame.logic.enums.PieceType;
import org.ethan.checkersgame.logic.enums.PlayerColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MiniMaxV3AI extends MiniMaxAI
{
    private static double KING_WEIGHT = 4;
    private static double MAN_WEIGHT = 3;

    private final Map<String, Double> rewardComputationCache = new HashMap<>(100000);

    private int cacheHitForDebug = 0;
    private int cacheMissForDebug = 0;

    public MiniMaxV3AI(PlayerColor color)
    {
        super(color);

        depth = 9;
    }

    public MiniMaxV3AI(PlayerColor color, int depth)
    {
        super(color);

        this.depth = depth;
    }

    @Override
    public Move chooseMove(GameStateManager stateManager)
    {
        Move move = super.chooseMove(stateManager);

        double cacheHitMissRatio = cacheMissForDebug == 0 ? 0. : (double) cacheHitForDebug / cacheMissForDebug;
        System.out.println(aiColor + " " + this.getClass().getSimpleName() + " cache hit/miss ratio " + cacheHitForDebug + "/" + cacheMissForDebug + " = " + cacheHitMissRatio);

        return move;
    }

    protected String getCacheLookupKey(GameStateManager stateManager)
    {
        StringBuilder keyBuilder = new StringBuilder();

        // this alg only uses the board to determine reward, not other factors like turn number, whos turn it is, etc..
        for ( int x = 0; x < stateManager.getBoard().length; x++ )
        {
            for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
            {
                keyBuilder.append(stateManager.getBoard()[x][y]);
            }
        }

        return keyBuilder.toString();
    }

    protected double getRewardAssociatedWithState(GameStateManager stateManager)
    {
        final String cacheLookupKey = getCacheLookupKey(stateManager);

        if ( rewardComputationCache.containsKey(cacheLookupKey) )
        {
            cacheHitForDebug++;

            return rewardComputationCache.get(cacheLookupKey);
        }
        else
        {
            cacheMissForDebug++;

            double piecesWeight = 0;
            double enemyPiecesWeight = 0;

            for ( int x = 0; x < stateManager.getBoard().length; x++ )
            {
                for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
                {
                    if ( stateManager.getPieceType(x, y) != PieceType.NONE )
                    {
                        final double middlePreferenceHeuristicWeight = getMiddlePreferenceHeuristicWeight(stateManager, x);

                        if ( stateManager.getPlayerColor(x, y) == aiColor )
                        {
                            if ( stateManager.getPieceType(x, y) == PieceType.KING )
                            {
                                piecesWeight += KING_WEIGHT + middlePreferenceHeuristicWeight;
                            }
                            else
                            {
                                piecesWeight += MAN_WEIGHT + middlePreferenceHeuristicWeight;
                            }
                        }
                        else
                        {
                            if ( stateManager.getPieceType(x, y) == PieceType.KING )
                            {
                                enemyPiecesWeight += KING_WEIGHT + middlePreferenceHeuristicWeight;
                            }
                            else
                            {
                                enemyPiecesWeight += MAN_WEIGHT + middlePreferenceHeuristicWeight;
                            }
                        }
                    }
                }
            }

            if ( enemyPiecesWeight <= 0 )
            {
                rewardComputationCache.put(cacheLookupKey, Double.MAX_VALUE);
                return Double.MAX_VALUE;
            }
            else
            {
                final double individualPiecesReward = piecesWeight / enemyPiecesWeight;

                final double overallBoardReward = getFinishTheJobHeuristicWeight(stateManager);

                rewardComputationCache.put(cacheLookupKey, individualPiecesReward + overallBoardReward);
                return individualPiecesReward + overallBoardReward;
            }
        }
    }

    private int getNumPlayersPieces(GameStateManager stateManager)
    {
        int numPlayersPieces = 0;
        for ( int x = 0; x < stateManager.getBoard().length; x++ )
        {
            for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
            {
                if ( stateManager.getPlayerColor(x, y) == aiColor )
                {
                    numPlayersPieces++;
                }
            }
        }
        return numPlayersPieces;
    }

    private int getNumPlayersKings(GameStateManager stateManager)
    {
        int numPlayersKings = 0;
        for ( int x = 0; x < stateManager.getBoard().length; x++ )
        {
            for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
            {
                if ( stateManager.getPlayerColor(x, y) == aiColor && stateManager.getPieceType(x, y) == PieceType.KING )
                {
                    numPlayersKings++;
                }
            }
        }
        return numPlayersKings;
    }

    private int getNumEnemyPieces(GameStateManager stateManager)
    {
        int numPlayersPieces = 0;
        for ( int x = 0; x < stateManager.getBoard().length; x++ )
        {
            for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
            {
                if ( stateManager.getPlayerColor(x, y) == getOppositeColor(aiColor) )
                {
                    numPlayersPieces++;
                }
            }
        }
        return numPlayersPieces;
    }

    private double getMiddlePreferenceHeuristicWeight(GameStateManager stateManager, int x)
    {
        // i think this rule would only help in the early game
        if ( getNumPlayersPieces(stateManager) >= 8 )
        {
            // this is between 3.5 and .5
            final double distFromMiddle = Math.abs(3.5 - x);

            // I think this weight should be kept small, so it doesn't override the
            // more important (piece / enemy piece) ratio reward component
            final double weight = -distFromMiddle / 10;

            return weight;
        }
        else
        {
            return 0;
        }
    }

    /**
     * from empeircal observation, the previous AIs have trouble "finishing the job" so to speak.
     * this weight tries to fix this
     *
     * @param stateManager
     * @return
     */
    private double getFinishTheJobHeuristicWeight(GameStateManager stateManager)
    {
        final double pieceRatio = (double) getNumPlayersPieces(stateManager) / getNumEnemyPieces(stateManager);

        // if we're dominating in pieces, we should try a 2 phase strategy
        // 1. promote all pieces to kings
        // 2. try to move our pieces closer to enemy
        if ( pieceRatio > 1.8 )
        {
            // This value should range from 2 to 12
            final int numKings = getNumPlayersKings(stateManager);
            final double numKingsReward = (double) numKings / 5.;

            // this value should range from 0 to 5
            final double avgDistOfPieces = getAverageDistFromMidpoint(stateManager);
            final double avgDistReward = avgDistOfPieces / 5.;


            return numKingsReward + avgDistReward;
        }
        else
        {
            return 0.;
        }
    }

    /**
     * This should theoretically be in a range from 0 to sqrt( 3.5^2 + 3.5^2 ) => 5
     *
     * @param stateManager
     * @return
     */
    private double getAverageDistFromMidpoint(GameStateManager stateManager)
    {
        int numPieces = 0;
        int sumX = 0;
        int sumY = 0;

        final List<Integer> xCoords = new ArrayList<>();
        final List<Integer> yCoords = new ArrayList<>();


        for ( int x = 0; x < stateManager.getBoard().length; x++ )
        {
            for ( int y = 0; y < stateManager.getBoard()[0].length; y++ )
            {
                if ( stateManager.getPieceType(x, y) != PieceType.NONE )
                {
                    numPieces++;
                    sumX += x;
                    sumY += y;
                    xCoords.add(x);
                    yCoords.add(y);
                }
            }
        }

        final double midpointX = (double) sumX / numPieces;
        final double midpointY = (double) sumY / numPieces;

        double sumDistFromMidX = 0;
        double sumDistFromMidY = 0;
        for ( int i = 0; i < xCoords.size(); i++ )
        {
            sumDistFromMidX += Math.abs(xCoords.get(i) - midpointX);
            sumDistFromMidY += Math.abs(yCoords.get(i) - midpointY);
        }

        final double avgDistFromMidX = sumDistFromMidX / numPieces;
        final double avgDistFromMidY = sumDistFromMidY / numPieces;

        return Math.sqrt(avgDistFromMidX * avgDistFromMidX + avgDistFromMidY * avgDistFromMidY);
    }
}
