package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Klasa reprezentujaca pojedynczy stan w modelu Kripkego.
 * 
 * @author Piotr Zygmunt
 *
 */
public class State
{
	/**
	 * Tablica wartosci logicznych o rozmiarze ilosci graczy. Wartosc na danej
	 * pozycji okresla role danego gracza w grze (sabotazysta badz nie).
	 */
	private boolean[] saboteurs;

	public State(boolean saboteurs[])
	{
		this.saboteurs = saboteurs;
	}

	/**
	 * Zwraca role gracza w grze.
	 * 
	 * @param playerID
	 *            Identyfikator gracza
	 * @return Rola (true - sabotazysta, false - kopacz).
	 */
	public boolean isPlayerSaboteur(int playerID)
	{
		return saboteurs[playerID];
	}

	/**
	 * Lista sabotazystow
	 * 
	 * @return Liste zabotazystow w postaci Listy wyplnionej identyfikatorami
	 *         graczy ktorzy sa sabotazystami.
	 */
	public List<Integer> getSaboteurs()
	{
		List<Integer> saboteursList = new ArrayList<Integer>();
		for (int i = 0; i < saboteurs.length; i++)
		{
			if (saboteurs[i])
			{
				saboteursList.add(i);
			}
		}
		return saboteursList;
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for (int i = 0; i < saboteurs.length; ++i)
			if (saboteurs[i])
				builder.append("T, ");
			else
				builder.append("F, ");
		builder.delete(builder.length() - 2, builder.length());
		builder.append(" )");

		return builder.toString();
	}

	/**
	 * Sprawdza czy dwaj gracze maja te sama role w grze.
	 * 
	 * @param player1ID
	 *            Identyfikator pierwszego gracza.
	 * @param player2ID
	 *            Identyfikator drugiego gracza.
	 * @return
	 */
	public boolean haveSameRole(int player1ID, int player2ID)
	{
		if (saboteurs[player1ID] == saboteurs[player2ID])
		{
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(saboteurs);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (!Arrays.equals(saboteurs, other.saboteurs))
			return false;
		return true;
	}
	
}
