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
	private Map<Integer,Integer> decisions;
	
	@Override
	public void initializeGame(int columns, int rows, int player) {
		gameBoard = new Board(columns,rows);
		ourPlayer = player == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		enemyPlayer = player == 2 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
		
		decisions = new HashMap<Integer,Integer>();
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
		int maxDepth = 2;
		decisions.clear();
		Map<Integer, Integer> oldDecisions = new HashMap<Integer,Integer>();
		while(maxDepth <= 12) { // LESS THAN 9000 !
			decisions.clear();
			maxValue(maxDepth,Integer.MIN_VALUE,Integer.MAX_VALUE,oldDecisions);
			maxDepth++;
			oldDecisions.clear();
			oldDecisions.putAll(decisions);
		}
		System.out.println("Decision took (ms): " + Long.toString(System.currentTimeMillis()-time));
		return oldDecisions.get(maxDepth-1);
	}
	
	private int maxValue(int depth,int alpha,int beta, Map<Integer, Integer> oldDecision){
		
		
		if (gameBoard.isGameFinished() || depth == 0) return gameBoard.evaluate(ourPlayer);
		
		int v = Integer.MIN_VALUE;
		int tempValue;
		for (int column : gameBoard.actions(oldDecision.get(depth-1))){ // Should be arranged according to values from previous iteration
			
			gameBoard.layCoin(column, ourPlayer);
			tempValue = minValue(depth-1,alpha,beta,oldDecision);
			gameBoard.removeLastCoin();
			if (tempValue > v){
				v = tempValue;
				decisions.put(depth, column);
			}
			
			if (v >= beta){
				return v;
			}
			alpha = Math.max(v,alpha);
		}
		return v;
	}
	
	private int minValue(int depth,int alpha,int beta, Map<Integer,Integer> oldDecision){
		
		if (gameBoard.isGameFinished() || depth == 0){
			return (gameBoard.evaluate(ourPlayer));
		}
		
		int v = Integer.MAX_VALUE;
		int tempValue;
		for (int column : gameBoard.actions(oldDecision.get(depth-1))){ // Should be arranged according to values from previous iteration
			
			gameBoard.layCoin(column, enemyPlayer);
			tempValue = maxValue(depth-1,alpha,beta,oldDecision);
			gameBoard.removeLastCoin();
			
			if (tempValue < v){
				v = tempValue;
				decisions.put(depth, column);
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
		// TODO Auto-generated method stub
		return gameBoard.gameFinished();
	}

}
