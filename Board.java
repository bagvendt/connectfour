import java.util.*;



/**
 * 
 * A board that handles:
 *  - Two different heuristic functions. Euclidian distance and a more connect four specific heuristic.
 *  - Caching of different game states
 *  - Unique hash code of a game state
 *  - Available actions
 *  - Move ordering of available actions
 *  - Terminal state check, and underlying helper functions.
 *
 */
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
	 * Creates a new board.
	 * 
	 * @param columns  Number of columns on the board.
	 * @param rows Number of rows on the board.
	 * @param ourPlayer The IGameLogic enum of the player we are.
	 * @param enemyPlayer The oposite enum of ourPlayer.
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
	
	/**
	 * Does the current state exist in the cache.
	 * @param depth depth of the game state.
	 * @return Boolean indication wheather the state exists in cache.
	 */
	public boolean isCached(int depth){
		return cache.containsKey(hashThis(depth));
	}
	/**
	 * Clears the cache, and stores the old state.
	 */
	@SuppressWarnings("unchecked")
	public void clearCache(){
		oldCache = (HashMap<String, Integer>) cache.clone();
		cache.clear();
	}
	/**
	 * 
	 * Add utility value to cache
	 * @param depth At which depth is the cache calculated.
	 * @param value The value of the utility value.
	 */
	public void addThisToCache(int depth,int value){
		cache.put(hashThis(depth), value);
	}
	
	/**
	 * Lookup the utility value 
	 * @param depth The depth of the state.
	 * @return The corresponding utility value
	 */
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

	/**
	 * Lay a coin on the board
	 * @param column At which column to lay the coin.
	 * @param player The player that owns the coin
	 */
	public void layCoin(int column, IGameLogic.Winner player) {
		board[column].push(player);
		history.push(column);
		updateGameFinished();
	}
	/**
	 * Remove the latest coin that was added to the board.
	 */
	public void removeLastCoin() {
		int lastColumn = history.pop();
		board[lastColumn].pop();
		updateGameFinished();
	}
	/**
	 * Returns the state of the game
	 * @return The IGameLogic enumeration, TIE, NOTFINISHED, PLAYER1 or PLAYER2
	 */
	public IGameLogic.Winner gameFinished() {
		return finished;
	}
	
	/**
	 * What actions can be carried out in this game state.
	 * @param depth At what depth is the board
	 * @param player Which player has the turn
	 * @param moveOrdering Should we do a qualified guess on which move is best.
	 * @return An array of columns/actions
	 */
	public List<Integer> actions(int depth, IGameLogic.Winner player, Boolean moveOrdering){
			
			List<Integer> initSet = actions();
			if (!moveOrdering) {
				return initSet;
			}
				
			Set<Integer> intSet = new LinkedHashSet<Integer>(length);
			
			String hash;
			List<Decision> decisionList = new ArrayList<Decision>();
			Set<Integer> notDecisionSet = new HashSet<Integer>();
			
			for (int column : initSet){
				
				layCoin(column, player);
				hash = hashThis(depth-1);
				if(oldCache.containsKey(hash)){
					//System.out.println("Getting actions from hash:" + column);
					decisionList.add(new Decision(column,oldCache.get(hash)));
				}
				else{
					notDecisionSet.add(column);
				}
				
				removeLastCoin();
			}
			
			if (decisionList.size() == 0) return initSet;
			else{
				
				Collections.sort(decisionList);
				if (player == enemyPlayer){
					for(int i = 0; i < decisionList.size()-1; i++){
						intSet.add(decisionList.get(i).column);
					}
				}
				else{
					for(int i = decisionList.size()-1; i >= 0; i--){
						intSet.add(decisionList.get(i).column);
					}
				}
				initSet.clear();
				initSet.addAll(intSet);
				initSet.addAll(notDecisionSet);
				return initSet;
			}
		}
	/**
	 * Evaluates the current game state.
	 * @return Utility value of the current state.
	 */
	public int evaluate() {

		int utility = 0;
		switch (finished) {
		case PLAYER1:
			utility = ourPlayer == IGameLogic.Winner.PLAYER1 ? Integer.MAX_VALUE-1
					: Integer.MIN_VALUE+1;
			break;

		case PLAYER2:
			utility = ourPlayer == IGameLogic.Winner.PLAYER2 ? Integer.MAX_VALUE-1
					: Integer.MIN_VALUE+1;
			break;

		case TIE:
			utility = 0;
			break;
			
		case NOT_FINISHED:
			utility += EuclidianDistance();
			// utility += CalculateHeuristic();
			break;
		}
		return utility;
	}
	/**
	 * Our heuristic function. 
	 * @return Returns high values for good moves. Low for bad ones.
	 */
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
	/**
	 * Calculates heuristic by traveling in all directions from the brick
	 * and checking if there is a possibility for a row of 4 bricks.
	 * The more possibilities the higher heuristic
	 * @param the column of the brick 
	 * @param the row of the brick
	 * @param the player player
	 * @return
	 */
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
	/**
	 * Calculates heuristic based on how many neighbor bricks there is to the brick at (column, row).
	 * The more neighbors the higher heuristic
	 * @param the column of the brick
	 * @param the row of the brick
	 * @param the player
	 * @return
	 */
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
	/**
	 * Returns if a brick is in the position.
	 * @param column The column to check.
	 * @param row The row to check.
	 * @return If the position is occupied.
	 */
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
	
	/**
	 * Checks if the brick at position belongs to player
	 * @param column Column to check
	 * @param row Row to check
	 * @param player Player to check for equality.
	 * @return Weather the coin at row,column belongs to player
	 */
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
	/**
	 * A naive heuristic function that returns negative values if the enemy player's bricks is closest to the center of the board,
	 * and symmetric if ourPlayer is closest to the center.
	 * @return Heuristic value.
	 */
	private double EuclidianDistance() {
		double myDistance = 0, otherDistance = 0;
		//System.out.println(length);
		int midX = (int) (length / 2);
		int midY = (int) (height / 2);
		int col = 0, row = 0;
		for (Stack<IGameLogic.Winner> column : board) {
			for (IGameLogic.Winner winner : column) {
				double distance = Math.sqrt(Math.pow(col - midX, 2)
						+ Math.pow(row - midY, 2));
				if (winner.equals(ourPlayer)) {
					myDistance += distance;
				} else {
					otherDistance += distance;
				}
				row++;
			}
			col++;
		}
		//System.out.println("\nmyDistance: " + myDistance);
		return (otherDistance - myDistance) * 1000;
	}
	/**
	 * Checks if the position is out of bounds.
	 * @param column Column to check.
	 * @param row Row to check.
	 * @return
	 */
	private boolean positionOutOfBounds(int column, int row) {
		if (column < 0 || column > length || row < 0 || row > height)
			return true;

		return false;
	}
	/**
	 * Checks if the position is within the board and if so, checks if the position contains a coin which is owned by player.
	 * @param column The column to check
	 * @param row The row to check. 
	 * @param player The player to check for.
	 * @return Weather coin at position belongs to player.
	 */
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
	/**
	 * Checks if the game is finished
	 * @return True iff game is finished.
	 */
	public boolean isGameFinished() {
		return finished != IGameLogic.Winner.NOT_FINISHED;
	}
	/**
	 * Updates the finished state of the game.
	 */
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
			System.out.println("This is a tie!");
			return;
		}

		finished = IGameLogic.Winner.NOT_FINISHED;
	}

	/**
	 * A brick can be layed in this column if true is returned.
	 * @param column Column to check
	 * @return If there is space for another brick in the column.
	 */
	private boolean freeValidColumn(int column) {
		return (column >= 0 && column <= length && board[column].size() - 1 != height);
	}
	
	
	
	/**
	 * Returns a list of valid actions.
	 * @return A list of valid actions.
	 */
	private List<Integer> actions() {
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
		List<Integer> intList = new ArrayList<Integer>(intSet);
		Collections.shuffle(intList);
		return intList;
	}

}
