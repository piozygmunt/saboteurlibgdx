package pl.zygmunt.model;

/**
 * Abstrakcyjna kasa reprezentujaca agenta.
 * 
 * @author Piot Zygmunt
 *
 */
public abstract class Agent extends Player
{

	/**
	 * Ka�dy z agent�w posiada w�asny model Kripkego.
	 */
	protected KripkeModel model;
	/**
	 * Warto�� reprezentuj�ca czy dana akcja jest warta wykonania.
	 */
	protected int treshold;

	/**
	 * Konstruktor klasy Agent.
	 * 
	 * @param id
	 *            Identyfikator agenta.
	 */
	public Agent(int id)
	{
		super(id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Funkcja reprezentuj�ca podj�cie decyzji o nast�pnym ruchu przez agenta.
	 * 
	 * @param players
	 *            Wszyscy gracze bior�cy udzia� w rogrywce.
	 * @param board
	 *            Obiekt reprezntuj�cy aktualne ustawienie kart na planszy.
	 * @return Obiekt reprezntuj�cy podj�t� decyzj� (u�yta karta oraz cel).
	 */
	public abstract Turn takeTurn(Player[] players, TunnelCard[][] board);

	/**
	 * Funkcja reprezentuj�ca uaktualnienie modelu Kripkego dla danego agenta na
	 * podstawie ostatnio wykonanego ruchu w grze.
	 * 
	 * @param turn
	 *            Obiekt reprezentuj�cy wykonany ruch.
	 * @param agentPlayed
	 *            Identyfikator gracza kt�ry wykona� ruch.
	 * @param board
	 *            Obiekt reprezntuj�cy aktualne ustawienie kart na planszy.
	 * @param agents
	 *            Gracze bior�cy udzia� w rozgrywce.
	 */
	public abstract void updateKripke(Turn turn, int agentPlayed, TunnelCard[][] board, Player[] agents);

	/**
	 * Funkcja zwracaj�ca model Kripkego.
	 * 
	 * @return
	 */
	public KripkeModel getKripkeModel()
	{
		return model;
	}

}
