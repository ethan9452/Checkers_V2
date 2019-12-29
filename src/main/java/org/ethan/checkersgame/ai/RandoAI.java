package org.ethan.checkersgame.ai;

import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;
import org.ethan.checkersgame.logic.enums.PlayerColor;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.ethan.checkersgame.logic.GameStateManager.BOARD_X_LENGTH;
import static org.ethan.checkersgame.logic.GameStateManager.BOARD_Y_LENGTH;

public class RandoAI implements AI
{
    private final PlayerColor color;

    public RandoAI(PlayerColor color)
    {
        this.color = color;
    }

    @Override
    public Move chooseMove(GameStateManager stateManager)
    {
        List<Move> allPossibleMoves = new ArrayList<>();

        Point reUsedPoint = new Point();
        for ( int x = 0; x < BOARD_X_LENGTH; x++ )
        {
            for ( int y = 0; y < BOARD_Y_LENGTH; y++ )
            {
                reUsedPoint.setLocation(x, y);
                List<Move> possibleMoves = stateManager.getPossibleMoves(reUsedPoint);
                allPossibleMoves.addAll(possibleMoves);
            }
        }

        if ( allPossibleMoves.isEmpty() )
        {
            throw new IllegalStateException("AI possible moves empty");
        }

        Random random = new Random();
        final int randIdx = random.nextInt(allPossibleMoves.size());

        return allPossibleMoves.get(randIdx);
    }
}
