package org.ethan.checkersgame.ai;

import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;
import org.ethan.checkersgame.logic.enums.PlayerColor;

public abstract class AI
{
    protected final PlayerColor aiColor;

    protected AI(PlayerColor aiColor)
    {
        this.aiColor = aiColor;
    }

    public abstract  Move chooseMove(GameStateManager stateManager);
}
