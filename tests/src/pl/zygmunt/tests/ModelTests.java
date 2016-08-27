package pl.zygmunt.tests;

import org.junit.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import pl.zygmunt.common.Common;
import pl.zygmunt.common.Direction;
import pl.zygmunt.model.Agent;
import pl.zygmunt.model.Card;
import pl.zygmunt.model.DeckGenerator;
import pl.zygmunt.model.GameProperties;
import pl.zygmunt.model.GameResult;
import pl.zygmunt.model.GoalCard;
import pl.zygmunt.model.Model;
import pl.zygmunt.model.Player;
import pl.zygmunt.model.StartCard;
import pl.zygmunt.model.State;
import pl.zygmunt.model.StatesGenerator;
import pl.zygmunt.model.TunnelCard;

public class ModelTests
{
	@Test
	public void testDirection()
	{
		Direction direct1 = Direction.Up;
		Direction direct2 = Direction.getOppositeDirection(direct1);
		
		Assert.assertEquals(direct2, Direction.Down);
		
		Direction direct3 = Direction.Down;
		Direction direct4 = Direction.getOppositeDirection(direct3);
		
		Assert.assertEquals(direct4, Direction.Up);
		
		Assert.assertEquals(Direction.Left, Direction.getOppositeDirection(Direction.getOppositeDirection(Direction.Left)));
	}

	@Test
	public void testDeckGenerator()
	{
		List<Card> testDeck = DeckGenerator.generateDeck();
		
		Assert.assertEquals(68d, testDeck.size(), 0);
	}
	
	@Test
	public void testState()
	{
		State state1 = new State(new boolean[] {false, false, false});
		State state2 = new State(new boolean[] {false, false, false});	
		State state3 = new State(new boolean[] {false, true, false});
		State state4 = new State(new boolean[] {true, true, false});
		
		Assert.assertEquals(state1, state2);
		
		Assert.assertTrue(state2.getSaboteurs().isEmpty());
		Assert.assertFalse(state3.getSaboteurs().isEmpty());
		
		List<Integer> saboteurIDs = state3.getSaboteurs();
		List<Integer> saboteurIDs2 = state4.getSaboteurs();
		
		Assert.assertArrayEquals(saboteurIDs.toArray(new Integer[saboteurIDs.size()]), new Integer[]{1});
		Assert.assertArrayEquals(saboteurIDs2.toArray(new Integer[saboteurIDs2.size()]), new Integer[]{0,1});
		
		Assert.assertFalse(state4.isPlayerSaboteur(2));
		Assert.assertTrue(state4.isPlayerSaboteur(1));

		Assert.assertFalse(state4.haveSameRole(1, 2));
		Assert.assertTrue(state4.haveSameRole(0, 1));		
	}
	
	@Test
	public void testGameProperties()
	{
		GameProperties.setAmountOfPlayers(5);
		GameProperties.setHumanPlayer(true);
		
		Assert.assertEquals(2, GameProperties.maxNumberOfSaboteurs);
		Assert.assertTrue(GameProperties.humanPlayer);
		
		GameProperties.setHumanPlayer(false);
		
		Assert.assertFalse(GameProperties.humanPlayer);
	}
	
	@Test
	public void testPlayer()
	{
		Player player = new Player(0);
		player.setRole(true);
		
		Assert.assertEquals(0, player.getID());
		
		Assert.assertFalse(player.getBlocked());
		
		player.setBlocked(true);
		
		Assert.assertTrue(player.getBlocked());
		
		Assert.assertTrue(player.getRole());
		
		Assert.assertEquals(0, player.knowsGoal(1));
		
		player.setGoal(1, true);
		
		Assert.assertEquals(2, player.knowsGoal(1));
		Assert.assertEquals(1, player.knowsGoal(2));
		Assert.assertEquals(1, player.knowsGoal(3));
		
		player.resetGoal();
		
		Assert.assertEquals(0, player.knowsGoal(1));
		Assert.assertEquals(0, player.knowsGoal(2));
		Assert.assertEquals(0, player.knowsGoal(3));
	}
	
