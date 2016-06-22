package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import pl.zygmunt.common.Common;
import pl.zygmunt.common.Direction;
import pl.zygmunt.common.Point;

/**
 * Klasa reprezentujaca agenta (sabotazyste) w grze.
 * 
 * @author Piotr Zygmunt
 *
 */
public class AgentSaboteur extends Agent
{
	/**
	 * Czy sabotujemy otwarcie.
	 */
	private boolean sabotageOpenly;

	/**
	 * Kontruktor.
	 * 
	 * @param id
	 *            Identyfikator agenta.
	 * @param possibleState
	 *            Mozliwe stany w grze.
	 */
	public AgentSaboteur(int id, List<State> possibleState)
	{
		super(id);
		treshold = 4;
		model = new KripkeModel(id, true, possibleState);
		sabotageOpenly = false;
	}

	public Turn takeTurn(Player[] players, TunnelCard[][] board)
	{

		List<Card> roatetdplayerCards = new ArrayList<Card>();
		List<Card> playerCards = super.getCards();

		// utworzenie zbioru kart tunneli odwroconych
		for (Card card : playerCards)
		{
			if (card instanceof TunnelCard)
			{
				roatetdplayerCards.add(TunnelCard.createRotatedCard((TunnelCard) card));
			}
		}

		CardScore bestScore = new CardScore();
		bestScore.setScore(treshold);
		int bestCard = 0;
		Turn turn;
		boolean turned = false;

		for (int i = 0; i < playerCards.size(); ++i)
		{
			CardScore currentScore = rateCard(playerCards.get(i), players, board);
			if (currentScore.getScore() > bestScore.getScore())
			{
				bestScore = currentScore;
				bestCard = i;
			}
		}

		for (int i = 0; i < roatetdplayerCards.size(); ++i)
		{
			CardScore currentScore = rateCard(roatetdplayerCards.get(i), players, board);
			if (currentScore.getScore() > bestScore.getScore())
			{
				bestScore = currentScore;
				bestCard = i;
				turned = true;
			}
		}
		Card playCard;
		if (bestScore.getScore() > treshold)
		{
			if (turned)
			{
				playCard = roatetdplayerCards.get(bestCard);

			}
			else
			{
				playCard = playerCards.get(bestCard);
			}
			playerCards.remove(bestCard);
			roatetdplayerCards.clear();
		}
		else
		{
			discard(players, board);
			roatetdplayerCards.clear();
			return null;
		}

		turn = new Turn(playCard, (int) bestScore.getScore());
		turn.setAgentTarget(bestScore.getPlayerIDTarget());
		turn.setBoardTarget(bestScore.getBoardTarget());
		return turn;
	}

	/**
	 * Wyrzucenie karty z reki.
	 * 
	 * @param players
	 *            Tablica graczy
	 * @param board
	 *            Plansza do gry.
	 * @return Numer karty do odrzucenia.
	 */
	private int discard(Player[] players, TunnelCard[][] board)
	{
		double worstValue = 10;
		int worstCard = 0;
		double currentValue;
		List<Card> handCards = super.getCards();

		for (int i = 0; i < handCards.size(); i++)
		{
			currentValue = rateCardDiscard(handCards.get(i), players, board);
			if (currentValue < worstValue)
			{
				worstValue = currentValue;
				worstCard = i;
			}
		}
		handCards.remove(worstCard);
		return worstCard;
	}

