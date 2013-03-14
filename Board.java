import java.util.*;

import com.sun.tools.javac.util.Pair;

class Decision implements Comparable<Decision> {
    int column;
    int utility;

    public Decision(int column, int utility) {
        this.column = column;
        this.utility = utility;
    }

    @Override
    public int compareTo(Decision o) {
        return utility < o.utility ? -1 : utility > o.utility ? 1 : 0;
    }
}

public class Board {

	private Stack<IGameLogic.Winner>[] board;
	private Stack<Integer> history;
	private int height, length;
	private IGameLogic.Winner finished;
	private IGameLogic.Winner ourPlayer;
	private IGameLogic.Winner enemyPlayer;
	private HashMap<String,Integer> cache;
	private HashMap<String,Integer> oldCache;

	/**
	 * @param board
	 */
	public Board(int columns, int rows, IGameLogic.Winner ourPlayer,
			IGameLogic.Winner enemyPlayer) {
		length = columns - 1;
		height = rows - 1;
		cache = new HashMap<String,Integer>();
		oldCache = new HashMap<String,Integer>();
		history = new Stack<Integer>();
		finished = IGameLogic.Winner.NOT_FINISHED;
		this.ourPlayer = ourPlayer;
		this.enemyPlayer = enemyPlayer;

		board = new Stack[columns];
		for (int i = 0; i < board.length; i++) {
			board[i] = new Stack<IGameLogic.Winner>();
		}
	}
	
	public boolean isHashed(int depth){
		return cache.containsKey(hashThis(depth));
	}
	
	@SuppressWarnings("unchecked")
	public void clearCache(){
		oldCache = (HashMap<String, Integer>) cache.clone();
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

	public Set<Integer> actions(int depth, IGameLogic.Winner player){
			
			Set<Integer> initSet = actions();
			Set<Integer> intSet = new LinkedHashSet<Integer>(length);
			
			String hash;
			List<Decision> decisionList = new ArrayList<Decision>();
			
			for (int column : initSet){
				
				layCoin(column, player);
				hash = hashThis(depth-1);
				if(oldCache.containsKey(hash)){
					//System.out.println("Getting actions from hash:" + column);
					decisionList.add(new Decision(column,oldCache.get(hash)));
				}
				
				removeLastCoin();
			}
			
			if (decisionList.size() == 0) return initSet;
			else{
				
				Collections.sort(decisionList);
				if (player == enemyPlayer){
					//System.out.println("Start");
					for(int i = 0; i < decisionList.size()-1; i++){
						intSet.add(decisionList.get(i).column);
						//System.out.println(i + "/" + decisionList.get(i).utility);
					}
					//System.out.println("Slut");
				}
				else{
					for(int i = decisionList.size()-1; i >= 0; i--){
						intSet.add(decisionList.get(i).column);
					}
				}
				
				return intSet;
			}
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
			// utility += EuclidianDistance();
			utility += CalculateHeuristic();
			break;
		}
		return utility;
	}

	private int CalculateHeuristic() {
		int playerSolutions = 0;
		int opponentSolutions = 0;

		int col = 0, row = 0;
		for (Stack<IGameLogic.Winner> column : board) {
			for (IGameLogic.Winner winner : column) {
				if (winner.equals(ourPlayer)) {
					playerSolutions += CheckNeighbourDirections(col, row,
							ourPlayer);
					playerSolutions += BrickNeighborCount(col, row, ourPlayer);
				} else {
					opponentSolutions += CheckNeighbourDirections(col, row,
							enemyPlayer);
					opponentSolutions += BrickNeighborCount(col, row,
							enemyPlayer);
				}
				row++;
			}
			col++;
		}

		return playerSolutions - opponentSolutions;
	}

	private int CheckNeighbourDirections(int column, int row,
			IGameLogic.Winner player) {
		int possibleDirections = 0;

		// Horizontal
		for (int i = column - 3; i <= column; i++) {
			if (positionOutOfBounds(i, row))
				continue;
			for (int j = i; j < i + 4; j++) {
				if (positionOutOfBounds(j, row))
					break;
				if (!checkPlayerIsMine(j, row, player))
					break;

				if (j == i + 3)
					possibleDirections++;
			}
		}

		// Diagonally and vertically
		int lu = 0, ru = 0, rd = 0, ld = 0, vUp = 0, vDown = 0;
		for (int dX = 1; dX < 4; dX++) {
			// left up
			if (!positionOutOfBounds(column - dX, row + dX)
					&& checkPlayerIsMine(column - dX, row + dX, player)) {
				lu++;
			}
			// right up
			if (!positionOutOfBounds(column + dX, row + dX)
					&& checkPlayerIsMine(column + dX, row + dX, player)) {
				ru++;
			}

			// right down
			if (!positionOutOfBounds(column + dX, row - dX)
					&& checkPlayerIsMine(column + dX, row - dX, player)) {
				rd++;
			}

			// left down
			if (!positionOutOfBounds(column - dX, row - dX)
					&& checkPlayerIsMine(column - dX, row - dX, player)) {
				ld++;
			}

			// Up
			if (!positionOutOfBounds(column, row + dX)
					&& checkPlayerIsMine(column, row + dX, player)) {
				vUp++;
			}
			// Down
			if (!positionOutOfBounds(column, row - dX)
					&& checkPlayerIsMine(column, row - dX, player)) {
				vDown++;
			}
		}

		if (lu == 3)
			possibleDirections++;
		if (ru == 3)
			possibleDirections++;
		if (rd == 3)
			possibleDirections++;
		if (ld == 3)
			possibleDirections++;
		if (vUp == 3)
			possibleDirections++;
		if (vDown == 3)
			possibleDirections++;

		return possibleDirections;
	}