	@Test
	public void testTunnelCard()
	{
		Set<Direction> tunnels = new HashSet<Direction>();
		tunnels.add(Direction.Up);
		tunnels.add(Direction.Left);
		
		Set<Direction> openTunnels = new HashSet<Direction>();
		openTunnels.add(Direction.Up);
		openTunnels.add(Direction.Left);
		
		TunnelCard card1 = new TunnelCard(tunnels,openTunnels);
		
		Set<Direction> tunnels2 = new HashSet<Direction>();
		tunnels2.add(Direction.Down);
		tunnels2.add(Direction.Right);
		
		Set<Direction> openTunnels2 = new HashSet<Direction>();
		openTunnels2.add(Direction.Down);
		openTunnels2.add(Direction.Right);
		
		TunnelCard card2 = new TunnelCard(tunnels2,openTunnels2);
		
		Assert.assertEquals(new HashSet(Arrays.asList(new Direction[]{Direction.Up, Direction.Left})), card1.getTunnels());
		
		Assert.assertEquals(card2, TunnelCard.createRotatedCard(card1));
		
		Assert.assertEquals(card2, TunnelCard.createRotatedCard(TunnelCard.createRotatedCard(card2)));
	}
	
	@Test
	public void testStatesGenerator()
	{
		State state1 = new State(new boolean[]{false,false,false});
		State state2 = new State(new boolean[]{true,false,false});
		State state3 = new State(new boolean[]{false,true,false});
		State state4 = new State(new boolean[]{false,false,true});
		
		List<State> expectedStates = Arrays.asList(new State[]{state1,state2,state3,state4});
		
		List<State> possStates = StatesGenerator.generateAllPossibleStates(3);
		
		Assert.assertEquals(4, possStates.size());
		
		Assert.assertEquals(expectedStates, possStates);
		
		possStates = StatesGenerator.generateAllPossibleStates(4);
		
		Assert.assertEquals(5, possStates.size());
		
		possStates = StatesGenerator.generateAllPossibleStates(5);
		
		Assert.assertEquals(15, possStates.size());
	}
	
	@Test
	public void testBoardInitialization()
	{
		Set<Direction> tunnels = new HashSet<Direction>();
		tunnels.add(Direction.Up);
		tunnels.add(Direction.Left);
		tunnels.add(Direction.Down);
		tunnels.add(Direction.Right);
		
		Set<Direction> openTunnels = new HashSet<Direction>();
		openTunnels.add(Direction.Up);
		openTunnels.add(Direction.Left);
		openTunnels.add(Direction.Down);
		openTunnels.add(Direction.Right);
		
		StartCard expectedStartCard = new StartCard(tunnels, openTunnels);
		GoalCard withoutGold = new GoalCard(tunnels,openTunnels, false);
		GoalCard withGold = new GoalCard(tunnels,openTunnels, true);
		
		Model model = new Model();
		
		int goldCardPos = model.getGoldCardPosition();
		
		TunnelCard[][] board = model.getBoard();
		
		Assert.assertEquals(expectedStartCard, board[6][2]);
		
		Assert.assertEquals(withGold, board[goldCardPos][10]);
		
		switch(goldCardPos)
		{
			case 4:
				Assert.assertEquals(withGold, board[goldCardPos][10]);
				Assert.assertEquals(withoutGold, board[6][10]);
				Assert.assertEquals(withoutGold, board[8][10]);
				break;
			case 6:
				Assert.assertEquals(withGold, board[goldCardPos][10]);
				Assert.assertEquals(withoutGold, board[4][10]);
				Assert.assertEquals(withoutGold, board[8][10]);
				break;
			case 8:
				Assert.assertEquals(withGold, board[goldCardPos][10]);
				Assert.assertEquals(withoutGold, board[4][10]);
				Assert.assertEquals(withoutGold, board[6][10]);
				break;
		}
	}
	
	
	@Test
	public void testBoard()
	{
		Set<Direction> tunnels = new HashSet<Direction>();
		tunnels.add(Direction.Up);
		tunnels.add(Direction.Left);
		tunnels.add(Direction.Down);
		tunnels.add(Direction.Right);
		
		Set<Direction> openTunnels = new HashSet<Direction>();
		openTunnels.add(Direction.Up);
		openTunnels.add(Direction.Left);
		openTunnels.add(Direction.Down);
		openTunnels.add(Direction.Right);
		
		TunnelCard tunnelCard = new TunnelCard(tunnels,openTunnels);
		
		Model model = new Model();
		TunnelCard[][] board = model.getBoard();
		GoalCard goalCard = (GoalCard) board[6][10];
		Player player = model.getPlayer(0);
		
		Assert.assertFalse(goalCard.isDiscovered());
		Assert.assertFalse(model.isGoalCardReachable(2*2+2));
		Assert.assertEquals(0, player.knowsGoal(2));
		
		for(int i = 3; i < 10; ++i)
			model.placeTunnelCardOnBoard(tunnelCard, 6, i);
		
		Assert.assertTrue(model.isGoalCardReachable(2*2+2));

		Assert.assertTrue(goalCard.isDiscovered());
		Assert.assertNotEquals(0, player.knowsGoal(2));
		
	}
	
	
	@Test
	public void testPlayerOperations()
	{
		Set<Direction> tunnels = new HashSet<Direction>();
		tunnels.add(Direction.Up);
		tunnels.add(Direction.Left);
		tunnels.add(Direction.Down);
		tunnels.add(Direction.Right);
		
		Set<Direction> openTunnels = new HashSet<Direction>();
		openTunnels.add(Direction.Up);
		openTunnels.add(Direction.Left);
		openTunnels.add(Direction.Down);
		openTunnels.add(Direction.Right);
		
		TunnelCard tunnelCard = new TunnelCard(tunnels,openTunnels);
		
		Model model = new Model();
		
		Assert.assertEquals(GameResult.GameInProgress, model.getGameResult());
		
		TunnelCard[][] board = model.getBoard();
		
		Player player = model.getPlayer(0);
		
		Assert.assertFalse(player.getBlocked());
		
		model.block(player);
		
		Assert.assertTrue(player.getBlocked());
		
		model.deblock(player);
		
		Assert.assertFalse(player.getBlocked());
		
		model.placeTunnelCardOnBoard(tunnelCard, 6, 3);
		
		Assert.assertEquals(tunnelCard, board[6][3]);
		
		model.placeTunnelCardOnBoard(null, 6, 3);
		
		Assert.assertEquals(null, board[6][3]);
	}
	
