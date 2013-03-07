/**
 * 
 */

/**
 * @author bagvendt
 *
 */
public class LRM_GameLogic implements IGameLogic {

	/* (non-Javadoc)
	 * @see IGameLogic#initializeGame(int, int, int)
	 */

	private Board gameBoard;
	private int ourPlayerID;
	private int enemyPlayerID;
	@Override
	public void initializeGame(int columns, int rows, int player) {
		gameBoard = new Board(columns,rows);
		ourPlayerID = player;
		enemyPlayerID = player == 1 ? 2 : 1; 
	}

	/* (non-Javadoc)
	 * @see IGameLogic#insertCoin(int, int)
	 */
	@Override
	public void insertCoin(int column, int playerID) {
		Brick brick = new Brick(playerID);
		gameBoard.layBrick(brick, column);	
	}

	/* (non-Javadoc)
	 * @see IGameLogic#decideNextMove()
	 */
	@Override
	public int decideNextMove() {
		// TODO Auto-generated method stub
		return 1;
	}

	/* (non-Javadoc)
	 * @see IGameLogic#gameFinished()
	 */
	@Override
	public Winner gameFinished() {
		// TODO Auto-generated method stub
		return IGameLogic.Winner.NOT_FINISHED;
	}

}