	/**
	 * Wycena karty do odrzucenia.
	 * 
	 * @param card
	 *            Karta.
	 * @param players
	 *            Gracze.
	 * @param board
	 *            Plansza do gry.
	 * @return Wartosc liczbowa - wycena.
	 */
	private double rateCardDiscard(Card card, Player[] players, TunnelCard[][] board)
	{
		double score = 0;
		int[] knowGoal = super.getGoals();

		// jesli jest to karta tunneli
		if (card instanceof TunnelCard)
		{
			TunnelCard pathCard = (TunnelCard) card;
			int branches = 0;
			branches = pathCard.getOpenTunnels().size();
			score = (9 - 2 * branches);
		}

		// jesli jest to karta blokowania
		else if (card instanceof BlockCard)
		{
			if(sabotageOpenly)
			{
				score = 7;
			}
			else
			{
				score = 6;
			}
		}

		// jesli jest to karta odblokowania
		else if (card instanceof DeblockCard)
		{
			// jesli sabotujemy otwarcie
			if (sabotageOpenly)
			{
				score = 6;
			}
			else
			{
				score = 8;
			}
		}

		// jesli jest to karta zniszczenia
		else if (card instanceof DemolishCard)
		{
			score = 8;
		}

		// karta widoku
		else
		{
			// jesli nie znamy celu
			if (knowGoal[0] == 0)
			{
				score = 9;
			}
			else
			{
				score = 1;
			}
		}

		return score;
	}

	/**
	 * Przedefiniowana funkcja wyceniajaca dana karte.
	 * 
	 * @param card
	 *            Karta.
	 * @param players
	 *            Tablica graczy.
	 * @param board
	 *            Plansza do gry.
	 * @return Obiekt reprezentujacy wyecene danej karty.
	 */
	private CardScore rateCard(Card card, Player[] players, TunnelCard[][] board)
	{
		CardScore score = new CardScore();
		score.setScore(0);
		int id = super.getID();
		int[] knowGoal = super.getGoals();

		// karta tunneli
		if (card instanceof TunnelCard)
		{
			if (super.getBlocked() == false)
			{
				score = calculatePath(card, board);
			}

		}

		// karta blokujaca
		else if (card instanceof BlockCard)
		{
			if(sabotageOpenly)
			{
			HashSet<Integer> possible_saboteurs = new HashSet<Integer>();

			for (int i = 0; i < GameProperties.numberOfSaboteurs; ++i)
			{
				// znajdz najmniej podejrzanego agenta
				double suspicion[] = model.getLeastSuspicious(id, possible_saboteurs);
				// jesli nie jest zablokowany
				if (players[(int) suspicion[1]].getBlocked() == false)
				{
					score.setScore(8);
					score.setPlayerIDTarget((int) suspicion[1]);
					break;
				}

				// jesli jest dodajemy go do graczy wykluczonych i powtarzamy
				else
				{
					possible_saboteurs.add((int) suspicion[1]);
				}
			}
			}

		}
		// karta odblokowujaca
		else if (card instanceof DeblockCard)
		{
			if (super.getBlocked())
			{
				if (sabotageOpenly)
				{
					score.setScore(5);
					score.setPlayerIDTarget(id);
				}
				else
				{
					score.setScore(8);
					score.setPlayerIDTarget(id);
				}
			}

		}

		// karta demolujaca
		else if (card instanceof DemolishCard)
		{
			double currentValue = 0;
			if (sabotageOpenly)
			{
				for (int i = 0; i < Common.initialColumns; i++)
				{
					for (int j = 0; j < Common.initialRows; j++)
					{
						if (board[i][j] != null && !(board[i][j] instanceof GoalCard)
								&& !(board[i][j] instanceof StartCard))
						{
							currentValue = calculateRemovalScore(i, j, board[i][j], board);
							if (currentValue > score.getScore())
							{
								score.setScore(currentValue);
								score.setBoardTarget(new Point(i, j));
							}
						}
					}
				}
			}
		}

		// karta widoku
		else
		{
			if (knowGoal[0] == 0)
			{
				score.setScore(9);
				Random rand = new Random();
				int checkGoal = rand.nextInt(3) + 1;
				if (knowGoal[checkGoal] == 0)
				{
					score.setBoardTarget(new Point((checkGoal * 2) + 2, 10));
				}
				else
				{
					score.setBoardTarget(new Point((((checkGoal % 3) + 1) * 2) + 2, 10));
				}
			}
		}

		return score;
	}

