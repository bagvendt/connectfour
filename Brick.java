public class Brick {
	private int column;
	private int row;
	private IGameLogic.Winner player;
	private Board board;
	

	/**
	 * @param column
	 * @param player
	 * @param board
	 */
	public Brick(IGameLogic.Winner player) {	
		this.player = player;
	}

	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public int getRow() {
		return row;
	}


	public void setRow(int row) {
		this.row = row;
	}


	public IGameLogic.Winner getPlayer() {
		return player;
	}

	public void setPlayer(IGameLogic.Winner player) {
		this.player = player;
	}


	public Board getBoard() {
		return board;
	}
	
	

}
