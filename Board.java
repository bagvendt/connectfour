import java.util.ArrayList;
import java.util.*;

public class Board implements Cloneable {
	
	private ArrayList<Brick>[] board;
	private ArrayList<Brick> bricks;
	private int height, length;
	
	/**
	 * @param board
	 */
	public Board(int columns, int rows) {
		length = columns;
		height = rows;
		board = new ArrayList[columns];
		for (int i = 0; i < board.length; i++) {
			board[i] = new ArrayList<Brick>();
		}
		bricks = new ArrayList<Brick>();
	}
	 public Object clone(){
		  try{
			  Board cloned = (Board)super.clone();
			  return cloned;
		  }
		  catch(CloneNotSupportedException e){
			  System.out.println(e);
		  return null;
		  }
		  }
	public void layBrick(Brick brick, int atColumn) {
		ArrayList<Brick> column = board[atColumn];
		column.add(brick);
		int height = column.size();
		// Column is zero indexed by design, so better make the row zero indexed too.
		brick.setRow(height);
		brick.setColumn(atColumn);
		bricks.add(brick);
	}
	
	
	public Brick getBrickRelative(Brick brick, Compass direction) {
		int col = brick.getColumn();
		int row = brick.getRow();
		
		switch (direction) {
		case N:
			row = row +1;
			break;
		case NE:
			row = row + 1;
			col = col + 1;
			break;
		case E:
			col = col + 1;
			break;
		case SE:
			row = row + 1;
			col = col - 1;
			break;
		case S:
			row = row - 1;
			break;
		case SW:
			row = row + 1;
			col = col + 1;
			break;
		case W:
			col = col + 1;
			break;
		case NW:
			row = row + 1;
			col = col - 1;
			break;
		}
		if (col > height || 0 > row) {
			return null;
		}
		if (row > length || 0 > row) {
			return null;
		}
		ArrayList<Brick> theCol = board[col];
		if (row > theCol.size()) {
			return null;
		}
		return theCol.get(col);
	}

	public IGameLogic.Winner gameFinished() {
		// TODO Auto-generated method stub
		for (Brick b : bricks) {
			
		}
		return IGameLogic.Winner.NOT_FINISHED;
	}
	
	public Boolean checkWinner(Brick brick) {
		for(Compass direction: Compass.values()) {
			Brick brick1 = getBrickRelative(brick, direction);
			Brick brick2 = getBrickRelative(brick1, direction);
			Brick brick3 = getBrickRelative(brick2, direction);
			if (brick.getPlayer() == brick1.getPlayer() && brick1.getPlayer() == brick2.getPlayer() && brick2.getPlayer() == brick3.getPlayer()) {
				return true;
			}
		}
		return false;
	}

	public int evalute() {
		return 0;
		
	}

	public Set<Integer> actions() {
		Set<Integer> intSet = new HashSet<Integer>(length); 
		int column;
		for (Brick brick : bricks){
			column = brick.getColumn();
			for(int j = -3; j <= 3; j++){
				if (column+j > 0 && column+j < length && board[column+j].size() != height)
					intSet.add(column+j);
			}
		}
		if (intSet.size() == 0) intSet.add(length/2);
		
		return intSet;
	}
	
	
	

}
