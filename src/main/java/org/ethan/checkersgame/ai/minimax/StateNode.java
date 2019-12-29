package org.ethan.checkersgame.ai.minimax;

import org.ethan.checkersgame.logic.GameStateManager;
import org.ethan.checkersgame.logic.Move;

import java.util.LinkedHashMap;
import java.util.Map;

public class StateNode
{
    private final GameStateManager state;

    private final Map<Move, StateNode> children;

    public StateNode(GameStateManager state)
    {
        this.state = state;
        this.children = new LinkedHashMap<>();
    }

    public GameStateManager getState()
    {
        return state;
    }

    public Map<Move, StateNode> getChildren()
    {
        return children;
    }
}
