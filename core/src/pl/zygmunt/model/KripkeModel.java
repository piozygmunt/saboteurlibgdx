package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import pl.zygmunt.common.Direction;
import pl.zygmunt.common.Point;

/**
 * Klasa reprezentujaca model Kripkego oraz zwiazane z nim funkcje.
 * 
 * @author Piotrek
 *
 */
public class KripkeModel
{
	/**
	 * Model kazdego agenta zawiera grafy dla kazdego gracza.
	 */
	private List<SimpleDirectedWeightedGraph<State, DefaultWeightedEdge>> kripkeGraphs;
	/**
	 * Wszystkie mozliwe stany w grze.
	 */
	private List<State> possibleStates;
	/**
	 * Wartosc okresla kiedy stan uznajemy za mozliwy.
	 */
	private double treshold = 0.0;

	/**
	 * Konstruktor.
	 * 
	 * @param id
	 *            Identyfikator gracza.
	 * @param saboteur
	 *            Czy jest sabotazysta.
	 * @param possibleStates
	 *            Wszystkie mozliwe stany.
	 */
	public KripkeModel(int id, boolean saboteur, List<State> possibleStates)
	{
		kripkeGraphs = new ArrayList<SimpleDirectedWeightedGraph<State, DefaultWeightedEdge>>();

		this.possibleStates = possibleStates;

		// stworzenie grafu dla kazdego gracza i dodanie wszystkich mozliwych
		// stanow
		for (int i = 0; i < GameProperties.numberOfPlayers; ++i)
		{
			kripkeGraphs.add(new SimpleDirectedWeightedGraph<State, DefaultWeightedEdge>(DefaultWeightedEdge.class));
			for (State state : possibleStates)
			{
				kripkeGraphs.get(i).addVertex(state);
			}
		}

		// poczatkowa inicjalizacja wartosci na krawedziach w grafie
		// dla kazdego grafu
		for (int i = 0; i < GameProperties.numberOfPlayers; ++i)
		{
			// jesli inicjujemy wlasny graf i jestesmy sabotazystami
			if (i == id && saboteur)
			{
				// dla kazdej mozliwej pary stanow (z wykluczeniem 2 takich
				// samych stanow)
				for (State state1 : possibleStates)
				{
					for (State state2 : possibleStates)
					{
						if (state1 != state2)
						{
							// jesli jestesmy sabotazystami
							if (state2.isPlayerSaboteur(id))
							{
								// stany sa bardziej prawdopodobne
								kripkeGraphs.get(i).setEdgeWeight(kripkeGraphs.get(i).addEdge(state1, state2),
										getTreshold() + 2);
							}
							else
							{
								// stany w ktorych nie jestesmy sabotazystami sa
								// wrecz niemozliwe
								kripkeGraphs.get(i).setEdgeWeight(kripkeGraphs.get(i).addEdge(state1, state2), -100);
							}
						}
					}
				}
			}
			// w p.p.
			else
			{
				for (State state1 : possibleStates)
				{
					for (State state2 : possibleStates)
					{
						if (state1 != state2)
						{
							if (!state2.isPlayerSaboteur(i))
							{

								kripkeGraphs.get(i).setEdgeWeight(kripkeGraphs.get(i).addEdge(state1, state2),
										getTreshold() + 2);
							}
							else
							{
								kripkeGraphs.get(i).setEdgeWeight(kripkeGraphs.get(i).addEdge(state1, state2), -100);
							}
						}
					}
				}
			}
		}

	}

