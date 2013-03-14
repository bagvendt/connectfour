import java.util.Stack;

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
	private double hit;
	private double miss;
	
	@Override
	public void initializeGame(int columns, int rows, int player) {
		decision = -1;
		ourPlayer = player == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		enemyPlayer = player == 2 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		
		gameBoard = new Board(columns,rows,ourPlayer);
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
		
		long time = System.currentTimeMillis();
		int maxDepth = 10;
		System.out.println("Starting calculation");
		while(maxDepth <= 15 && System.currentTimeMillis() - time < 9000) {
			gameBoard.clearCache();
			decisionDepth = maxDepth;
			maxValue(maxDepth,Integer.MIN_VALUE,Integer.MAX_VALUE);
			maxDepth++;
		}
		System.out.println("Decision took (ms): " + Long.toString(System.currentTimeMillis()-time));
		System.out.println("Hit/miss:" + hit/miss);
		System.out.println("Hitrate:" + (hit/(miss+hit)));
		System.out.println("Depth: " + (maxDepth-1));
		return decision;
	}
	
	private int maxValue(int depth,int alpha,int beta){
		
		if (gameBoard.isGameFinished() || depth == 0) return gameBoard.evaluate();
		
		int v = Integer.MIN_VALUE;
		int tempValue;
		for (int column : gameBoard.actions()){ // Should be arranged according to values from previous iteration
			
			gameBoard.layCoin(column, ourPlayer);
			if (gameBoard.isHashed(depth)){
				//System.out.println("Using hash");
				tempValue = gameBoard.getHashUtility(depth);
				hit++;
			}
			else{
				tempValue = minValue(depth-1,alpha,beta) / 10 * 9;
				gameBoard.addThisToCache(depth, tempValue);
				miss++;
			}
			 // Decreases value over time
			gameBoard.removeLastCoin();
			if (depth == decisionDepth)
				System.out.println("Column: " + Integer.toString(column) + "\nValue: " + Integer.toString(tempValue));
			if (tempValue > v){
				v = tempValue;
				if (depth == decisionDepth) decision = column;
			}
			
			if (v >= beta){
				//System.out.println("Beta-cut");
				return v;
			}
			alpha = Math.max(v,alpha);
		}
		//System.out.println(v);
		return v;
	}
	
	private int minValue(int depth,int alpha,int beta){
		
		if (gameBoard.isGameFinished() || depth == 0){
			return (gameBoard.evaluate());
		}
		
		int v = Integer.MAX_VALUE;
		int tempValue;
		for (int column : gameBoard.actions()){ // Should be arranged according to values from previous iteration
			
			gameBoard.layCoin(column, enemyPlayer);
			if (gameBoard.isHashed(depth)){
				tempValue = gameBoard.getHashUtility(depth);
				hit++;
			}
			else{
				tempValue = maxValue(depth-1,alpha,beta) / 10 * 9; // Decreases value over time
				gameBoard.addThisToCache(tempValue,depth);
				miss++;
			}
			gameBoard.removeLastCoin();
			
			if (tempValue < v){
				v = tempValue;
				if (depth == decisionDepth)
					decision = column;
			}
			
			if (v <= alpha){
				//System.out.println("Alpha-cut");
				return v;
			}
			beta = Math.min(v,beta);
		}
		//System.out.println(v);
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
