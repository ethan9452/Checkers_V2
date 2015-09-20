import java.awt.Point;
	/*
	 * Represents a checkers move
	 */
public class Move {
	
	private Point start_pos; // position of the piece to be moved
	private Point end_pos; // position the piece of interest ends at
	
	public Move(Point start, Point end){
		start_pos = start;
		end_pos = end;
	}
	
	public Point getStartPos(){
		return start_pos;
	}
	
	public Point getEndPos(){
		return end_pos;
	}

}