	/**
	 * Aktualizacja modelu Kripkego ( wartosci dla grafow), gdy ruch uwzglednial
	 * tylko jednego gracza.
	 * 
	 * @param playerIDPlayed
	 *            Identyfikator gracza ktory wykonal ruch.
	 * @param updateValue
	 *            Wartosc o jaka aktualizujemy grafy.
	 */
	public void updateKripkeGraphs(int playerIDPlayed, int updateValue)
	{
		double currentValue;
		DefaultWeightedEdge edge;

		// aktualizacja dla kazdego grafu gracza
		for (int i = 0; i < GameProperties.numberOfPlayers; i++)
		{
			// oprocz gracza ktory zagrywal
			if (i != playerIDPlayed)
			{
				for (State state1 : possibleStates)
					for (State state2 : possibleStates)
					{
						// jesli stany sa rozne
						if (state1 != state2)
						{
							edge = kripkeGraphs.get(i).getEdge(state1, state2);
							currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);
							// jesli agent zagrywajacy jest kopaczem
							if (!state2.isPlayerSaboteur(playerIDPlayed))
							{
								if (updateValue > 0)
								{
									kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + updateValue);
								}
								else
								{
									kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 5 * updateValue);
								}
							}
							// jesli jest sabotazysta
							else
							{
								if (updateValue < 0)
								{
									kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - updateValue);
								}
								else
								{
									kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 5 * updateValue);
								}
							}
						}
					}
			}
		}
	}

	/**
	 * Aktualizacja modelu Kripkego (kazdego grafu), gdy ruch uwzglednial 2
	 * graczy (blokowanie / odblokowywanie).
	 * 
	 * @param player1ID
	 *            Identyfikator pierwszego gracza.
	 * @param player2ID
	 *            Identyfikator drugiego gracza.
	 * @param updateValue
	 *            Wartosc o ktora aktualizujemy model.
	 */
	public void updateKripkeGraphs(int player1ID, int player2ID, int updateValue)
	{
		double currentValue;
		DefaultWeightedEdge edge;
		for (int i = 0; i < GameProperties.numberOfPlayers; i++)
		{
			for (State state1 : possibleStates)
				for (State state2 : possibleStates)
				{
					if (state1 != state2)
					{
						edge = kripkeGraphs.get(i).getEdge(state1, state2);
						currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);
						// dla stanow w ktorych gracza maja ta sama role
						if (state2.haveSameRole(player1ID, player2ID))
						{
							kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + updateValue);
						}
						// jesli maja rozna role
						else
						{
							kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 1.5 * updateValue);
						}
					}
				}
		}
	}

	// update beliefs/relations for all agents in the model of the agent who
	// called this function, as a result of the last turn
	/**
	 * Aktualizacja przekonan dla kazdego z graczy w modelu agenta ktory wywolal
	 * funkcje na postawie ostatniego ruchu.
	 * 
	 * @param turn
	 *            Obiekt reprezentujacy ostatni ruch (ture).
	 * @param playerIDPlayed
	 *            Identyfikator gracza ktory zagrywal karte.
	 * @param board
	 *            Aktualna plansza do gry.
	 * @param players
	 *            Tablica graczy bioracych udzial w rozgrywce.
	 */
	public void update(Turn turn, int playerIDPlayed, TunnelCard[][] board, Player[] players)
	{
		Card playCard = turn.getCard();
		Point targetBoard = turn.getBoardTarget();
		int playerIDReceived = turn.getAgentTarget();

		// jesli zostala zagrana karta usuwajaca inna karte z planszy
		if (playCard instanceof DemolishCard)
		{
			boolean opened = false;

			if (board[targetBoard.getX()][targetBoard.getY()].getOpenTunnels().size() > 0)
				opened = true;

			// jesli usunieta karta nie zawierala otwartych tuneli (nie byla
			// przydatna)
			if (opened == false)
			{
				updateKripkeGraphs(playerIDPlayed, 1);
			}

			// jesli usunieta karta zawierala otwarte tunele, wartosc
			// aktualizacji zalezy od odleglosci karty od celu
			else
			{
				updateKripkeGraphs(playerIDPlayed, (-1 - closerToGoal(board[targetBoard.getX()][targetBoard.getY()],
						targetBoard, board, players[playerIDPlayed])));
			}

		}
		// jesli zostala zagrana karta tuneli
		else if (playCard instanceof TunnelCard)
		{
			// aktualizacja zalezy od odleglosci od celu
			TunnelCard pathCard = (TunnelCard) playCard;
			updateKripkeGraphs(playerIDPlayed, closerToGoal(pathCard, targetBoard, board, players[playerIDPlayed]));

		}

		// jesli zostala zagran akarta blokowan
		else if (playCard instanceof BlockCard)
		{
			// agentReceived zostala zablokowany => malo prawdopodobne ze gracz
			// zagrywajacy ma ta sama role
			updateKripkeGraphs(playerIDPlayed, playerIDReceived, -2);

		}

		// jesli zostala zagrana karta odblokowan
		else if (playCard instanceof DeblockCard)
		{
			// ilosc zablokowanych graczy
			int numberOfPlayersBlocked = 0;
			for (int i = 0; i < GameProperties.numberOfPlayers; i++)
			{
				if (players[i].getBlocked())
				{
					numberOfPlayersBlocked++;
				}
			}

			// jesli zablokowany byl tylko jeden gracz
			if (numberOfPlayersBlocked == 1)
			{
				updateKripkeGraphs(playerIDPlayed, playerIDReceived, 1);
			}

			// jesli bylo wiecej zablokowanych graczy
			else
			{
				// jeden z nich zostal odblokowany => wieksza szansa ze maja ta
				// sama role
				updateKripkeGraphs(playerIDPlayed, playerIDReceived, 2);

				// dla kazdego innego zablokowanego gracza
				for (int i = 0; i < GameProperties.numberOfPlayers; i++)
				{
					if (i != playerIDPlayed && i != playerIDReceived && players[i].getBlocked())
					{
						updateKripkeGraphs(playerIDPlayed, i, -1);
					}
				}
			}

		}

		// jesli zostala zagrana karta widoku
		else if (playCard instanceof ViewCard)
		{
			boolean someoneKnows = false;
			for (int i = 0; i < GameProperties.numberOfPlayers; i++)
			{
				// jesli wczesniej ktos sprawdzal ta karte
				if (i != playerIDPlayed && players[i].knowsGoal((targetBoard.getX() / 2) - 2) != 0)
				{
					someoneKnows = true;
				}
			}
			double currentValue;
			DefaultWeightedEdge edge;

			// jesli karta byla sprawdzana wczesniej
			if (someoneKnows)
			{
				for (int i = 0; i < GameProperties.numberOfPlayers; i++)
				{
					// dla kazdego gracza ktory zna zawartosc danej karty celu
					if (i != playerIDPlayed && players[i].knowsGoal((targetBoard.getX() / 2) - 2) != 0)
					{
						for (State state1 : possibleStates)
						{
							for (State state2 : possibleStates)
							{
								if (state1 != state2)
								{
									edge = kripkeGraphs.get(i).getEdge(state1, state2);
									currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);

									// w stanach w ktorych gracz zagrywajacy i
									// gracz ktory sprawdzal dana karte maja ta
									// sama role
									if (state2.haveSameRole(i, playerIDPlayed))
									{
										// jesli 'mowia' to samo tzn ze
										// prawdopodobnie maja ta sama role
										if (players[i]
												.knowsGoal((targetBoard.getX() / 2) - 2) == players[playerIDPlayed]
														.knowsGoal((targetBoard.getX() / 2) - 2))
										{
											kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 1);

										}
										// w p.p.
										else
										{
											kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 2);
										}
									}
									// w stanach w ktorych maja rozne role
									else
									{
										// jesli 'mowia' to samo
										if (players[i]
												.knowsGoal((targetBoard.getX() / 2) - 2) == players[playerIDPlayed]
														.knowsGoal((targetBoard.getX() / 2) - 2))
										{
											kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 1);
										}
										else
										{
											kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 1);
										}
									}
								}
							}
						}
					}

					// aktualizacja dla agenta ktory zagrywal karte celu
					else if (i == playerIDPlayed)
					{
						for (State state1 : possibleStates)
						{
							for (State state2 : possibleStates)
							{
								for (int j = 0; j < GameProperties.numberOfPlayers; j++)
								{

									if (state1 != state2)
									{

										edge = kripkeGraphs.get(i).getEdge(state1, state2);
										currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);

										// jesli gracza maja te sama role
										if (state2.haveSameRole(i, j))
										{
											// jesli 'mowia' to samo
											if (players[i].knowsGoal((targetBoard.getX() / 2) - 2) == players[j]
													.knowsGoal((targetBoard.getX() / 2) - 2))
											{
												kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 1);
											}
											else
											{
												kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 2);
											}
										}
										else
										{
											if (players[i].knowsGoal((targetBoard.getX() / 2) - 2) == players[j]
													.knowsGoal((targetBoard.getX() / 2) - 2))
											{
												kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 1);
											}
											else
											{
												kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 1);
											}
										}
									}
								}
							}
						}
					}

					// przekonanie innych agentow o graczu ktory zagrywal karte
					// oraz innych graczy ktory widzieli karte tez musza byc
					// zmienione
					else
					{ // relations of other agents about agentPlayed and another
						// agent who has seen the goal card, have to be updated
						// too
						for (int j = 0; j < GameProperties.numberOfPlayers; j++)
						{
							if (j != i && j != playerIDPlayed
									&& players[j].knowsGoal((targetBoard.getX() / 2) - 2) != 0)
							{
								if (players[j].knowsGoal((targetBoard.getX() / 2) - 2) == players[playerIDPlayed]
										.knowsGoal((targetBoard.getX() / 2) - 2))
								{
									for (State state1 : possibleStates)
									{
										for (State state2 : possibleStates)
										{
											if (state1 != state2)
											{
												edge = kripkeGraphs.get(i).getEdge(state1, state2);
												currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);
												if (state2.haveSameRole(j, playerIDPlayed))
												{
													kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 1);
												}
												else
												{
													kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 1);
												}
											}
										}
									}
								}
								else
								{
									for (State state1 : possibleStates)
									{
										for (State state2 : possibleStates)
										{
											if (state1 != state2)
											{

												edge = kripkeGraphs.get(i).getEdge(state1, state2);
												currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);
												if (state2.haveSameRole(j, playerIDPlayed))
												{
													kripkeGraphs.get(i).setEdgeWeight(edge, currentValue - 2);
												}
												else
												{
													kripkeGraphs.get(i).setEdgeWeight(edge, currentValue + 1);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Funkcja uzywana przez kopaczy, ktory czasem przejmuja przekonania innych,
	 * najmniej podejrzanych graczy.
	 * 
	 * @param agentID
	 *            Identyfikator gracza.
	 */
	public void updateSecondOrderBeliefs(int agentID)
	{
		boolean reachable = false;
		double[] leastSuspcion = getLeastSuspicious(agentID);
		double currentValue;
		DefaultWeightedEdge edge;

		// najmniej podejrzany agent musi byc kopaczem w wiecej niz 1 staniem i
		// sabotazysta w mniej niz 2 stanach
		if (leastSuspcion[0] > 1 && (leastSuspcion[2] - leastSuspcion[0]) < 2)
		{
			for (State state1 : possibleStates)
			{
				for (State state2 : possibleStates)
				{
					if (state1 != state2)
					{
						edge = kripkeGraphs.get((int) leastSuspcion[1]).getEdge(state2, state1);
						currentValue = kripkeGraphs.get((int) leastSuspcion[1]).getEdgeWeight(edge);

						// jesli jest polaczenie
						if (currentValue >= getTreshold() && !state1.equals(state2))
						{
							reachable = true;
							break;
						}
					}
				}
				for (State state2 : possibleStates)
				{
					if (state1 != state2)
					{
						edge = kripkeGraphs.get((int) leastSuspcion[1]).getEdge(state2, state1);
						currentValue = kripkeGraphs.get((int) leastSuspcion[1]).getEdgeWeight(edge);
						if (reachable)
						{
							kripkeGraphs.get(agentID).setEdgeWeight(edge, currentValue + 0.5);
						}
						else
						{
							kripkeGraphs.get(agentID).setEdgeWeight(edge, currentValue - 0.5);
						}
					}
				}

				reachable = false;
			}
		}
	}

	/**
	 * Funkcja okreslajaca ktory gracz jest najbardziej podejrzany przez agenta.
	 * 
	 * @param agentID
	 *            Identyfikator agenta.
	 * @return Zwraca tablice w postaci: 0 - wartosc 'podejrzliwosci' danego
	 *         gracza, 1 - identyfikator gracza, 2 - liczba osiagalnych stanow
	 */
	public double[] getSuspicion(int agentID)
	{
		return getSuspicion(agentID, null);
	}

	/**
	 * Przedefiniowana funkcja okreslajaca ktory gracz jest najbardziej
	 * podejrzany z wylaczeniem tych ktorych identyfikatory znajduja sie w secie
	 * possible_saboteurs.
	 * 
	 * @param agentID
	 *            Identyfikator agenta.
	 * @param possible_saboteurs
	 *            Set zawierajacy identyfikatory graczy ktorych nie
	 *            uwzgledniamy.
	 * @return Zwraca tablice w postaci: 0 - wartosc 'podejrzliwosci' danego
	 *         gracza, 1 - identyfikator gracza, 2 - liczba osiagalnych stanow
	 */
	public double[] getSuspicion(int agentID, Set<Integer> possible_saboteurs)
	{
		double mostSuspicion[] = new double[] { -1, -1, -1 };
		boolean reachable = false;
		double suspicions[] = new double[GameProperties.numberOfPlayers + 1];

		for (int i = 0; i < suspicions.length; ++i)
			suspicions[i] = 0;

		double currentValue;
		DefaultWeightedEdge edge;

		for (State state1 : possibleStates)
		{
			for (State state2 : possibleStates)
			{
				if (state1 != state2)
				{
					edge = kripkeGraphs.get(agentID).getEdge(state2, state1);
					currentValue = kripkeGraphs.get(agentID).getEdgeWeight(edge);
					if (currentValue >= getTreshold() && !state1.equals(state2))
					{
						reachable = true;
						suspicions[GameProperties.numberOfPlayers]++;
						break;
					}
				}
			}
			// dla kazdego stanu ktory jest osiagalny
			if (reachable)
			{
				for (int k = 0; k < GameProperties.numberOfPlayers; k++)
				{
					if (agentID != k && possible_saboteurs != null && !possible_saboteurs.contains(k)
							&& state1.isPlayerSaboteur(k))
					{
						suspicions[k]++;
					}
				}
			}
			reachable = false;
		}
		int j;
		for (int i = 0; i < GameProperties.numberOfPlayers; i++)
		{
			j = (i + agentID) % GameProperties.numberOfPlayers;
			if (suspicions[j] > mostSuspicion[0])
			{
				mostSuspicion[0] = suspicions[j];
				mostSuspicion[1] = j;
			}
		}
		mostSuspicion[2] = suspicions[GameProperties.numberOfPlayers];

		return mostSuspicion;
	}

	/**
	 * Zwraca najmniej podejrzanego gracza wg agenta.
	 * 
	 * @param agentID
	 *            Identyfikator agenta.
	 * @return Zwraca tablice w postaci: 0 - wartosc 'nie podejrzliwosci' danego
	 *         gracza, 1 - identyfikator gracza, 2 - liczba osiagalnych stanow
	 */
	public double[] getLeastSuspicious(int agentID)
	{
		return getLeastSuspicious(agentID, null);
	}

	/**
	 * Przedefiniowana funkcja ktora umozliwa dodanie agentow ktorych nie
	 * bierzemy pod uwage.
	 * 
	 * @param agentID
	 *            Identyfikator agenta
	 * @param possible_saboteurs
	 *            Lista identyfikatorow graczy ktorych nie bierzemy pod uwage.
	 * @return Zwraca tablice w postaci: 0 - wartosc 'nie podejrzliwosci' danego
	 *         gracza, 1 - identyfikator gracza, 2 - liczba osiagalnych stanow
	 */
	public double[] getLeastSuspicious(int agentID, Set<Integer> possible_saboteurs)
	{
		double theMostUnsuspicion[] = new double[] { -1, -1, -1 };
		boolean reachable = false; // zmiene pomocnicza aby okreslic dostepnosc
									// danego celu
		double unsuspicions[] = new double[GameProperties.numberOfPlayers + 1]; // licznik
																				// dla
																				// kazdego
																				// z
																				// graczy
																				// +
																				// licznik
																				// dla
																				// wszystkich
																				// dostepnych
																				// stanow

		// inicjalizacja
		for (int i = 0; i < unsuspicions.length; ++i)
			unsuspicions[i] = 0;

		double currentValue;
		DefaultWeightedEdge edge;

		for (State state1 : possibleStates)
		{
			for (State state2 : possibleStates)
			{
				if (state1 != state2)
				{
					edge = kripkeGraphs.get(agentID).getEdge(state2, state1);
					currentValue = kripkeGraphs.get(agentID).getEdgeWeight(edge);

					// czy jest galaz pomiedzy stanami
					if (currentValue >= getTreshold() && state1 != state2)
					{
						reachable = true; // stan sie osiagalny
						unsuspicions[GameProperties.numberOfPlayers]++;// zwiekszamy
																		// ilosc
																		// wszystkich
																		// dostepnych
																		// stanow
						break; // wiemy ze stan jest osiagalny wiec konczymy
								// wew. petle
					}
				}
			}

			// jesli stan jest osiagalny
			if (reachable)
			{
				for (int k = 0; k < GameProperties.numberOfPlayers; k++)
				{
					// jesli w tym stanie agent jest kopaczem ( z wykluczeniem
					// samego siebie i przeslanych do funkcji innych agentow)
					if (agentID != k && possible_saboteurs != null && !possible_saboteurs.contains(k)
							&& !state1.isPlayerSaboteur(k))
					{
						unsuspicions[k]++; // zwiekszamy licznik dla tego agenta
					}
				}
			}
			reachable = false; // resetujemy dostepnosc
		}
		int j;
		// iterujemy po liczniku
		for (int i = 0; i < GameProperties.numberOfPlayers; i++)
		{
			j = (i + agentID) % GameProperties.numberOfPlayers; // jesli bedzie
																// wiecej niz 1
																// gracz z tym
																// samym
																// poziomem,
																// wybrany
																// zostanie ten
																// ktory
																// wystepuje
																// zaraz po
																// zagrywajacym
																// rgaczu
			// spradzenie wartosci, jesli jest bardziej 'niepodejrzany' to
			// zmieniamy
			if (unsuspicions[j] > theMostUnsuspicion[0])
			{
				theMostUnsuspicion[0] = unsuspicions[j];
				theMostUnsuspicion[1] = j;
			}
		}
		theMostUnsuspicion[2] = unsuspicions[GameProperties.numberOfPlayers];

		return theMostUnsuspicion;
	}

	/**
	 * Obliczenie jak blisko dana karta jest od celu danego gracza
	 * 
	 * @param tunnelCard
	 *            Zagrywana karta tuneli
	 * @param targetBoard
	 *            Miejsce w ktore dana karta ma byc ulozona.
	 * @param board
	 *            Plansza do gry.
	 * @param player
	 *            Gracza zagrywajacy karte.
	 * @return
	 */
	private int closerToGoal(TunnelCard tunnelCard, Point targetBoard, TunnelCard[][] board, Player player)
	{
		boolean horizontalFit = false;
		boolean verticalFit = false;
		int returnValue = 0;

		// jesli wspolrzedna Y jest mniejsza od 10 (powyzej kart celu) i karta
		// posiada otwarty tunel na dol - mozna przejsc dalej
		if ((targetBoard.getY() < 10 && tunnelCard.getOpenTunnels().contains(Direction.Down))
				|| (targetBoard.getY() > 10 && tunnelCard.getOpenTunnels().contains(Direction.Up)))
		{
			verticalFit = true;
		}
		// jesli karta posiada dobre 'wyprowadzenie poziome
		else if ((targetBoard.getX() > 8 && tunnelCard.getOpenTunnels().contains(Direction.Left))
				|| (targetBoard.getX() < 4 && tunnelCard.getOpenTunnels().contains(Direction.Right))
				|| (targetBoard.getX() > 4 && targetBoard.getX() < 8))
		{
			horizontalFit = true;
		}

		// jesli karta nie przybliza nas do cele
		if (!horizontalFit && !verticalFit)
		{
			return -2;
		}

		// jesli karta przybliza nas do celu pionowo
		if (verticalFit)
		{
			returnValue += 1;
		}

		// sprawdzamy cel danego gracza
		if (player.getGoal() == 1)
		{
			// jesli karta ma otwarty tunel skierowany w strone celu
			if (targetBoard.getX() > 4 && tunnelCard.getOpenTunnels().contains(Direction.Left))
			{
				return returnValue + 1;
			}
			if (targetBoard.getX() < 4 && tunnelCard.getOpenTunnels().contains(Direction.Right))
			{
				return returnValue + 1;
			}

			// jesli nie ale pasuje pionowo
			if (verticalFit)
			{
				return 1;
			}

			// wie gdzie jest cel ale nie zmierza w tym kierunku
			return -1;
		}

		// analogicznie
		if (player.getGoal() == 3)
		{
			if (targetBoard.getX() > 8 && tunnelCard.getOpenTunnels().contains(Direction.Left))
			{
				return returnValue + 1;
			}
			if (targetBoard.getX() < 8 && tunnelCard.getOpenTunnels().contains(Direction.Right))
			{
				return returnValue + 1;
			}
			if (verticalFit)
			{
				return 1;
			}
			return -1;
		}

		// analogicznie
		if (player.getGoal() == 2)
		{
			if (targetBoard.getX() > 6 && tunnelCard.getOpenTunnels().contains(Direction.Left))
			{
				return returnValue + 1;
			}
			if (targetBoard.getX() < 6 && tunnelCard.getOpenTunnels().contains(Direction.Right))
			{
				return returnValue + 1;
			}
			if (verticalFit)
			{
				return 1;
			}
			return -1;
		}

		// jesli nie wie gdzie jest cel ale zmierza w kierunku kart celu
		return 1;
	}

	public List<SimpleDirectedWeightedGraph<State, DefaultWeightedEdge>> getKripkeGraphs()
	{
		return kripkeGraphs;
	}

	/**
	 * Sprawdzenie czy gracz jest podejrzewany przez innych
	 * 
	 * @param playerID
	 *            Identyfikator gracza.
	 * @return Warto�� logiczna okreslajaca czy dany gracz jest podejrzewany.
	 */
	public boolean getSuspected(int playerID)
	{
		int numberOfAgentsSuspecting = 0;
		int numberOfWorldsHesSaboteur = 0;
		int numberOfWorldsHesNotSaboteur = 0;
		double currentValue;
		DefaultWeightedEdge edge;
		for (int i = 0; i < GameProperties.numberOfPlayers; i++)
		{
			if (i != playerID)
			{
				for (State state1 : possibleStates)
				{
					for (State state2 : possibleStates)
					{
						edge = kripkeGraphs.get(i).getEdge(state1, state2);
						currentValue = kripkeGraphs.get(i).getEdgeWeight(edge);
						// jesli stan jest prawdopodobny i gracz jest w tym
						// stanie sabotazysta
						if (currentValue >= getTreshold() && state2.isPlayerSaboteur(playerID))
						{
							numberOfWorldsHesSaboteur++;
						}
						// w p.p.
						else
						{
							numberOfWorldsHesNotSaboteur++;
						}
					}
				}
			}
			// jesli liczba 'swiatow' w ktorych nie jest sabotazysta jest
			// mniejsza niz 1 i mniejsza niz liczba swiatow w ktorych jest
			// sabotazystow
			if (numberOfWorldsHesNotSaboteur <= 1 && numberOfWorldsHesSaboteur >= numberOfWorldsHesNotSaboteur)
			{
				// dany agent go podejrzewa
				numberOfAgentsSuspecting++;
			}

			numberOfWorldsHesNotSaboteur = 0;
			numberOfWorldsHesSaboteur = 0;
		}
		// jesli liczba podejrzewajacych gracy jest wieksza 2, gracz uznawany
		// jest ogolnie za podejrzewanego
		if (numberOfAgentsSuspecting >= 2)
		{
			return true;
		}
		return false;
	}

	public double getTreshold()
	{
		return treshold;
	}

	public void setTreshold(double treshold)
	{
		this.treshold = treshold;
	}

}