package org.ethan.checkersgame;

import org.ethan.checkersgame.viewcontrol.UserInterface;

import javax.swing.JFrame;

public class Main extends JFrame
{

    public Main()
    {
        setSize( 500, 600 );
        setVisible( true );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setResizable( false );
        setTitle( "Checkers " );
        add( new UserInterface() );
        validate();
    }

    public static void main(String[] args)
    {
        Main display = new Main();

    }


}
