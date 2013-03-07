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
		Brick brick = new Brick(thePlayer);
		gameBoard.layBrick(brick, column);	
	}

	/* (non-Javadoc)
	 * @see IGameLogic#decideNextMove()
	 */
	@Override
	public int decideNextMove() {
		
		long time = System.currentTimeMillis();
		int depth = 2;
		int v = 0;
		while(time < 9000) { // LESS THAN 9000 !
			v = maxValue(gameBoard,depth,Integer.MAX_VALUE,Integer.MIN_VALUE);
			depth++;
		}
		
		return v;
	}
	
	private int maxValue(Board state,int depth,int alpha,int beta){
		
		switch (state.gameFinished()){
		case PLAYER1:
			return ourPlayer == Winner.PLAYER1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			break;
			
		case PLAYER2:
			return ourPlayer == Winner.PLAYER2 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			break;
			
		case TIE:
			return 0;
			break;
			
		case NOT_FINISHED:
			
			if (depth == 0) return state.evalute();
			
			int v = Integer.MIN_VALUE;
			for (int column : state.actions()){ // Should be arranged according to values from previous iteration
				
				Board newState = state.copy();
				newState.layBrick(new Brick(ourPlayer), column);
				v = Math.max(v,minValue(newState,depth-1,alpha,beta));
				if (v >= beta) return v;
				alpha = Math.max(v,alpha);
			}
			return v;
		}
	}
	
	private int minValue(Board state,int depth,int alpha,int beta){
		
		switch (state.gameFinished()){
			case PLAYER1:
				return ourPlayer == Winner.PLAYER1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
				break;
				
			case PLAYER2:
				return ourPlayer == Winner.PLAYER2 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
				break;
				
			case TIE:
				return 0;
				break;
				
			case NOT_FINISHED:
				
				if (depth == 0) return state.evalute();
				
				int v = Integer.MAX_VALUE;
				for (int column : state.actions()){ // Should be arranged according to values from previous iteration
					Board newState = state.copy();
					newState.layBrick(new Brick(ourPlayer), column);
					v = Math.min(v,maxValue(newState,depth-1,alpha,beta));
					if (v <= alpha) return v;
					alpha = Math.min(v,beta);
				}
				return v;
				break;
		}
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
