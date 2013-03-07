import java.util.ArrayList;
import java.util.*;

public class Board implements Cloneable {
	
	private ArrayList<Brick>[] board;
	private ArrayList<Brick> bricks;
	/**
	 * @param board
	 */
	public Board(int columns, int rows) {
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
		//Brick b = board[-1][-1];
		return null;
	}

	public IGameLogic.Winner gameFinished() {
		// TODO Auto-generated method stub
		for (Brick b : bricks) {
			
		}
		return IGameLogic.Winner.NOT_FINISHED;
	}
	
	public IGameLogic.Winner checkWinner(Brick brick) {
		for(Compass direction: Compass.values()) {
			Brick brick1 = getBrickRelative(brick, direction);
			Brick brick2 = getBrickRelative(brick1, direction);
			Brick brick3 = getBrickRelative(brick2, direction);
			if (brick.getPlayer() == brick1.getPlayer() && brick1.getPlayer() == brick2.getPlayer() && brick2.getPlayer() == brick3.getPlayer()) {
				return brick.getPlayer();
			}
			return IGameLogic.Winner.NOT_FINISHED;
		}
	}

	public int evalute() {
		return 0;
		
	}

	public Set<Integer> actions() {
		Set<Integer> intSet = new HashSet<Integer>(board.length); 
		
		for (int i = 0; i < board.length; i++){
			if (!board[i].isEmpty()){
			
				for(int j = -3; j <= 3; j++){
					if (i+j > 0 && i+j < board.length-1 && board[i+j].size() != board[i+j].height)
						intSet.add(i+j);
				}	
			}
		}
		if (intSet.size() == 0) intSet.add(board.length/2);
		
		return intSet;
	}
	
	
	

}
