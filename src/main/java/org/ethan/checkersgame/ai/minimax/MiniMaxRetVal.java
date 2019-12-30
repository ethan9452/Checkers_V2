package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.logic.Move;

public class MiniMaxRetVal
{
    private final double reward;
    private final Move move;

    private final int numNodesTraversedForDebug;

    public MiniMaxRetVal(double reward, Move move, int numNodesTraversedForDebug)
    {
        this.reward = reward;
        this.move = move;
        this.numNodesTraversedForDebug = numNodesTraversedForDebug;
    }

    public double getReward()
    {
        return reward;
    }

    public Move getMove()
    {
        return move;
    }

    public int getNumNodesTraversedForDebug()
    {
        return numNodesTraversedForDebug;
    }
}
