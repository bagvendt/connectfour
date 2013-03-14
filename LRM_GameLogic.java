import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		long time = System.currentTimeMillis();
		int maxDepth = 10;
		System.out.println("Starting calculation");
		while(maxDepth <= 10) {
			decisionDepth = maxDepth;
			maxValue(maxDepth,Integer.MIN_VALUE,Integer.MAX_VALUE);
			maxDepth++;
		}
		System.out.println("Decision took (ms): " + Long.toString(System.currentTimeMillis()-time));
		return decision;
	}
	
	private int maxValue(int depth,int alpha,int beta){
		
		if (gameBoard.isGameFinished() || depth == 0) return gameBoard.evaluate();
		
		int v = Integer.MIN_VALUE;
		int tempValue;
		for (int column : gameBoard.actions()){ // Should be arranged according to values from previous iteration
			
			gameBoard.layCoin(column, ourPlayer);
			tempValue = minValue(depth-1,alpha,beta) / 10 * 9; // Decreases value over time
			//if (depth == decisionDepth) System.out.println("Column: " + Integer.toString(column) + "\nValue: " + Integer.toString(tempValue) + "\n");
			gameBoard.removeLastCoin();
			if (tempValue > v){
				v = tempValue;
				if (depth == decisionDepth)
					decision = column;
			}
			
			if (v >= beta){
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
			tempValue = maxValue(depth-1,alpha,beta); // Decreases value over time
			gameBoard.removeLastCoin();
			
			if (tempValue < v){
				v = tempValue;
				if (depth == decisionDepth)
					decision = column;
			}
			
			if (v <= alpha){
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
