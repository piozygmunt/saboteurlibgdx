package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import pl.zygmunt.common.Common;
import pl.zygmunt.common.Direction;
import pl.zygmunt.common.Point;

/**
 * Klasa reprezentuj¹ca agenta (kopacza) w grze.
 * 
 * @author Piotr Zygmunt.
 *
 */
public class AgentDwarf extends Agent
{

	/**
	 * Konstruktor klasy.
	 * 
	 * @param id
	 *            Identyfikator agenta.
	 */
	public AgentDwarf(int id, List<State> possibleStates)
	{
		super(id);
		treshold = 4;
		model = new KripkeModel(id, false, possibleStates);
	}

	@Override
	/**
	 * Przedefiniowana funkcja odpowiedzialna na wykonanie ruchu.
	 */
	public Turn takeTurn(List<Player> players, TunnelCard[][] board)
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

		// iteracja po wszystkich kartach i ich wycena
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
		// jesli najlepsza karta ma wynik wiekszy niz okreslony
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

		// w p.p odrzucamy jedna z kart
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

	private void discard(List<Player> players, TunnelCard[][] board)
	{
		double worstValue = 10;
		int worstCard = 0;
		List<Card> playerCards = super.getCards();

		for (int i = 0; i < playerCards.size(); i++)
		{
			CardScore currentScore = rateCard(playerCards.get(i), players, true, board);
			if (currentScore.getScore() < worstValue)
			{
				worstValue = currentScore.getScore();
				worstCard = i;
			}
		}

		playerCards.remove(worstCard);
	}

	/**
	 * Wycena danej karta
	 * 
	 * @param card
	 *            Karta.
	 * @param players
	 *            Lista graczy.
	 * @param board
	 *            Plansza do gry.
	 * @return Obiekt repezentujacy wycene danej karty.
	 */
	private CardScore rateCard(Card card, List<Player> players, TunnelCard[][] board)
	{
		return rateCard(card, players, false, board);
	}

