package pl.zygmunt.model;

/**
 * Klasa reprezentujaca podstawowe informacje o grze.
 * 
 * @author Piotr Zygmunt
 *
 */
public class GameProperties
{
	/**
	 * Ilosc graczy.
	 */
	public static int numberOfPlayers = 6;
	/**
	 * Ilosc kopaczy.
	 */
	public static int maxNumberOfDwarfs = 4;
	/**
	 * Ilosc sabotazystow.
	 */
	public static int maxNumberOfSaboteurs = 2;
	/**
	 * Poczatkowa ilosc kart u kazdego z graczy.
	 */
	public static int numberOfCards = 5;
	/**
	 * Czy w grze bierze udzial gracz poza agentami.
	 */
	public static boolean humanPlayer = true;

	/**
	 * Okreslenie czy w grze bierze udzial ludzki gracz.
	 * @param value 
	 */
	public static void setHumanPlayer(boolean value)
	{
		humanPlayer = value;
	}
	
	/**
	 * Okreslenie ilosci graczy w danej rozgrywce i ustalenie ilosci kart
	 * zgodnie z regulami gry.
	 * 
	 * @param players
	 *            Ilosc graczy.
	 */
	public static void setAmountOfPlayers(int players)
	{
		switch (players)
		{
			case 3:
				numberOfPlayers = players;
				maxNumberOfSaboteurs = 1;
				maxNumberOfDwarfs = 2;
				numberOfCards = 6;
				break;
			case 4:
				numberOfPlayers = players;
				maxNumberOfSaboteurs = 1;
				maxNumberOfDwarfs = 3;
				numberOfCards = 6;
				break;
			case 5:
				numberOfPlayers = players;
				maxNumberOfSaboteurs = 2;
				maxNumberOfDwarfs = 3;
				numberOfCards = 6;
				break;
			case 6:
				numberOfPlayers = players;
				maxNumberOfSaboteurs = 2;
				maxNumberOfDwarfs = 4;
				numberOfCards = 5;
				break;
			case 7:
				numberOfPlayers = players;
				maxNumberOfSaboteurs = 3;
				maxNumberOfDwarfs = 4;
				numberOfCards = 5;
				break;

		}
	}
}
