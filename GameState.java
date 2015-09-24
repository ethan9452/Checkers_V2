import java.awt.Point;
import java.util.ArrayList;

/*
	 * This class represents a checkers board: the piece positions and state of the 
	 * board (whose turn, must jump, etc).
	 * 
	 * The information given by this class must be enough to choose a legal move.
	 *
	 * The info here stays the same regardless of if we have AI or no AI, AI is GameMechanics's problem
	 *
	 * This class should return possible moves to GameMechanics class
	 */
public class GameState {
	
	// Constants
	final public boolean black = true; // Used to represent whose turn it is
	final public boolean red = false;
	
	// Variables
	private int[][] board; /* represents the checker board coordinates
				     	  Board looks like:
						   0 1 2 3 4 5 6 7
						   ________________
						0 | 0 0 0 0 0 0 0 0 
						1 | 0 0 0 0 0 0 0 0 
						2 | 0 0 0 0 0 0 0 0 
						3 | 0 0 0 0 0 0 0 0 
						4 | 0 0 0 0 0 0 0 0 
						5 | 0 0 0 0 0 0 0 0 
						6 | 0 0 0 0 0 0 0 0 
						7 | 0 0 0 0 0 0 0 0 
				     
				     * Pieces are represented as follows:
				     *  0 : empty
				     *  1 : black man
				     *  2 : black king
				     * -1 : red man
				     * -2 : red king 
				     */
	private boolean turn; // whose turn it is, use constants 'black' and 'red' for simplicity (instead of true, false)
	private boolean jumpInProgress = false; // set to true when a piece jumps, so we know to stay on current players turn
	private Point jumpingPiece = new Point(-1, -1); // set to the coordinates of the last piece that jumped, if that piece has no more jumps, we know to end the players turn
	
	
	// Constructor
	public GameState(){
		this.board = new int[8][8];
		this.turn = black;
		this.resetBoard();
	}

	
	// Function to get possible moves: Given a piece, what are it's possible moves
	public Move[] getPossibleMoves(Point coords){ 
		ArrayList<Move> moves = new ArrayList<Move>();
		
		// conditions
		/*
		 * whose turn
		 * color
		 * own pieces blocking
		 * other pieces blocking
		 * 	 pieces blocking other pieces blocking
		 * king or not
		 * edge of board
		 * */
		
		
		/* Helpers: 
		 * spaceOccupied, isKing
		 */
		
		
		return (Move[]) moves.toArray();
	}
	private boolean spaceOccupied(Point coord){
		if(board[(int)coord.getX()][(int)coord.getY()] != 0){
			return true;
		}
		else{
			return false;
		}
	}
	private boolean spaceOccupied(int x, int y){
		if(board[x][y] != 0){
			return true;
		}
		else{
			return false;
		}
	}
	private boolean isKing(Point coord){
		if(board[(int)coord.getX()][(int)coord.getY()] == 2 || board[(int)coord.getX()][(int)coord.getY()] == -2){
			return true;
		}
		else{
			return false;
		}
	}
	private boolean isKing(int x, int y){
		if(board[x][y] == 2 || board[x][y] == -2){
			return true;
		}
		else{
			return false;
		}
	}
	

	
	// Getters and setters
	public int[][] getBoard() {
		return board;
	}
	public void setSpace(int x, int y, int piece) {
		this.board[x][y] = piece;
	}
	public void resetBoard(){
		for(int i = 0; i < 4; i++){
			this.board[i * 2 + 1][0] = -1;
		}
		for(int i = 0; i < 4; i++){ 
			this.board[i * 2][1] = -1;
		}
		for(int i = 0; i < 4; i++){ 
			this.board[i * 2 + 1][2] = -1;
		}
		for(int i = 0; i < 4; i++){
			this.board[i * 2][5] = 1;
		}
		for(int i = 0; i < 4; i++){
			this.board[i * 2 + 1][6] = 1;
		}
		for(int i = 0; i < 4; i++){
			this.board[i * 2][7] = 1;
		}	
	}

	public boolean getTurn() {
		return turn;
	}


	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	public void flipTurn(){
		this.turn = !this.turn;
	}
	public void flipJumpInProgress(){
		this.jumpInProgress = !this.jumpInProgress;
	}
	public void setJumpInProgress(boolean j){
		this.jumpInProgress = j;
	}
	public Point getJumpingPiece() {
		return jumpingPiece;
	}
	public void setJumpingPiece(Point jumpingPiece) {
		this.jumpingPiece.setLocation(jumpingPiece);
	}
	public void setJumpingPiece(int x, int y) {
		this.jumpingPiece.setLocation(x, y);
	}
	
	
	
	
	
	
	
	

}