	/**
	 * Resetowanie modelu Kripkego.
	 * 
	 * @param possibleStates
	 *            Wszystkie mozliwe stany.
	 */
	public void resetKripke(List<State> possibleStates)
	{
		model = new KripkeModel(super.getID(), true, possibleStates);
	}

	/**
	 * Zmiana postepowania - sabotujemy otwarcie badz nie.
	 * 
	 * @param sabotage
	 */
	public void setSabotageOpenly(boolean sabotage)
	{
		this.sabotageOpenly = sabotage;
	}

	public boolean isSabotagingOpenly()
	{
		return sabotageOpenly;
	}

	/**
	 * Sprawdzenie czy tunel jeset dosc blisko aby zaczac sabotowac otwarcie lub
	 * czy jestesmy juz podejrzewani.
	 * 
	 * @param board
	 *            Plansza do gry.
	 */
	public void updateStrategy(TunnelCard[][] board)
	{
		int diffX = 10;
		int diffY = 10;
		for (int x = 1; x < 12; x++)
		{
			for (int y = 7; y < 13; y++)
			{
				if (board[x][y] != null && (board[x][y].getOpenTunnels().size() > 0))
				{
					diffX = Math.min(Math.abs(x - 4), Math.min(Math.abs(x - 6), Math.abs(x - 8)));
					diffY = Math.abs(y - 10);
					// jesli jestesmy blisko kart celu sabotujemy otwarcie
					if (diffX + diffY < 4)
					{
						sabotageOpenly = true;
						return;
					}
				}
			}
		}
		// jesli jestesmy juz podejrzewani - sabotujemy otwarcie
		if (model.getSuspected(super.getID()))
		{
			sabotageOpenly = true;
		}
	}

	/**
	 * Obliczenie wyceny karty tuneli.
	 * 
	 * @param card
	 *            Karta tuneli.
	 * @param board
	 *            Plansza do gry.
	 * @return Wynik.
	 */
	private CardScore calculatePath(Card card, TunnelCard[][] board)
	{
		TunnelCard pathcard = (TunnelCard) card;
		double currentValue;
		CardScore score = new CardScore();
		score.setScore(-1);

		for (int i = 0; i < Common.initialColumns; i++)
		{
			for (int j = 0; j < Common.initialRows; j++)
			{

				if (Common.checkIfCardCanBePlaced(board, pathcard, i, j))
				{

					board[i][j] = pathcard;
					currentValue = calculatePathScore(i, j, pathcard, board);
					if (currentValue > score.getScore())
					{
							score.setScore(currentValue);
							score.setBoardTarget(new Point(i, j));
					}
					board[i][j] = null;
				}
			}
		}
		return score;
	}

