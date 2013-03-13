import static org.junit.Assert.*;

import org.junit.Test;

import sun.security.ssl.Debug;

public class BoardTest {

	@Test
	public void testHeight() {
		Board board = new Board(3,3);
		Brick brick1 = new Brick(IGameLogic.Winner.PLAYER1);
		Brick brick2 = new Brick(IGameLogic.Winner.PLAYER1);
		Brick brick3 = new Brick(IGameLogic.Winner.PLAYER1);
		
		board.layBrick(brick1, 0);
		board.layBrick(brick2, 0);
		board.layBrick(brick3, 0);
		assertEquals(1, brick1.getRow());
		assertEquals(2, brick2.getRow());
		assertEquals(3, brick3.getRow());
		
		assertEquals(0, brick1.getColumn());
		assertEquals(0, brick2.getColumn());
		assertEquals(0, brick3.getColumn());
	}


	@Test
	public void testHeuristic1() {
		Board board = new Board(7,7);
		Brick brick1 = new Brick(IGameLogic.Winner.PLAYER1);
		
		board.layBrick(brick1, 3);
		System.out.println(Integer.toString(board.EvaluateBoard(IGameLogic.Winner.PLAYER1)));
	}




}
