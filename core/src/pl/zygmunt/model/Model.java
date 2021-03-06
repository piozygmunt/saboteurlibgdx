package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.graph.DefaultWeightedEdge;

import pl.zygmunt.common.Common;
import pl.zygmunt.common.Point;

/**
 * Klasa reprezntujaca model - glowna logike gry Sabotazysta.
 * 
 * @author Piotrek
 *
 */
public class Model
{

	/**
	 * Talia kart.
	 */
	private List<Card> deck;
	/**
	 * Tablica graczy.
	 */
	private List<Player> players;
	/**
	 * Aktualny rezultat gry.
	 */
	private GameResult gameResult;
	/**
	 * Plansza do gry.
	 */
	private TunnelCard[][] board = new TunnelCard[Common.initialColumns][Common.initialRows];
	/**
	 * Polozenie karty ze zlotem.
	 */
	private int goldCardPosition;
	/**
	 * Aktualna ilosc rund.
	 */
	private int turns;
	/**
	 * Lista o rozmiarze ilosci graczy. Kazdy element oznacza role gracza na
	 * danej pozycji.
	 */
	private List<Boolean> saboteurs;

	/**
	 * Ostatnio wykonany ruch przez gracza (ludzkiego).
	 */
	private Turn lastPlayerTurn;
	/**
	 * Wszystkie mozliwe stany (uklady sabotazystow).
	 */
	private List<State> possible_states;

	/**
	 * 
	 * @return Zwraca gracza ktory aktualnie zagrywa.
	 */
	public Player getActivePlayer()
	{
		return players.get(turns % GameProperties.numberOfPlayers);
	}

	/**
	 * 
	 * @return Zwraca obiekt reprezntujacy ostatnio wykonana akcje przez gracza.
	 */
	public Turn getLastTurn()

	{
		return lastPlayerTurn;
	}

	/**
	 * 
	 * @param playerID
	 *            Identyfikator gracza.
	 * @return Zwraca obiekt gracza o danym ID.
	 */
	public Player getPlayer(int playerID)
	{
		return players.get(playerID);
	}

	/**
	 * 
	 * @return Zwraca tablice do gry.
	 */
	public TunnelCard[][] getBoard()
	{
		return board;
	}

	
	public List<Player> getPlayers()
	{
		return players;
	}
 	
	public Model()
	{

		initialize();

	}

	/**
	 * Ustawienie ostatnio wykonanej akcji.
	 * 
	 * @param turn
	 */
	public void setLastTurn(final Turn turn)
	{
		this.lastPlayerTurn = turn;
	}

	/**
	 * Inicjalizacja modelu.
	 */
	public void initialize()
	{

		turns = 0;
		gameResult = GameResult.GameInProgress;
		lastPlayerTurn = null;
		deck = DeckGenerator.generateDeck();
		possible_states = StatesGenerator.generateAllPossibleStates(GameProperties.numberOfPlayers);
		players = new ArrayList<Player>();

		for (int i = 0; i < Common.initialColumns; i++)
		{
			for (int j = 0; j < Common.initialRows; j++)
			{
				board[i][j] = null;
			}

		}

		board[6][2] = new StartCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight);

		Random rand = new Random();
		int goldPosition = rand.nextInt(3) + 1;

