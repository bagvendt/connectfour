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
	private IGameLogic.Winner ourPlayer;
	private IGameLogic.Winner enemyPlayer;
	private int decision;
	private int decisionDepth;
	
	@Override
	public void initializeGame(int columns, int rows, int player) {
		gameBoard = new Board(columns,rows);
		ourPlayer = player == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		enemyPlayer = player == 2 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
	}

	/* (non-Javadoc)
	 * @see IGameLogic#insertCoin(int, int)
	 */
	@Override
	public void insertCoin(int column, int playerID) {
		IGameLogic.Winner thePlayer = playerID == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		gameBoard.layBrick(column, thePlayer);	
	}

	/* (non-Javadoc)
	 * @see IGameLogic#decideNextMove()
	 */
	@Override
	public int decideNextMove() {
		
		long time = System.currentTimeMillis();
		int maxDepth = 2;
		decisionDepth = 0;
		
		while(maxDepth <= 10) { // LESS THAN 9000 !
			decisionDepth = maxDepth;
			maxValue(maxDepth,Integer.MIN_VALUE,Integer.MAX_VALUE);
			maxDepth++;
		}
		System.out.println(System.currentTimeMillis()-time);
		return decision;
	}
	
	private int maxValue(int depth,int alpha,int beta){
		
		
		if (gameBoard.isGameFinished() || depth == 0) return gameBoard.evaluate(ourPlayer);
		
		int v = Integer.MIN_VALUE;
		int tempValue;
		for (int column : gameBoard.actions()){ // Should be arranged according to values from previous iteration
			
			gameBoard.layBrick(column, ourPlayer);
			tempValue = minValue(depth-1,alpha,beta);
			gameBoard.removeLastBrick();
			if (tempValue > v){
				v = tempValue;
				if (depth >= decisionDepth){
					this.decisionDepth = depth;
					this.decision = column;
				}
			}
			System.out.println(v);
			
			if (v >= beta){
				System.out.println("Betacut: " + Integer.toString(depth));
				return v;
			}
			alpha = Math.max(v,alpha);
		}
		return v;
	}
	
	private int minValue(int depth,int alpha,int beta){
		
		
		if (gameBoard.isGameFinished() || depth == 0){
			return (gameBoard.evaluate(ourPlayer));
		}
		
		int v = Integer.MAX_VALUE;
		int tempValue;
		for (int column : gameBoard.actions()){ // Should be arranged according to values from previous iteration
			
			gameBoard.layBrick(column, enemyPlayer);
			tempValue = maxValue(depth-1,alpha,beta);
			gameBoard.removeLastBrick();
			
			if (tempValue < v){
				v = tempValue;
				if (depth >= decisionDepth){
					this.decisionDepth = depth;
					this.decision = column;
				}
			}
			
			if (v <= alpha){
				System.out.println("Alphacut: " + Integer.toString(depth));
				return v;
			}
			beta = Math.min(v,beta);
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see IGameLogic#gameFinished()
	 */
	@Override
	public Winner gameFinished() {
		// TODO Auto-generated method stub
		return gameBoard.gameFinished();
	}

}