	/**
	 * Przedefiniowana funkcja wyceniajaca dana karte.
	 * 
	 * @param card
	 *            Karta.
	 * @param players
	 *            Lista graczy.
	 * @param discard
	 *            Czy dana karta ma byc usunieta.
	 * @param board
	 *            Plansza do gry.
	 * @return Obiekt reprezentujacy wyecene danej karty.
	 */
	private CardScore rateCard(Card card, List<Player> players, boolean discard, TunnelCard[][] board)
	{
		CardScore score = new CardScore();
		int id = super.getID();
		int[] knowGoal = super.getGoals();
		Set<Integer> possible_saboteurs;

		// jesli jest to karta tunneli
		if (card instanceof TunnelCard)
		{
			// jesli nie jestesmy zablokowani lub chcemy odrzucic karte
			if (super.getBlocked() == false || discard)
			{
				score = calculatePath(card, board);
			}

		}

		// jesli jest to blokujaca karta
		else if (card instanceof BlockCard)
		{
			possible_saboteurs = new HashSet<Integer>();

			for (int i = 0; i < GameProperties.maxNumberOfSaboteurs; ++i)
			{
				// znajdz najbardziej podejrzanego agenta
				double suspicion[] = model.getSuspicion(id, possible_saboteurs);
				// jesli nie jest zablokowany
				if (players.get((int) suspicion[1]).getBlocked() == false)
				{
					score.setScore(((suspicion[0] / suspicion[2]) * 10) - 2);
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
		// jesli jest to karta odblokowania
		else if (card instanceof DeblockCard)
		{
			// jesli jestesmy zablokowani
			if (super.getBlocked())
			{
				score.setScore(8);
				score.setPlayerIDTarget(id);
			}

			else
			{

				possible_saboteurs = new HashSet<Integer>();
				for (int i = 0; i < GameProperties.maxNumberOfSaboteurs; ++i)
				{
					// znajdujemy gracza najmniej podejrzanego
					double suspicion[] = model.getLeastSuspicious(id, possible_saboteurs);

					// jesli jest zablokowany
					if (players.get((int) suspicion[1]).getBlocked())
					{
						score.setScore(((suspicion[0] / suspicion[2]) * 10) - 2);
						score.setPlayerIDTarget((int) suspicion[1]);
						break;
					}
					// jesli nie jest zablokowany dodajemy do listy wykluczenia i szukamy jeszcze raz
					else
					{
						possible_saboteurs.add((int) suspicion[1]);
					}
				}
			}

		}
		// jesli jest to karta niszczenia
		else if (card instanceof DemolishCard)
		{
			double currentValue = 0;
			// iterujemy po calej tablicy
			for (int i = 0; i < Common.initialColumns; i++)
			{
				for (int j = 0; j < Common.initialRows; j++)
				{
					// jesli pole nie jest puste i nie lezy tam karta poczatku
					// ani celu
					if (board[i][j] != null && !(board[i][j] instanceof GoalCard)
							&& !(board[i][j] instanceof StartCard))
					{
						// obliczamy wartosc
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
		// karta widoku
		else
		{
			// jesli nie znamy celu
			if (knowGoal[0] == 0)
			{
				// chcemy go poznac 
				score.setScore(9);
				//losujemy karte do sprawdzenia
				Random rand = new Random();
				int checkGoal = rand.nextInt(3) + 1;

				// jesli nie znamy celu
				if (knowGoal[checkGoal] == 0)
				{
					score.setBoardTarget(new Point((checkGoal * 2) + 2, 10));
				}
				// jesli znamy bierzemy nastepna
				else
				{
					score.setBoardTarget(new Point((((checkGoal % 3) + 1) * 2) + 2, 10));
				}
			}
		}

		return score;
	}

	/**
	 * Funkcja resetujaca model Kripkego.
	 * 
	 * @param possibleStates
	 *            Wszystkie mozliwe stany.s
	 */
	public void resetKripke(List<State> possibleStates)
	{
		model = new KripkeModel(super.getID(), false, possibleStates);
	}

	/**
	 * Wycena karty tuneli
	 * 
	 * @param card
	 *            Karta
	 * @param board
	 *            Plansza do gry
	 * @return Obiekt reprezentujacy ocene.
	 */
	private CardScore calculatePath(Card card, TunnelCard[][] board)
	{
		TunnelCard pathcard = (TunnelCard) card;
		double currentValue;
		CardScore score = new CardScore();
		score.setScore(-1);

		// iteracja po calej planszy
		for (int i = 0; i < Common.initialColumns; i++)
		{
			for (int j = 0; j < Common.initialRows; j++)
			{

				// sprawdzenie czy karta moze byc tam ulozona
				if (Common.checkIfCardCanBePlaced(board, pathcard, i, j))
				{

					// tablica okreslajaca czy juz przeszlismy przez dane pole
					// na planszy
					boolean[][] recursionBoard = new boolean[Common.initialColumns][Common.initialRows];
					for (int o = 0; o < Common.initialColumns; o++)
					{
						for (int p = 0; p < Common.initialRows; p++)
						{
							recursionBoard[o][p] = false;
						}
					}

						// umiejscowienie karty
						board[i][j] = pathcard;

						/// sprawdzenie czy karta prowadzi do poczatku
						if (Common.linksToStart(board, i, j, recursionBoard))
						{
							// dalsza wycena
							currentValue = calculatePathScore(i, j, pathcard, board);
							// jesli jest lepsza niz dotychczasowa
							if (currentValue > score.getScore())
							{
								score.setScore(currentValue);
								score.setBoardTarget(new Point(i, j));
							}
						}
						board[i][j] = null;
					}
				}
			}

		return score;
	}

	/**
	 * Dokladniejsza wycena karty tuneli. Wywolywana po upewnieniu ze karta moze
	 * byc polozona na danym miejscu
	 * 
	 * @param x
	 *            Wspolrzedna X.
	 * @param y
	 *            Wspolrzedna Y.
	 * @param card
	 *            Karta.
	 * @param board
	 *            Plansza do gry.
	 * @return Wycena w postaci liczby.
	 */
	private double calculatePathScore(int x, int y, TunnelCard card, TunnelCard[][] board)
	{
		double cardScore = 0;
		int goalX = 6;
		double distance;
		int amountOfTunnels = 0;
		boolean horizontalFit = false;
		boolean verticalFit = false;
		int[] knowGoal = super.getGoals();

		// jesli wiemy gdzie jest cel to ustawiamy go
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

		amountOfTunnels = card.getTunnels().size();

		// jesli jestesmy po prawej stronie od karty celu
		if (card.getOpenTunnels().contains(Direction.Left) && x > 0 && board[x - 1][y] instanceof GoalCard)
		{
			// jesli jest to nasz cel
			if (knowGoal[0] == 1 && (x - 1) == goalX)
			{
				return 10;
			}

			// jesli nie ma tam kamienia
			else if (knowGoal[((x - 1) / 2) - 2] != 2)
			{
				if (amountOfTunnels > 2)
				{
					return 10;
				}

				else
				{
					return 9;
				}
			}
		}

		// analogicznie - chcemy umiescic karte po lewej stronie
		if (card.getOpenTunnels().contains(Direction.Right) && x < 12 && board[x + 1][y] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && (x + 1) == goalX)
			{
				return 10;
			}
			else if (knowGoal[((x + 1) / 2) - 2] != 2)
			{
				if (amountOfTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}

		// jesli jestesmy pod karta celu
		if (card.getOpenTunnels().contains(Direction.Up) && y > 0 && board[x][y - 1] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && x == goalX)
			{
				return 10;
			}
			else if (knowGoal[(x / 2) - 2] != 2)
			{
				if (amountOfTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}

		// jesli jestesmy nad karta celu
		if (card.getOpenTunnels().contains(Direction.Down) && y < 12 && board[x][y + 1] instanceof GoalCard)
		{
			if (knowGoal[0] == 1 && x == goalX)
			{
				return 10;
			}
			else if (knowGoal[(x / 2) - 2] != 2)
			{
				if (amountOfTunnels > 2)
				{
					return 10;
				}
				else
				{
					return 9;
				}
			}
		}

		// dystans do celu
		distance = Math.abs(goalX - x) + Math.abs(y - 10);

		// sprawdzenie czy karta ma dobra ulozenie wzgledem celu
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

		// jesli jestemy dalej w poziomie juz w pionie
		if (Math.abs(goalX - x) > Math.abs(10 - y))
		{
			// i poziomo pasuje
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
		// w p.p.
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

		cardScore += (amountOfTunnels - 1) / 2;
		cardScore += (19 - distance) / 4;
		return cardScore;
	}

	/**
	 * Funkcja obliczajaca ocene usuniecia danej karty z planszy.
	 * 
	 * @param x
	 *            Wspolrzedna X.
	 * @param y
	 *            Wspolrzedna Y.
	 * @param card
	 *            Karta.
	 * @param board
	 *            Plansza do gry.
	 * @return Wynik w postacie liczby.
	 */
	private double calculateRemovalScore(int x, int y, TunnelCard card, TunnelCard[][] board)
	{
		double cardScore = 0;
		int goalX = 6;
		double distance;
		int numberOfTunnels = 0;
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

		// obliczamy dystans i ilosc otwartych tuneli karty
		distance = Math.abs(goalX - x) + Math.abs(y - 10);
		numberOfTunnels = card.getOpenTunnels().size();
		cardScore += 8 - 2 * numberOfTunnels;
		cardScore += ((19 - distance) / 9);
		return cardScore - 4;
	}

	@Override
	public void updateKripke(Turn turn, int agentPlayed, TunnelCard[][] board, List<Player> agents)
	{
		model.update(turn, agentPlayed, board, agents);
		model.updateBeliefsAccordingToLeastSuspiciousAgent(super.getID());
	}

}