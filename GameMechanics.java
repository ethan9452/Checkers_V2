import java.awt.Point;

/*
 * Responsible for executing mechanics of game (move piece, convert to king, etc)
 * 
 *  Takes in a click from UserInterface
 * 
 */
public class GameMechanics {
	GameState board;
	boolean blackAI = false;
	boolean redAI = false;


	// Constructor
	public GameMechanics(){
		this.board = new GameState();
	}
	
	
	// Recieves the board space that the user clicked on (between [0,0] to [7,7]
	// and acts accordingly
	public void recieveClick(Point coords){  //TODO
		// check to see if click is in board, eg 8,8 just gets ignored
		
		// check to see if a piece is selected or not,....
	}
	
	// Given a move, alters the board accordingly
	public void makeMove(Move move){
		// TODO
	}
	
	// AI methods
	// If both are AI, these 2 methods loop back and forth
	// If not, the method ends, and we wait for mouse input from user
	public void blackAI(){  //TODO
		// make some move
		if(redAI == true){
			redAI();
		}
	}
	public void redAI(){  //TODO
		//make some move
		if(blackAI == true){
			blackAI();
		}
	}


	// Getters and Setters
	public boolean isBlackAI() {
		return blackAI;
	}
	public void setBlackAI(boolean blackAI) {
		this.blackAI = blackAI;
	}
	public boolean isRedAI() {
		return redAI;
	}
	public void setRedAI(boolean redAI) {
		this.redAI = redAI;
	}
	public int[][] getBoard(){
		return this.board.getBoard();
	}


	// Debug
	public void printBoard(){
		for(int y = 0; y < 8; y++){
			for(int x = 0; x < 8; x++){
				System.out.print(board.getBoard()[x][y]);
			}
			System.out.println();
		}
	}

}
