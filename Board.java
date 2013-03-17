import java.util.ArrayList;
import java.util.*;

public class Board{
	
	private Stack<IGameLogic.Winner>[] board;
	private Stack<Integer> history;
	private int height, length;
	private IGameLogic.Winner finished;
	
	/**
	 * @param board
	 */
	public Board(int columns, int rows) {
		length = columns-1;
		height = rows-1;
		history = new Stack<Integer>();
		finished = IGameLogic.Winner.NOT_FINISHED;
		
		board = new Stack[columns];
		for (int i = 0; i < board.length; i++) {
			board[i] = new Stack<IGameLogic.Winner>();
		}
	}

	public void layCoin(int column,IGameLogic.Winner player) {
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
	
	public int evaluate(IGameLogic.Winner ourPlayer){
		
		int utility = 0;
		switch (finished){
			case PLAYER1:
				utility = ourPlayer == IGameLogic.Winner.PLAYER1 ? Integer.MAX_VALUE-1 : Integer.MIN_VALUE+1;
				break;
				
			case PLAYER2:
				utility = ourPlayer == IGameLogic.Winner.PLAYER2 ? Integer.MAX_VALUE-1 : Integer.MIN_VALUE+1;
				break;
				
			case TIE:
				
			case NOT_FINISHED:
				
				utility += BrickNeighborCount();
				break;
		}
		return utility;
	}
	
	private boolean positionIsValid(int column,int row, IGameLogic.Winner player){
		
		// Outside range of the board
		if (column < 0 || column > length || row < 0 || row > height) return false;
		
		// space not taken by coin
		if (board[column].size()-1 < row) return false;
		
		// coin is position is not players;Problem
		if(board[column].get(row) != player) return false;
		
		// If nothing has stopped you from getting here, this is your coin
		return true;
	}
	
	//Check to see if position is out of ranged, used for heuristic methods
	private boolean positionIsOutOfRange(int column,int row)
	{
		if (column < 0 || column > length || row < 0 || row > height) return true;
		
		return false;
	}
	
	//Check to see if position is empty, used for heuristic methods
	private boolean positionIsEmpty(int column,int row)
	{
		if (board[column].size()-1 < row) return true;
		
		return false;	
	}
	
	//Check to see if position is mine, used for heuristic methods
	private boolean positionIsMine(int column,int row, IGameLogic.Winner player)
	{	
		if(board[column].get(row) == player) return true;
		
		return false;
	}
		
	public boolean isGameFinished()
	{
		return finished != IGameLogic.Winner.NOT_FINISHED;
	}

	private void updateGameFinished() {
		
		if (history.size() == 0){
			finished = IGameLogic.Winner.NOT_FINISHED;
			return;
		}
		
		
		int lastCoinX = history.peek();
		int lastCoinY = board[lastCoinX].size()-1;
		IGameLogic.Winner player = board[lastCoinX].get(lastCoinY);
		
		int NE = 0;
		int E  = 0;
		int SE = 0;
		int S  = 0;
		int SW = 0;
		int W  = 0;
		int NW = 0;
		
		// Going through coins. Direction: NE
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX+i,lastCoinY+i,player))
				NE++;
			else
				break;
		}
		
		// Going through coins. Direction: SW 
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX-i,lastCoinY-i,player))
				SW++;
			else
				break;
		}
		
		if(NE + SW + 1 >= 4){
			finished = player;
			return;
		}
		
		// Going through coins. Direction: E
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX+i,lastCoinY,player))
				E++;
			else
				break;
		}
		
		// Going through coins. Direction: W 
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX-i,lastCoinY,player))
				W++;
			else
				break;
		}
		
		if(E + W + 1 >= 4){
			finished = player;
			return;
		}
		
		// Going through coins. Direction: SE
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX-i,lastCoinY+i,player))
				SE++;
			else
				break;
		}
		
		// Going through coins. Direction: NW 
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX+i,lastCoinY-i,player))
				NW++;
			else
				break;
		}
		
		if(SE + NW + 1 >= 4){
			finished = player;
			return;
		}
		
		// Going through coins. Direction: S
		for (int i = 1; i <=3; i++){
			if (positionIsValid(lastCoinX,lastCoinY-i,player))
				S++;
			else
				break;
		}
		
		if(S + 1 >= 4){
			finished = player;
			return;
		}
			
		
		
		
		boolean tie = true;
		
		for (int i = 0; i <= length; i++){
			if (board[i].size()-1 < height){
				tie = false;
				break;
			}
		}
		
		if (tie){
			finished = IGameLogic.Winner.TIE;
		}
		
		finished = IGameLogic.Winner.NOT_FINISHED;
	}
	
	private boolean freeValidColumn(int column){
		return (column >= 0 && column <= length && board[column].size()-1 != height);
	}
	
	public Set<Integer> actions(Integer lastBest) {
		Set<Integer> intSet = new HashSet<Integer>(length); 
		if (lastBest != null && freeValidColumn(lastBest)) intSet.add(lastBest);
		
		
		int column;
		for (int i = 0; i <= length; i++){
			if (!board[i].isEmpty()){
				for(int j = -3; j <= 3; j++){
					column = i+j;
					
					if (freeValidColumn(column))
						intSet.add(column);
				}
			}
		}
		if (intSet.size() == 0 && finished != IGameLogic.Winner.TIE) intSet.add(length/2);
		
		return intSet;
	}
	
	public int BrickNeighborCount()
	{
		int heuristic = 0;
		
		int lastCoinX = history.peek();
		int lastCoinY = board[lastCoinX].size()-1;
		IGameLogic.Winner player = board[lastCoinX].get(lastCoinY);
		
		int NE = 1;
		int E  = 1;
		int SE = 1;
		int S  = 1;
		int SW = 1;
		int W  = 1;
		int NW = 1;
		
		//Check NE
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX+i, lastCoinY+i)) break;
			if(positionIsEmpty(lastCoinX+i, lastCoinY+i)) continue;
			if(positionIsMine(lastCoinX+i, lastCoinY+i, player)) NE++;
			else break;	
		}
		//Check E
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX+i, lastCoinY)) break;
			if(positionIsEmpty(lastCoinX+i, lastCoinY)) continue;
			if(positionIsMine(lastCoinX+i, lastCoinY, player)) E++;
			else break;	
		}
		//Check SE
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX+i, lastCoinY-i)) break;
			if(positionIsEmpty(lastCoinX+i, lastCoinY-i)) continue;
			if(positionIsMine(lastCoinX+i, lastCoinY-i, player)) SE++;
			else break;	
		}
		//Check S
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX, lastCoinY-i)) break;
			if(positionIsEmpty(lastCoinX, lastCoinY-i)) continue;
			if(positionIsMine(lastCoinX, lastCoinY-i, player)) S++;
			else break;	
		}
		//Check SW
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX-i, lastCoinY-i)) break;
			if(positionIsEmpty(lastCoinX-i, lastCoinY-i)) continue;
			if(positionIsMine(lastCoinX-i, lastCoinY-i, player)) SW++;
			else break;	
		}
		//Check W
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX-i, lastCoinY)) break;
			if(positionIsEmpty(lastCoinX-i, lastCoinY)) continue;
			if(positionIsMine(lastCoinX-i, lastCoinY, player)) W++;
			else break;	
		}
		//Check NW
		for(int i = 1; i < 4; i++)
		{		
			if(positionIsOutOfRange(lastCoinX-i, lastCoinY+i)) break;
			if(positionIsEmpty(lastCoinX-i, lastCoinY+i)) continue;
			if(positionIsMine(lastCoinX-i, lastCoinY+i, player)) NW++;
			else break;	
		}
		
		int heuristicCompass[] = new int[]{NE, E, SE, S, SW, W, NW};
		
		for(int i : heuristicCompass)
		{		
			if(i > heuristic) heuristic = i;
		}
		
		return heuristic;		
	}
	
	public int BlockingOpponentPaths()
	{
		int heuristic = 0;
		
		return heuristic;
	}
	
}
