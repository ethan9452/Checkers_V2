package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.logic.Move;

public class MiniMaxRetVal
{
    private final int reward;
    private final Move move;


    public MiniMaxRetVal(int reward, Move move)
    {
        this.reward = reward;
        this.move = move;
    }

    public int getReward()
    {
        return reward;
    }

    public Move getMove()
    {
        return move;
    }
}
