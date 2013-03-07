import java.util.ArrayList;

public class Board {
	
	private ArrayList<Brick>[] board;
	private ArrayList<Brick> bricks;
	/**
	 * @param board
	 */
	public Board(int columns, int rows) {
		//KUSSE
		board = new ArrayList[columns];
		for (int i = 0; i < board.length; i++) {
			board[i] = new ArrayList<Brick>();
		}
		bricks = new ArrayList<Brick>();
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

	public IGameLogic.Winner gameFinished() {
		// TODO Auto-generated method stub
		return IGameLogic.Winner.NOT_FINISHED;
	}
	
	
	

}