	public int BrickNeighborCount(int column, int row, IGameLogic.Winner player) {
		int heuristic = 0;

		int lastCoinX = column;
		int lastCoinY = row;

		int NE = 0;
		int E = 0;
		int SE = 0;
		int S = 0;
		int SW = 0;
		int W = 0;
		int NW = 0;

		// Check NE
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX + i, lastCoinY + i))
				break;
			if (positionIsEmpty(lastCoinX + i, lastCoinY + i))
				continue;
			if (checkPlayerIsMine(lastCoinX + i, lastCoinY + i, player))
				NE++;
			else
				break;
		}
		// Check E
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX + i, lastCoinY))
				break;
			if (positionIsEmpty(lastCoinX + i, lastCoinY))
				continue;
			if (checkPlayerIsMine(lastCoinX + i, lastCoinY, player))
				E++;
			else
				break;
		}
		// Check SE
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX + i, lastCoinY - i))
				break;
			if (positionIsEmpty(lastCoinX + i, lastCoinY - i))
				continue;
			if (checkPlayerIsMine(lastCoinX + i, lastCoinY - i, player))
				SE++;
			else
				break;
		}
		// Check S
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX, lastCoinY - i))
				break;
			if (positionIsEmpty(lastCoinX, lastCoinY - i))
				continue;
			if (checkPlayerIsMine(lastCoinX, lastCoinY - i, player))
				S++;
			else
				break;
		}
		// Check SW
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX - i, lastCoinY - i))
				break;
			if (positionIsEmpty(lastCoinX - i, lastCoinY - i))
				continue;
			if (checkPlayerIsMine(lastCoinX - i, lastCoinY - i, player))
				SW++;
			else
				break;
		}
		// Check W
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX - i, lastCoinY))
				break;
			if (positionIsEmpty(lastCoinX - i, lastCoinY))
				continue;
			if (checkPlayerIsMine(lastCoinX - i, lastCoinY, player))
				W++;
			else
				break;
		}
		// Check NW
		for (int i = 1; i < 4; i++) {
			if (positionOutOfBounds(lastCoinX - i, lastCoinY + i))
				break;
			if (positionIsEmpty(lastCoinX - i, lastCoinY + i))
				continue;
			if (checkPlayerIsMine(lastCoinX - i, lastCoinY + i, player))
				NW++;
			else
				break;
		}

		int heuristicCompass[] = new int[] { NE, E, SE, S, SW, W, NW };

		for (int i : heuristicCompass) {
			heuristic += i;
		}
		return heuristic;
	}

	private boolean positionIsEmpty(int column, int row) {

		// Outside range of the board
		if (column < 0 || column > length || row < 0 || row > height)
			return false;

		// space not taken by coin
		if (board[column].size() - 1 < row)
			return true;

		// If nothing has stopped you from getting here, this is your coin
		return false;
	}

	private boolean checkPlayerIsMine(int column, int row,
			IGameLogic.Winner player) {
		// Outside range of the board
		if (column < 0 || column > length || row < 0 || row > height)
			return false;

		// coin is position is not player
		if (board[column].size() > row) 
		{
			if (board[column].get(row) != player)
				return false;
		}

		return true;
	}

	private double EuclidianDistance() {
		double myDistance = 0, otherDistance = 0;
		int midX = (int) (length / 2);
		int midY = (int) (height / 2);
		int col = 0, row = 0;
		for (Stack<IGameLogic.Winner> column : board) {
			for (IGameLogic.Winner winner : column) {
				double distance = Math.sqrt(Math.pow(row - midX, 2)
						+ Math.pow(col - midY, 2));
				if (winner.equals(ourPlayer)) {
					myDistance += distance;
				} else {
					otherDistance += distance;
				}
				row++;
			}
			col++;
		}
		return (otherDistance - myDistance) * 1000;
	}

	private boolean positionOutOfBounds(int column, int row) {
		if (column < 0 || column > length || row < 0 || row > height)
			return true;

		return false;
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
	
	

	private Set<Integer> actions() {
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
