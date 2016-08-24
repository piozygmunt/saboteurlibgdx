package pl.zygmunt.events;

/**
 * Klasa reprezentujaca klikniecia na przycisk 'New Game'. Rozpoczecie nowej
 * gry.
 *
 * @author Piotr Zygmunt
 */
public class StartNewGameEvent extends ApplicationEvent
{
	private int numberOfPlayers;
	private boolean humanPlayer;
	public int getNumberOfPlayers()
	{
		return numberOfPlayers;
	}
	public void setNumberOfPlayers(int numberOfPlayers)
	{
		this.numberOfPlayers = numberOfPlayers;
	}
	public boolean isHumanPlayer()
	{
		return humanPlayer;
	}
	public void setHumanPlayer(boolean humanPlayer)
	{
		this.humanPlayer = humanPlayer;
	}
	
}
