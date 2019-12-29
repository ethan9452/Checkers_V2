package org.ethan.checkersgame.ai;

import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;

public interface AI
{
    public Move chooseMove(GameStateManager stateManager);
}
