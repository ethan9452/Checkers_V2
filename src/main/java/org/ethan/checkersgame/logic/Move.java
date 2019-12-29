package org.ethan.checkersgame.logic;

import java.awt.Point;

/*
 * Represents a checkers move
 */
public class Move
{

    private final Point startPos; // position of the piece to be moved
    private final Point endPos; // position the piece of interest ends at

    public Move(Point start, Point end)
    {
        startPos = start;
        endPos = end;
    }

    public Point getStartPos()
    {
        return startPos;
    }

    public Point getEndPos()
    {
        return endPos;
    }
}
