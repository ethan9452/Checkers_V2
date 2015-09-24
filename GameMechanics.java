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

	Point selected_piece = new Point(-1, -1);
	
	String message = ""; // message displayed to player

	// Constructor
	public GameMechanics(){
		this.board = new GameState();
	}
	
	
	// Recieves the board space that the user clicked on (between [0,0] to [7,7]
	// and acts accordingly
	public void recieveClick(Point coords){  //TODO
		// check to see if click is in board, eg 8,8 just gets ignored
		if(coords.getX() < 0 || coords.getX() > 7 || coords.getY() < 0 || coords.getY() > 7){
			return;
		}
		
		// if piece is not selected, and the color is the same as the turn, set that piece to be 'selected_piece'
		if(!coords.equals(selected_piece) && ((this.getBoard()[(int)coords.getX()][(int)coords.getY()] < 0) 
				&& this.board.getTurn() == board.red || ((this.getBoard()[(int)coords.getX()][(int)coords.getY()]
						> 0) && this.board.getTurn() == board.black))){ 
			selected_piece.setLocation(coords);
		}
		
		// if piece is selected, unselect
		if(coords.equals(selected_piece)){
			selected_piece.setLocation(-1, -1);
		}
		
		// if piece is selected and valid move is chosen, make the move
		Move[] possible_moves = board.getPossibleMoves(selected_piece); // get all moves for the selected piece
		for(int i = 0; i < possible_moves.length; i++){ // loop thru all possible moves
			if(possible_moves[i].getEndPos().equals(coords)){ // if clicked space matches valid move, make the move
				makeMove(possible_moves[i]);
			}
		}
		

	}
	
	// Given a move, alters the board accordingly
	public void makeMove(Move move){
		int piece = getBoard()[(int)move.getStartPos().getX()][(int)move.getStartPos().getY()];
		board.setSpace((int)move.getEndPos().getX(), (int)move.getEndPos().getY(), piece);
		board.setSpace((int)move.getStartPos().getX(), (int)move.getStartPos().getY(), 0);
		
		// Check if move is a jump
		if(Math.abs(move.getEndPos().getX() - move.getStartPos().getX()) == 2){
			// remove jumped piece
			int jumpedX = (int)((move.getEndPos().getX() + move.getStartPos().getX())/2);
			int jumpedY = (int)((move.getEndPos().getY() + move.getStartPos().getY())/2);
			board.setSpace(jumpedX, jumpedY, 0);
			
			board.setJumpInProgress(true);
			board.setJumpingPiece(move.getEndPos());
			
			board.flipTurn(); // If a piece is jumped, don't change turns, so we premtively undo the flip-turn that happens later.
			
			// If the jumping piece has no more moves, jump is NO LONGER in progress
			if(board.getPossibleMoves(board.getJumpingPiece()).length == 0){
				board.setJumpInProgress(false);
				board.flipTurn(); // If jump is over, we need to change turns. (3 flips equals 1 flip)
			}
		}
		board.flipTurn();
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
	public String getMessage(){
		return message;
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
