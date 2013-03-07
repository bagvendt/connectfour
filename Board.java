import java.util.ArrayList;

public class Board {
	
	private ArrayList<Brick>[] board;
	private ArrayList<Brick> bricks;
	public int Height; 
	public int Length;
	/**
	 * @param board
	 */
	public Board(int columns, int rows) {
		Height = rows;
		Length = rows;
		//KUSSE
		board = new ArrayList[columns];
		for (int i = 0; i < board.length; i++) {
			board[i] = new ArrayList<Brick>();
		}
		bricks = new ArrayList<Brick>();
	}
	
	public int EvaluateBoard(IGameLogic.Winner player) 
	{
		int h = 0;
		
		for (Brick b : bricks) 
		{
			int col = b.getColumn();
			int row = b.getRow();
			
			// W
			if (col > 2) 
			{
				if (GetNeighbours(row, col, -1,0, player) == 4)
					h++;
			}
			
			// NW
			if (col > 2 && row < Height-4) 
			{
				if (GetNeighbours(row, col, -1, 1, player) == 4)
					h++;
			}
			
			// N
			if (row < Height-4) 
			{
				if (GetNeighbours(row, col, 0, 1, player) == 4)
					h++;
			}
			
			// NE
			if (row < Height-4 && col < Length-4) 
			{
				if (GetNeighbours(row, col, 1, 1, player) == 4)
					h++;
			}
			
			// E
			if (col < Length-4) 
			{
				if (GetNeighbours(row, col, 1, 0, player) == 4)
					h++;
			}
			
			// SE
			if (row > 2 && col < Length-4) 
			{
				if (GetNeighbours(row, col, -1, 1, player) == 4)
					h++;
			}
			
			// S
			if (row > 2) 
			{
				if (GetNeighbours(row, col, -1, 0, player) == 4)
					h++;
			}
			
			// SW
			if (row > 2 && col > 2) 
			{
				if (GetNeighbours(row, col, -1, -1, player) == 4)
					h++;
			}
		}	
		return 0;
	}
	
	private int GetNeighbours(int playerRow, int playerCol, int horizontalIncrementer, int verticalIncrementer, IGameLogic.Winner player) 
	{
		int neighbours = 0;
		for (int i = 1; i < 4; i++) 
		{
			int colIndex = playerCol + i * horizontalIncrementer;
			int rowIndex = playerRow + i * verticalIncrementer;
			if (board[colIndex].size() > rowIndex && board[colIndex].get(rowIndex).getPlayer() != player)
				break;
			neighbours++;
		}
		return neighbours;
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
	
	
	

}
