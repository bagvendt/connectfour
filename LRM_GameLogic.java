import java.util.List;

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
		
		// Initialize decision to -1 so we get an error if something doesn't work
		decision = -1;
		
		ourPlayer = player == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		enemyPlayer = player == 2 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		
		gameBoard = new Board(columns,rows,ourPlayer, enemyPlayer);
	}

	/* (non-Javadoc)
	 * @see IGameLogic#insertCoin(int, int)
	 */
	@Override
	public void insertCoin(int column, int playerID) {
		IGameLogic.Winner thePlayer = playerID == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		gameBoard.layCoin(column, thePlayer);	
	}

	/* (non-Javadoc)
	 * @see IGameLogic#decideNextMove()
	 */
	@Override
	public int decideNextMove() {
		
		long startTime = System.currentTimeMillis();
		
		// We start with a depth of 10, to save some time.
		int maxDepth = 5;
		
		while(System.currentTimeMillis() - startTime < 9000) {
			
			gameBoard.clearCache();
			decisionDepth = maxDepth;
			maxValue(maxDepth,Integer.MIN_VALUE,Integer.MAX_VALUE);
			maxDepth++;
		}
		System.out.println(decision);
		return decision;
	}
	
	private int maxValue(int depth,int alpha,int beta){
		
		// Should we stop and evaluate
		if (gameBoard.isGameFinished() || depth == 0) return gameBoard.evaluate();
		
		int v = Integer.MIN_VALUE;
		int tempValue;
		// Get a set of (ordered) actions to loop through
		List<Integer> validColumns = gameBoard.actions(depth,ourPlayer); 
		if (depth == decisionDepth){ System.out.println(validColumns);
		
		}
		for (int column : validColumns){
			
			gameBoard.layCoin(column, ourPlayer);
			
			// If the node is cached, just get the utility value
			if (gameBoard.isCached(depth)){
				tempValue = gameBoard.getHashUtility(depth);
			}
			// Otherwise calculate it and add it to the cache
			else{ 
				tempValue = minValue(depth,alpha,beta);
				gameBoard.addThisToCache(depth, tempValue);
			}
			
			gameBoard.removeLastCoin();
			// If the found value is lather than the current, we use that instead.
			if (tempValue > v){
				v = tempValue;
				// If we are at the top level, we correct our decision to reflect it.
				if (depth == decisionDepth) decision = column;
			}

			
			if (v >= beta){
				return v;
			}
			alpha = Math.max(v,alpha);
		}
		return v;
	}
	
	private int minValue(int depth,int alpha,int beta){
		
		if (gameBoard.isGameFinished() || depth == 0) return gameBoard.evaluate();
		
		int v = Integer.MAX_VALUE;
		int tempValue;
		
		// Get a set of (ordered) actions to loop through
		for (int column : gameBoard.actions(depth,enemyPlayer)){
			
			gameBoard.layCoin(column, enemyPlayer);

			tempValue = maxValue(depth-1,alpha,beta); 
			
			// If the node is cached, just get the utility value
			if (gameBoard.isCached(depth)){
				tempValue = gameBoard.getHashUtility(depth);
			}
			// If the found value is lather than the current, we use that instead.
			else{
				tempValue = maxValue(depth-1,alpha,beta);
				gameBoard.addThisToCache(tempValue,depth);
			}
			gameBoard.removeLastCoin();
			
			if (tempValue < v){
				v = tempValue;
			}
			
			if (v <= alpha){
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

		return gameBoard.gameFinished();
	}

}
