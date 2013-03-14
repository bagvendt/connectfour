import java.util.*;

public class Board {

	private Stack<IGameLogic.Winner>[] board;
	private Stack<Integer> history;
	private int height, length;
	private IGameLogic.Winner finished;
	private IGameLogic.Winner ourPlayer;
	private HashMap<String,Integer> cache;
	

	/**
	 * @param board
	 */

	public Board(int columns, int rows,IGameLogic.Winner ourPlayer) {
		length = columns-1;
		height = rows-1;
		history = new Stack<Integer>();
		finished = IGameLogic.Winner.NOT_FINISHED;
		cache = new HashMap<String,Integer>();
		
		this.ourPlayer = ourPlayer;
		
		board = new Stack[columns];
		for (int i = 0; i < board.length; i++) {
			board[i] = new Stack<IGameLogic.Winner>();
		}
	}
	
	public boolean isHashed(int depth){
		return cache.containsKey(hashThis(depth));
	}
	
	public void clearCache(){
		cache.clear();
	}
	
	public void addThisToCache(int depth,int value){
		cache.put(hashThis(depth), value);
	}
	
	public int getHashUtility(int depth){
		return cache.get(hashThis(depth));
	}
	
	private String hashThis(int depth){
		String hash = "";
		for (Stack<IGameLogic.Winner> stack : board ){
			for (IGameLogic.Winner player  : stack){
				hash += player == ourPlayer ? "1" : "2";
			}
			hash += "0";
		}
		
		hash += depth;
		return hash;
	}


	public void layCoin(int column, IGameLogic.Winner player) {
		board[column].push(player);
		history.push(column);
		updateGameFinished();
	}

	public void removeLastCoin() {
		int lastColumn = history.pop();
		board[lastColumn].pop();
		updateGameFinished();
	}

	public IGameLogic.Winner gameFinished() {
		return finished;
	}

	public int evaluate() {

		int utility = 0;
		switch (finished) {
		case PLAYER1:
			utility = ourPlayer == IGameLogic.Winner.PLAYER1 ? Integer.MAX_VALUE - 1
					: Integer.MIN_VALUE + 1;
			break;

		case PLAYER2:
			utility = ourPlayer == IGameLogic.Winner.PLAYER2 ? Integer.MAX_VALUE - 1
					: Integer.MIN_VALUE + 1;
			break;

		case TIE:
			utility = 0;
			break;
			
		case NOT_FINISHED:
			utility = (int) EuclidianDistance();
			break;
		}
		return utility;
	}

	private double EuclidianDistance() 
	{
		double myDistance = 0, otherDistance = 0;
		int midX = (int)(length/2);
		int midY = (int)(height/2);
		int col = 0, row = 0;
		for (Stack<IGameLogic.Winner> column : board)
		{
			for (IGameLogic.Winner winner : column) {
				double distance = Math.sqrt(Math.pow(row-midX, 2)+Math.pow(col-midY,2));
				if (winner.equals(ourPlayer)) 
				{
					myDistance += distance;
				} else 
				{
					otherDistance += distance;
				}
				row++;
			}
		col++;
		}
		//System.out.println("distance: " + (otherDistance - myDistance));
		return (otherDistance - myDistance) * 1000;
	}

	private boolean positionIsValid(int column, int row,
			IGameLogic.Winner player) {

		// Outside range of the board
		if (column < 0 || column > length || row < 0 || row > height)
			return false;

		// space not taken by coin
		if (board[column].size() - 1 < row)
			return false;

		// coin is position is not players;
		if (board[column].get(row) != player)
			return false;

		// If nothing has stopped you from getting here, this is your coin
		return true;
	}

	public boolean isGameFinished() {
		return finished != IGameLogic.Winner.NOT_FINISHED;
	}

	private void updateGameFinished() {

		if (history.size() == 0) {
			finished = IGameLogic.Winner.NOT_FINISHED;
			return;
		}

		int lastCoinX = history.peek();
		int lastCoinY = board[lastCoinX].size() - 1;
		IGameLogic.Winner player = board[lastCoinX].get(lastCoinY);

		int NE = 0;
		int E = 0;
		int SE = 0;
		int S = 0;
		int SW = 0;
		int W = 0;
		int NW = 0;

		// Going through coins. Direction: NE
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX + i, lastCoinY + i, player))
				NE++;
			else
				break;
		}

		// Going through coins. Direction: SW
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX - i, lastCoinY - i, player))
				SW++;
			else
				break;
		}

		if (NE + SW + 1 >= 4) {
			finished = player;
			return;
		}

		// Going through coins. Direction: E
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX + i, lastCoinY, player))
				E++;
			else
				break;
		}

		// Going through coins. Direction: W
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX - i, lastCoinY, player))
				W++;
			else
				break;
		}

		if (E + W + 1 >= 4) {
			finished = player;
			return;
		}

		// Going through coins. Direction: SE
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX - i, lastCoinY + i, player))
				SE++;
			else
				break;
		}

		// Going through coins. Direction: NW
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX + i, lastCoinY - i, player))
				NW++;
			else
				break;
		}

		if (SE + NW + 1 >= 4) {
			finished = player;
			return;
		}

		// Going through coins. Direction: S
		for (int i = 1; i <= 3; i++) {
			if (positionIsValid(lastCoinX, lastCoinY - i, player))
				S++;
			else
				break;
		}

		if (S + 1 >= 4) {
			finished = player;
			return;
		}

		boolean tie = true;

		for (int i = 0; i <= length; i++) {
			if (board[i].size() - 1 < height) {
				tie = false;
				break;
			}
		}

		if (tie) {
			finished = IGameLogic.Winner.TIE;
		}

		finished = IGameLogic.Winner.NOT_FINISHED;
	}

	private boolean freeValidColumn(int column) {
		return (column >= 0 && column <= length && board[column].size() - 1 != height);
	}

	public Set<Integer> actions() {
		Set<Integer> intSet = new HashSet<Integer>(length); 
		
		int column;
		for (int i = 0; i <= length; i++) {
			if (!board[i].isEmpty()) {
				for (int j = -3; j <= 3; j++) {
					column = i + j;

					if (freeValidColumn(column))
						intSet.add(column);
				}
			}
		}
		if (intSet.size() == 0 && finished != IGameLogic.Winner.TIE)
			intSet.add(length / 2);

		return intSet;
	}

}
