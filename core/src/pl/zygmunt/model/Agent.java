package pl.zygmunt.model;

import java.util.List;

/**
 * Abstrakcyjna kasa reprezentujaca agenta.
 * 
 * @author Piot Zygmunt
 *
 */
public abstract class Agent extends Player
{

	/**
	 * Ka¿dy z agentów posiada w³asny model Kripkego.
	 */
	protected KripkeModel model;
	/**
	 * Wartoœæ reprezentuj¹ca czy dana akcja jest warta wykonania.
	 */
	protected int minCardValue;

	/**
	 * Konstruktor klasy Agent.
	 * 
	 * @param id
	 *            Identyfikator agenta.
	 */
	public Agent(int id)
	{
		super(id);
	}

	/**
	 * Funkcja reprezentuj¹ca podjêcie decyzji o nastêpnym ruchu przez agenta.
	 * 
	 * @param players
	 *            Wszyscy gracze bior¹cy udzia³ w rogrywce.
	 * @param board
	 *            Obiekt reprezntuj¹cy aktualne ustawienie kart na planszy.
	 * @return Obiekt reprezntuj¹cy podjêt¹ decyzjê (u¿yta karta oraz cel).
	 */
	public abstract Turn takeTurn(List<Player> players, TunnelCard[][] board);

	/**
	 * Funkcja reprezentuj¹ca uaktualnienie modelu Kripkego dla danego agenta na
	 * podstawie ostatnio wykonanego ruchu w grze.
	 * 
	 * @param turn
	 *            Obiekt reprezentuj¹cy wykonany ruch.
	 * @param agentPlayed
	 *            Identyfikator gracza który wykona³ ruch.
	 * @param board
	 *            Obiekt reprezntuj¹cy aktualne ustawienie kart na planszy.
	 * @param agents
	 *            Gracze bior¹cy udzia³ w rozgrywce.
	 */
	public abstract void updateKripke(Turn turn, int agentPlayed, TunnelCard[][] board, List<Player> agents);

	/**
	 * Funkcja zwracaj¹ca model Kripkego.
	 * 
	 * @return
	 */
	public KripkeModel getKripkeModel()
	{
		return model;
	}

}
