package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezntujaca pojedynczego gracza w grze.
 * 
 * @author Piotr Zygmunt
 *
 */
public class Player
{

	/**
	 * Identyfikator gracza.
	 */
	private int id;
	/**
	 * Pole okreslaj¹ce czy dany gracze jest sabota¿yst¹ (true), czy krasnalem.
	 */
	private boolean saboteur;
	/**
	 * Karty posiadane przez gracza.
	 */
	private List<Card> cards;
	/**
	 * Pole okreœlaj¹ce czy gracz jest zablokowany.
	 */
	private boolean blocked;
	/**
	 * Tablica okreœlaj¹ca czy gracz wie co kryja sie pod kartami celu. [0] - czy wie gdzie jest zloto ( 0 lub 1), [1-3] - oznaczaja poszczegolne karty celu
	 * i przyjmuja wartosc 0- nie wiemy co jest pod karta, 1 - karta ze zlotem, 2 - karta bez zlota.
	 */
	private int[] knowGoal;

	/**
	 * Konstruktor klasy Player/
	 * 
	 * @param id
	 */
	public Player(final int id)
	{
		this.id = id;
		blocked = false;
		knowGoal = new int[] { 0, 0, 0, 0 };
		cards = new ArrayList<Card>();
	}

	public List<Card> getCards()
	{
		return cards;
	}

	public int getID()
	{
		return id;
	}

	public boolean getBlocked()
	{
		return blocked;
	}

	public void setBlocked(boolean blocker)
	{
		blocked = blocker;
	}

	public void setRole(boolean role)
	{
		saboteur = role;
	}

	public boolean getRole()
	{
		return saboteur;
	}

	public int[] getGoals()
	{
		return knowGoal;
	}

	/**
	 * Aktualizja celow danego gracza.
	 * 
	 * @param goalNumber
	 *            Jedno z pol koncowych.
	 * @param hasGold
	 *            Czy pole zawiera zloto?
	 * @return
	 */
	public int setGoal(int goalNumber, boolean hasGold)
	{
		// jesli zloto zosta³o znalezione
		if (hasGold)
		{
			// znamy cel
			knowGoal[0] = 1;
			// ustawienie karty ze z³otem
			knowGoal[goalNumber ] = 1;
			
			switch(goalNumber)
			{
				case 1:
					knowGoal[2] = 2;
					knowGoal[3] = 2;
					break;
				case 2:
					knowGoal[1] = 2;
					knowGoal[3] = 2;
					break;
				case 3:
					knowGoal[1] = 2;
					knowGoal[2] = 2;
					break;
			}
			
			
			return goalNumber;
		}
		// jesli z³ota nie ma (znaleziono kamien), aktualizuj cele
		else
		{
			knowGoal[goalNumber ] = 2;
			// jesli kamien jest pod pierwsza karta
			if (knowGoal[1] == 2)
			{
				// .. i druga tzn ¿e z³oto jest pod trzecia
				if (knowGoal[2] == 2)
				{
					knowGoal[3] = 1;
					knowGoal[0] = 1;
					return 2;
				}

				// .... i trzecia tzn ¿e z³oto jest pod druga
				else if (knowGoal[3] == 2)
				{
					knowGoal[2] = 1;
					knowGoal[0] = 1;
					return 1;
				}
			}

			// jesli kamien jest pod druga i trzecia, zloto pod pierwsza
			else if (knowGoal[2] == 2 && knowGoal[3] == 2)
			{
				knowGoal[1] = 1;
				knowGoal[0] = 1;
				return 0;
			}
		}
		return 10;
	}

	/**
	 * Zresetowanie celow.
	 */
	public void resetGoal()
	{
		for (int i = 0; i < 4; i++)
		{
			knowGoal[i] = 0;
		}
	}

	/**
	 * Sprawdzenie czy gracz wie co jest pod dana karta celu
	 * 
	 * @param goalCard
	 *            Numer karty celu.
	 * @return
	 */
	public int knowsGoal(int goalCard)
	{
		// jesli sabotazysta to zwroci odwrotnie
		if (saboteur)
		{
			if (knowGoal[goalCard] == 0)
			{
				return 0;
			}
			return ((knowGoal[goalCard] % 2) + 1);
		}
		return knowGoal[goalCard];
	}

}
