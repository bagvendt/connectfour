import java.util.ArrayList;

public class Board {
	
	private ArrayList<Brick>[] board;
	private ArrayList<Brick> bricks;
	private int maxHeight;
	
	
	
	/**
	 * @param board
	 */
	public Board(int columns, int rows) {
		board = new ArrayList[columns];
		bricks = new ArrayList<Brick>();
		maxHeight = rows;
	}

	public void layBrick(Brick brick, int atColumn) {
		ArrayList<Brick> column = board[atColumn];
		int height = column.size();
		// Column is zero indexed by design, so better make the row zero indexed too.
		brick.setRow(height -1 );
		brick.setColumn(atColumn);
		bricks.add(brick);
	}
	
	public IGameLogic.Winner goalState() {
		for (Brick b: bricks) {
			//
			
		}
		return IGameLogic.Winner.PLAYER1;
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
	
	
	

}
