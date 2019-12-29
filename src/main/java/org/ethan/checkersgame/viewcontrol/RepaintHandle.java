package org.ethan.checkersgame.viewcontrol;

public class RepaintHandle
{
    private final UserInterface ui;

    public RepaintHandle(UserInterface ui)
    {
        this.ui = ui;
    }

    public void triggerRepaint()
    {
        ui.repaint();
    }
}
