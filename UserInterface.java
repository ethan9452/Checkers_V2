import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.Timer;


/*
 * This class is responsible for handling all user input, and displaying to the user.
 * All the code for the user interface portion of the game.
 * 
 * All the info it gets comes from 'GameMechanics,' only talks to GameMechanics
 */
public class UserInterface extends JPanel implements MouseListener, ActionListener{

	GameMechanics mechanics;

	Timer timer;

	// Constructor
	public UserInterface(){
		addMouseListener(this);
		setFocusable(true);
		timer = new Timer(1000, this);

		mechanics = new GameMechanics();

		repaint();
	}


	//// Painting ////
	public void paint(Graphics g){
		super.paint(g);
		paintBoard(g);
		paintPieces(g);
	}
	public void paintBoard(Graphics g){
		g.setColor(Color.lightGray);
		g.fillRect(50,40, 400, 400);
		g.setColor(Color.gray);

		boolean dark = false;

		// 50 x 50 squares
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(dark){
					g.fillRect(i * 50 + 50, j * 50 + 40, 50, 50);
				}
				dark = !dark;
			}
			dark = !dark;
		}
	}
	public void paintPieces(Graphics g){
		/*  0 : empty
		 *  1 : black man
		 *  2 : black king
		 * -1 : red man
		 * -2 : red king */
		int[][] board = mechanics.getBoard();
		for(int x = 0; x < 8; x++){
			for(int y = 0; y < 8; y++){
				if(board[x][y] != 0){ // Ignore empty space
					// Choose Color
					if(board[x][y] > 0){
						g.setColor(Color.black);
					}
					else{
						g.setColor(Color.red);
					}
					// Paint Piece
					int drawX = (int)spaceToPixel(x,y).getX() + 5;
					int drawY= (int)spaceToPixel(x,y).getY() + 5;
					g.fillOval(drawX, drawY, 40, 40);
					
					//King?
					if(Math.abs(board[x][y]) == 2){
						g.drawString("K", drawX+ 5, drawY + 5);
					}
				}
			}
		}
	}


	//// Mouse Input ////
	public void mouseClicked(MouseEvent e) {		
	}

	public void mousePressed(MouseEvent e) {
		Point boardSpace = pixelToSpace(e.getX(), e.getY());	
		mechanics.recieveClick(boardSpace);
		repaint();
	}

	public void mouseReleased(MouseEvent e) {		
	}

	public void mouseEntered(MouseEvent e) {		
	}

	public void mouseExited(MouseEvent e) {		
	}


	// Coordinate Conversion
	public Point pixelToSpace(int x, int y){
		int retX = (x - 50)/50;
		int retY = (y - 40)/50;
		Point ret = new Point(retX, retY);
		if(x < 50 || y < 40){
			ret.setLocation(-69, -69);
		}
		return ret;
	}
	public Point spaceToPixel(int x, int y){ //  returns top left corner of square
		int retX = (x * 50) + 50;
		int retY = (y * 50) + 40;
		return new Point(retX, retY);
	}


	// Timer
	public void actionPerformed(ActionEvent e) {
		repaint();
		
	}
}