	/**
	 * Wycena karty tuneli.
	 * 
	 * @param x
	 *            Wspolrzedne X.
	 * @param y
	 *            Wspolrzedne Y.
	 * @param card
	 *            Karta tuneli.
	 * @param board
	 *            Plansza do gry.
	 * @return Ocena karty.
	 */
	private double calculatePathScore(int x, int y, TunnelCard card, TunnelCard[][] board)
	{
		double cardScore = 0;
		int goalX = 6;
		double distance;
		int numberOfOpenTunnels = 0;
		boolean horizontalFit = false;
		boolean verticalFit = false;

		int[] knowGoal = super.getGoals();

		// jesli znamy cel
		if (knowGoal[0] == 1)
		{
			for (int i = 1; i < 4; i++)
			{
				if (knowGoal[i] == 1)
				{
					goalX = (2 * i) + 2;
				}
			}
		}

		numberOfOpenTunnels = card.getOpenTunnels().size();
		// odleglosc od celu
		distance = Math.abs(goalX - x) + Math.abs(y - 10);

		// jesli nie sabotujemy otwarcie
		if (!sabotageOpenly)
		{
			// jesli na lewym polu jest karta konca
			if (card.getOpenTunnels().contains(Direction.Left) && x > 0 && board[x - 1][y] instanceof GoalCard)
			{
				// jesli znamy cel i celem jest karta konc
				if (knowGoal[0] == 1 && (x - 1) == goalX)
				{
					return 10;
				}
				else if (knowGoal[((x - 1) / 2) - 2] != 2)
				{
					if (numberOfOpenTunnels > 2)
					{
						return 10;
					}
					else
					{
						return 9;
					}
				}
			}
			if (card.getOpenTunnels().contains(Direction.Right) && x < 12 && board[x + 1][y] instanceof GoalCard)
			{
				if (knowGoal[0] == 1 && (x + 1) == goalX)
				{
					return 10;
				}
				else if (knowGoal[((x + 1) / 2) - 2] != 2)
				{
					if (numberOfOpenTunnels > 2)
					{
						return 10;
					}
					else
					{
						return 9;
					}
				}
			}
			if (card.getOpenTunnels().contains(Direction.Up) && y > 0 && board[x][y - 1] instanceof GoalCard)
			{
				verticalFit = true;
			}
			if (card.getOpenTunnels().contains(Direction.Down) && y < 12 && board[x][y + 1] instanceof GoalCard)
			{
				return 10;
			}

			if (x > goalX && card.getOpenTunnels().contains(Direction.Left) && board[x - 1][y] == null)
			{
				horizontalFit = true;
			}
			if (x < goalX && card.getOpenTunnels().contains(Direction.Right) && board[x + 1][y] == null)
			{
				horizontalFit = true;
			}
			if (y > 10 && card.getOpenTunnels().contains(Direction.Up) && board[x][y - 1] == null)
			{
				verticalFit = true;
			}
			if (y < 10 && card.getOpenTunnels().contains(Direction.Down) && board[x][y + 1] == null)
			{
				verticalFit = true;
			}
			if (Math.abs(goalX - x) > Math.abs(10 - y))
			{
				if (horizontalFit)
				{
					cardScore += 5;
				}
				else if (verticalFit)
				{
					cardScore += 4;
				}
				if (horizontalFit && verticalFit)
				{
					cardScore += 0.5;
				}
			}
			else
			{
				if (verticalFit)
				{
					cardScore += 5;
				}
				else if (horizontalFit)
				{
					cardScore += 4;
				}
				if (horizontalFit && verticalFit)
				{
					cardScore += 0.5;
				}
			}

			cardScore += numberOfOpenTunnels - 1;
			cardScore += (19 - distance) / 9;
			return cardScore;
		}

		// jesli nie ma otwartych tuneli
		if (numberOfOpenTunnels == 0)
		{
			cardScore += 5;
			if (x > goalX && x < 12 && card.getOpenTunnels().contains(Direction.Right) && board[x + 1][y] != null
					&& board[x + 1][y].getOpenTunnels().contains(Direction.Left))
			{
				horizontalFit = true;
			}
			if (x < goalX && x > 0 && card.getOpenTunnels().contains(Direction.Left) && board[x - 1][y] != null
					&& board[x - 1][y].getOpenTunnels().contains(Direction.Right))
			{
				horizontalFit = true;
			}
			if (y == 11 && card.getOpenTunnels().contains(Direction.Down) && board[x][y + 1] != null
					&& board[x][y + 1].getOpenTunnels().contains(Direction.Up))
			{
				verticalFit = true;
			}
			if (y < 10 && y > 0 && card.getOpenTunnels().contains(Direction.Up) && board[x][y - 1] != null
					&& board[x][y - 1].getOpenTunnels().contains(Direction.Down))
			{
				verticalFit = true;
			}
			if (Math.abs(goalX - x) > Math.abs(10 - y))
			{
				if (horizontalFit)
				{
					cardScore += 3;
				}
				else if (verticalFit)
				{
					cardScore += 2;
				}
				if (horizontalFit && verticalFit)
				{
					cardScore += 0.5;
				}
			}

			else
			{
				if (verticalFit)
				{
					cardScore += 5;
				}
				else if (horizontalFit)
				{
					cardScore += 4;
				}
				if (horizontalFit && verticalFit)
				{
					cardScore += 0.5;
				}
			}
			cardScore += (19 - distance) / 9;
		}

		return cardScore;
	}