	@Test
	public void testPlacingCard()
	{
		Set<Direction> tunnels = new HashSet<Direction>();
		tunnels.add(Direction.Up);
		tunnels.add(Direction.Down);
		tunnels.add(Direction.Right);
		
		Set<Direction> openTunnels = new HashSet<Direction>();
		openTunnels.add(Direction.Up);
		openTunnels.add(Direction.Down);
		openTunnels.add(Direction.Right);
		
		Set<Direction> tunnels2 = new HashSet<Direction>();
		tunnels2.add(Direction.Left);
		tunnels2.add(Direction.Right);
		
		Set<Direction> openTunnels2 = new HashSet<Direction>();
		openTunnels2.add(Direction.Left);
		openTunnels2.add(Direction.Right);
		
		TunnelCard card1 = new TunnelCard(tunnels, openTunnels);
		TunnelCard card2 = new TunnelCard(tunnels2, openTunnels2);
		
		Model model = new Model();
		
		TunnelCard[][] board = model.getBoard();
		
		boolean result1 = Common.checkIfCardCanBePlaced(board, card2 , 7, 3);
		boolean result2 = Common.checkIfCardCanBePlaced(board, card1, 6, 3);
		
		Assert.assertTrue(result2);
		Assert.assertFalse(result1);
		
		model.placeTunnelCardOnBoard(card1, 6, 3);
		
		result1 = Common.checkIfCardCanBePlaced(board, card2 , 7, 3);
		Assert.assertTrue(result1);

		
	}
	
	
	@Test
	public void testPlayersCreating()
	{
		GameProperties.setAmountOfPlayers(4);
		GameProperties.setHumanPlayer(false);
		Model model = new Model();
		
		int playersAmount = model.getPlayers().size();
		
		Assert.assertEquals(4, playersAmount);
		Assert.assertTrue(model.getPlayer(0) instanceof Agent);
		
		GameProperties.setAmountOfPlayers(6);
		GameProperties.setHumanPlayer(true);
		
		model.initialize();
		
		int newPlayersAmount = model.getPlayers().size();
		
		Assert.assertEquals(6, newPlayersAmount);
		Assert.assertFalse(model.getPlayer(0) instanceof Agent);
		
	}
}