		for (int i = 1; i < 4; ++i)
		{
			if (i == goldPosition)
			{
				board[i * 2 + 2][10] = new GoalCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight, true);
				goldCardPosition = i * 2 + 2;
			}
			else
				board[i * 2 + 2][10] = new GoalCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight, false);
		}

		saboteurs = new ArrayList<Boolean>();

		for (int i = 0; i < GameProperties.maxNumberOfDwarfs; ++i)
		{
			saboteurs.add(false);
		}

		for (int i = 0; i < GameProperties.maxNumberOfSaboteurs; ++i)
		{
			saboteurs.add(true);
		}
		Collections.shuffle(saboteurs);
		
		int j = 0;
		if(GameProperties.humanPlayer)
		{

			Player humanPlayer = new Player(0);
			humanPlayer.setRole(saboteurs.get(0));
			players.add(humanPlayer);
			j = 1;
		}
			

		for ( ; j < GameProperties.numberOfPlayers; j++)
		{
			if (saboteurs.get(j))
				players.add(new AgentSaboteur(j, possible_states));
			else
				players.add(new AgentDwarf(j, possible_states));
		}

		for (int k = 0; k < GameProperties.numberOfPlayers; k++)
		{
			for (int i = 0; i < GameProperties.numberOfCards; i++)
			{
				//players[k].getCards().add(deck.get(0));
				players.get(k).getCards().add(deck.get(0));
				deck.remove(0);
			}
		}
	}

	public String getWinners()
	{
		if (this.gameResult != GameResult.GameInProgress)
		{
			if (this.gameResult == GameResult.MinersWon)
				return new String("Miners");
			else if (this.gameResult == GameResult.SaboteursWon)
				return new String("Saboteurs");
		}
		return new String("");
	}

	public Vector<Double> getAgentsAssumptions()
	{
		double currentValue;
		DefaultWeightedEdge edge;
		int stateCounter = 0;
		boolean[] saboteurs = new boolean[this.saboteurs.size()];
		boolean containsActual = false;
		Vector<Double> result = new Vector<Double>();
		
		for(int i = 0; i < this.saboteurs.size(); ++i )
			saboteurs[i] = this.saboteurs.get(i);
		
		State actualState = new State(saboteurs);
			
		for (Player player : players)
		{
			if (player instanceof Agent)
			{
				for (State state1 : possible_states)
				{
					for (State state2 : possible_states)
					{
						if (state2 != state1)
						{
							edge = ((Agent) player).getKripkeModel().getKripkeGraphs().get(player.getID())
									.getEdge(state2, state1);
							currentValue = ((Agent) player).getKripkeModel().getKripkeGraphs().get(player.getID())
									.getEdgeWeight(edge);
							if (currentValue >= ((Agent) player).getKripkeModel().getTreshold())
							{
								if(state1.equals(actualState)) 
								{
									containsActual = true;
								}
								stateCounter++;
								break;
							}
						}
					}
				}
				result.add((double) stateCounter);
				stateCounter = 0;
				if(containsActual)
				{
					result.add(1.0);
				}
				else
				{
					result.add(1.0);
				}
			}
		}
		return result;

	}
	
	public Vector<Double> getAgentsAssuptionsAboutOtherAgents()
	{
		double currentValue;
		double currentValueInActualAgent;
		DefaultWeightedEdge edge,edge2;
		int stateCounter = 0;
		int stateCounterActual = 0;
		int stateCounterSame = 0;
		boolean statePossible = false, stateActualPossible = false;
		Vector<Double> result = new Vector<Double>();
		
		
			
		for (Player player : players)
		{
			for (Player player2 : players)
			{
				if (player2.getID() != player.getID())
				{
					for (State state1 : possible_states)
					{
						for (State state2 : possible_states)
						{
							if (state2 != state1)
							{
								edge = ((Agent) player).getKripkeModel().getKripkeGraphs().get(player2.getID())
										.getEdge(state2, state1);
								edge2 = ((Agent) player2).getKripkeModel().getKripkeGraphs().get(player2.getID())
										.getEdge(state2, state1);
								currentValue = ((Agent) player).getKripkeModel().getKripkeGraphs()
										.get(player2.getID()).getEdgeWeight(edge);
								currentValueInActualAgent = ((Agent) player2).getKripkeModel().getKripkeGraphs()
										.get(player2.getID()).getEdgeWeight(edge2);
								if (!statePossible && currentValue >= ((Agent) player).getKripkeModel().getTreshold())
								{
									stateCounter++;
									statePossible = true;
								}
								if(!stateActualPossible && currentValueInActualAgent >= ((Agent) player2).getKripkeModel().getTreshold() )
								{
									stateCounterActual++;
									stateActualPossible = true;
								}
								if( statePossible && stateActualPossible )
								{
									stateCounterSame++;
									break;
								}
								
							}
						}
						statePossible = stateActualPossible = false;
					}
					result.add((double) stateCounter);
					result.add((double) stateCounterActual);
					result.add(stateCounter > stateCounterActual ? ((double) stateCounterSame/stateCounter) : ((double) stateCounterSame/stateCounterActual));
					stateCounter = stateCounterActual = stateCounterSame =0;
					statePossible = stateActualPossible = false;
				}


			}
			}
		return result;
		}

	
	/**
	 * Wypisanie na konsoli rezultatow gry oraz przekonan agentow.
	 */
	public void printResults()
	{
		double currentValue;
		DefaultWeightedEdge edge;
		int stateCounter = 0;
		for (Player player : players)
		{
			if (player instanceof Agent)
			{
				System.out.println("Agent o ID: " + player.getID() + " wierzy, �e prawdopodobne s� stany: ");
				for (State state1 : possible_states)
				{
					for (State state2 : possible_states)
					{
						if (state2 != state1)
						{
							edge = ((Agent) player).getKripkeModel().getKripkeGraphs().get(player.getID())
									.getEdge(state2, state1);
							currentValue = ((Agent) player).getKripkeModel().getKripkeGraphs().get(player.getID())
									.getEdgeWeight(edge);
							if (currentValue >= ((Agent) player).getKripkeModel().getTreshold())
							{
								System.out.print(state1 + ", ");
								stateCounter++;
								break;
							}
						}
					}
				}
				System.out.print("Liczba mozliwych stanow: " + stateCounter);
				stateCounter = 0;

				System.out.println("\n");
				for (Player player2 : players)
				{
					if (player2.getID() != player.getID())
					{
						System.out.println("Agent o ID: " + player.getID() + " uwaza, ze agent o ID: " + player2.getID()
								+ " wierzy w stany: ");
						for (State state1 : possible_states)
						{
							for (State state2 : possible_states)
							{
								if (state2 != state1)
								{
									edge = ((Agent) player).getKripkeModel().getKripkeGraphs().get(player2.getID())
											.getEdge(state2, state1);
									currentValue = ((Agent) player).getKripkeModel().getKripkeGraphs()
											.get(player2.getID()).getEdgeWeight(edge);
									if (currentValue >= ((Agent) player).getKripkeModel().getTreshold())
									{
										System.out.print(state1 + ", ");
										stateCounter++;
										break;
									}
								}
							}
						}
						System.out.print("Liczba mozliwych stanow: " + stateCounter);
						stateCounter = 0;
						System.out.println("\n");
					}
				}
				System.out.println("\n");
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for (int i = 0; i < saboteurs.size(); ++i)
			if (saboteurs.get(i))
				builder.append("T, ");
			else
				builder.append("F, ");
		builder.delete(builder.length() - 2, builder.length());
		builder.append(" )");

		System.out.println("Prawdziwy stan gry: " + builder.toString());

		if (gameResult.equals(GameResult.MinersWon))
		{
			System.out.println("Wygrali: kopacze");
		}
		else
		{
			System.out.println("Wygrali: sabotazysci");
		}

	}

	/**
	 * Sprawdzenie czy spelnione sa warunki konca gry: 1. Dotarcie przez kopaczy
	 * do karty ze zlotem. 2. Brak kart w ktoregos z graczy + pusta talia.
	 * 
	 * @return Prawda/falsz czy gra zostala zakonczona.
	 */
	public boolean checkForEndGame()
	{

		boolean empty = true;
		boolean[][] recursionBoard = new boolean[Common.initialColumns][Common.initialRows];
		for (int x = 0; x < Common.initialColumns; x++)
		{
			for (int y = 0; y < Common.initialRows; y++)
			{
				recursionBoard[x][y] = false;
			}
		}

		if (Common.linksToStart(board, goldCardPosition, 10, recursionBoard))
		{
			gameResult = GameResult.MinersWon;
		}

		if (gameResult.equals(GameResult.GameInProgress))
		{
			for (int j = 0; j < GameProperties.numberOfPlayers; j++)
			{
				if (players.get(j).getCards().isEmpty() == false)
				{
					empty = false;
				}
			}
			if (empty == true)
			{
				gameResult = GameResult.SaboteursWon;
			}
		}
		return gameResult.equals(GameResult.GameInProgress) ? false : true;
	}

	/**
	 * Usuniecie karty posiadanych przez danego gracza.
	 * 
	 * @param card
	 *            Karta ktora chcemy usunac.
	 * @param playerID
	 *            Identyfikator gracza ktoremu usuwamy karte.
	 */
	public void removeCardFromPlayer(Card card, int playerID)
	{
		players.get(playerID).getCards().remove(card);
	}

	/**
	 * Uaktualnienie modelu wraz z modelami Kripkego dla kazdego agenta.
	 * 
	 * @param turn
	 *            Obiekt reprezentujaca wyknana akcje na postawie ktorego
	 *            wykonywane jest uaktualnienie.
	 */
	public void updateModel(Turn turn)
	{
		int currentAgent = getActivePlayer().getID();
		Card playCard = null;

		// jesli zostala zagrana karta
		if (turn != null)
		{
			playCard = turn.getCard();
			Point targetBoard = turn.getBoardTarget();
			int agentTarget = turn.getAgentTarget();
			if (playCard != null)
			{
				// update modelu Kripkego dla kazdego agenta
				for (Player player : players)
				{
					if (player instanceof Agent)
						((Agent) player).updateKripke(turn, currentAgent, board, players);
				}
			}
			if (playCard instanceof TunnelCard)
			{
				placeTunnelCardOnBoard((TunnelCard) playCard, targetBoard.getX(), targetBoard.getY());
			}
			else if (playCard instanceof BlockCard)
			{
				block(players.get(agentTarget));
			}
			else if (playCard instanceof DeblockCard)
			{
				deblock(players.get(agentTarget));
			}
			else if (playCard instanceof DemolishCard)
			{
				demolish(targetBoard.getX(), targetBoard.getY());
			}
			else
			{
				players.get(currentAgent).setGoal((targetBoard.getX() / 2) - 2, view(targetBoard.getX()));
			}
		}

		/*
		 * Jesli talia nie jest jeszcze pusta to gracz ktory wlasnie zagrywal
		 * dobiera karte.
		 */
		if (!deck.isEmpty())
		{
			players.get(currentAgent).getCards().add(deck.get(0));
			deck.remove(0);
		}

	}

	/**
	 * Inkrementacja ilosci tur. Na podstawie numeru tur obliczany jest
	 * identyfikator aktualnego gracza.
	 */
	public void incrementTurn()
	{
		turns++;
	}

	/**
	 * Wykonanie ruchu przez nastepnego agenta oraz aktualizacja modelu.
	 * 
	 * @return Zwraca obiekt reprezentujacy wykonanny ruch.
	 */
	public Turn nextTurn()
	{
		int currentPlayer = getActivePlayer().getID();
		Turn turn = ((Agent) players.get(currentPlayer)).takeTurn(players, board);
		updateModel(turn);
		return turn;
	}

	/**
	 * Umieszczenie karty tuneli na planszy.
	 * 
	 * @param pathCard
	 *            Umieszczana karta
	 * @param x
	 *            Wspolrzedna X planszy.
	 * @param y
	 *            Wspolrzedna Y planszy.
	 */
	public void placeTunnelCardOnBoard(TunnelCard pathCard, int x, int y)
	{
		board[x][y] = pathCard;

		// sprawdzenie czy mozna dotrzec do ktorejs z kart celu
		for (int i = 1; i < 4; ++i)
		{
			if (!((GoalCard) board[i * 2 + 2][10]).isDiscovered() && isGoalCardReachable(i * 2 + 2))
			{
				for (int j = 0; j < players.size(); ++j)
					players.get(j).setGoal(i, ((GoalCard) board[i * 2 + 2][10]).getGold());
			}
		}

	}

	/**
	 * Zwraca Set wspolrzednych kart celu ktore sa odsloniete.
	 * 
	 * @return Set wspolrzednych.
	 */
	public Set<Integer> getDiscoveredGoalCardPositions()
	{
		Set<Integer> discovered = new HashSet<Integer>();

		// sprawdzamy kazda celu pod kartem jej widocznosci i dodajemy do seta
		for (int i = 1; i < 4; ++i)
			if (((GoalCard) board[i * 2 + 2][10]).isDiscovered())
			{
				discovered.add(i * 2 + 2);

			}

		return discovered;
	}

	/**
	 * Blokada konkretnego gracza.
	 * 
	 * @param player
	 *            Gracz ktory ma zostac zablokowany.
	 */
	public void block(Player player)
	{
		player.setBlocked(true);
	}

	/**
	 * Odblokowanie konkretnego gracza.
	 * 
	 * @param player
	 *            Gracz ktory ma zostac odblokowany.
	 */
	public void deblock(Player player)
	{
		player.setBlocked(false);
	}

	/**
	 * Zniszczenie (usuniecie z planszy) jednej z kart tunneli.
	 * 
	 * @param x
	 *            Wspolrzedna X karty do usuniecia.
	 * @param y
	 *            Wspolrzedna Y karty do usuniecia.
	 */
	public void demolish(int x, int y)
	{
		board[x][y] = null;
	}

	/**
	 * Sprawdzenie czy dana karta celu zawwiera zloto.
	 * 
	 * @param x
	 *            Wspolrzedna X karty celu (wspolrzedna Y zawsze 10)
	 * @return Prawda / falsz w zaleznosci czy jest zloto.
	 */
	public boolean view(int x)
	{
		return ((GoalCard) board[x][10]).getGold();
	}

	/**
	 * Sprawdzenie czy dana karta celu jest osiagalna (czy jest zbudowany tunel
	 * do startu do tej karty.
	 * 
	 * @param x
	 *            Wspolrzedna X karty celu (wspolrzedna y zawsze 10)
	 * @return Prawda / falsz w zaleznosci czy karta jest osiagalna.
	 */
	public boolean isGoalCardReachable(int x)
	{
		boolean[][] recursionBoard = new boolean[Common.initialColumns][Common.initialRows];
		for (int i = 0; i < Common.initialColumns; ++i)
			for (int j = 0; j < Common.initialRows; ++j)
				recursionBoard[j][i] = false;

		if (Common.linksToStart(board, x, 10, recursionBoard))
		{
			((GoalCard) board[x][10]).setDiscovered(true);
			return true;
		}
		return false;
	}

	public int getGoldCardPosition()
	{
		return goldCardPosition;
	}
	
	public GameResult getGameResult()
	{
		return gameResult;
	}
	
	public List<Boolean> getSaboteurs()
	{
		return saboteurs;
	}
}