	/**
	 * Ocena karty ktora ma zostac zniszczona.
	 * 
	 * @param x
	 *            Wspolrzedna X.
	 * @param y
	 *            Wspolrzedna Y.
	 * @param card
	 *            Karta celu.
	 * @param board
	 *            Plansza do gry.
	 * @return Liczbowa ocena.
	 */
	private double calculateRemovalScore(int x, int y, TunnelCard card, TunnelCard[][] board)
	{
		double cardScore = 0;
		int goalX = 6;
		double distance;
		int numberOfOpenTunnels = 0;
		boolean horizontalFit = false;
		boolean verticalFit = false;

		int[] knowGoal = super.getGoals();

		if (knowGoal[0] == 1)
		{
			for (int i = 1; i < 4; i++)
			{
				if (knowGoal[i] == 1)
				{
					goalX = (2 * i) + 2;
				}
			}
		}
		numberOfOpenTunnels = card.getOpenTunnels().size();

		if (card.getOpenTunnels().contains(Direction.Left) && x > 0 && board[x - 1][y] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && (x - 1) == goalX)
			{
				return 10;
			}
			else if (knowGoal[((x - 1) / 2) - 2] != 2)
			{
				if (numberOfOpenTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}
		if (card.getOpenTunnels().contains(Direction.Right) && x < 12 && board[x + 1][y] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && (x + 1) == goalX)
			{
				return 10;
			}
			else if (knowGoal[((x + 1) / 2) - 2] != 2)
			{
				if (numberOfOpenTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}
		if (card.getOpenTunnels().contains(Direction.Up) && y > 0 && board[x][y - 1] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && x == goalX)
			{
				return 10;
			}
			else if (knowGoal[((x + 1) / 2) - 2] != 2)
			{
				if (numberOfOpenTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}
		if (card.getOpenTunnels().contains(Direction.Down) && y < 12 && board[x][y + 1] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && x == goalX)
			{
				return 10;
			}
			else if (knowGoal[((x + 1) / 2) - 2] != 2)
			{
				if (numberOfOpenTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}
		distance = Math.abs(goalX - x) + Math.abs(y - 10);

		if (x > goalX && card.getOpenTunnels().contains(Direction.Left) && board[x - 1][y] == null)
		{
			horizontalFit = true;
		}
		if (x < goalX && card.getOpenTunnels().contains(Direction.Right) && board[x + 1][y] == null)
		{
			horizontalFit = true;
		}
		if (y > 10 && card.getOpenTunnels().contains(Direction.Up) && board[x][y - 1] == null)
		{
			verticalFit = true;
		}
		if (y < 10 && card.getOpenTunnels().contains(Direction.Down) && board[x][y + 1] == null)
		{
			verticalFit = true;
		}
		if (Math.abs(goalX - x) > Math.abs(10 - y))
		{
			if (horizontalFit)
			{
				cardScore += 3;
			}
			else if (verticalFit)
			{
				cardScore += 2;
			}
			if (horizontalFit && verticalFit)
			{
				cardScore += 0.5;
			}
		}
		else
		{
			if (verticalFit)
			{
				cardScore += 3;
			}
			else if (horizontalFit)
			{
				cardScore += 2;
			}
			if (horizontalFit && verticalFit)
			{
				cardScore += 0.5;
			}
		}

		cardScore += numberOfOpenTunnels - 1;
		cardScore += (19 - distance) / 4;

		return cardScore;
	}

	public void updateKripke(Turn turn, int agentPlayed, TunnelCard[][] board, Player[] agents)
	{
		model.update(turn, agentPlayed, board, agents);
		if (!isSabotagingOpenly())
		{
			updateStrategy(board);
		}
	}

}
