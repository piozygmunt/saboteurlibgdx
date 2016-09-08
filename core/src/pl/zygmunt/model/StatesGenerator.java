package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstrakcyjna klasa zawierajca statyczna funkcja potrzebne do generacji
 * wszystkich mozliwych stanow w grze.
 * 
 * @author Piotr Zygmunt
 *
 */
public abstract class StatesGenerator
{

	/**
	 * Funkcja generujaca wszystkie mozliwe stany w zaleznosci od ilosci graczy
	 * 
	 * @param numberOfPlayers
	 * @return
	 */
	public static List<State> generateAllPossibleStates(int numberOfPlayers)
	{
		int maxSaboteurs = 0, minSaboteurs = 0;
		ArrayList<State> possibleStates = new ArrayList<State>();
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPlayers; ++i)
		{
			positions.add(i);
		}

		switch (numberOfPlayers)
		{
			case 3:
				minSaboteurs = 0;
				maxSaboteurs = 1;
				break;
			case 4:
				minSaboteurs = 0;
				maxSaboteurs = 1;
				break;
			case 5:
				maxSaboteurs = 2;
				minSaboteurs = 1;
				break;
			case 6:
				maxSaboteurs = 2;
				minSaboteurs = 1;
				break;
			case 7:
				maxSaboteurs = 3;
				minSaboteurs = 2;
				break;
		}

		for (int i = minSaboteurs; i <= maxSaboteurs; ++i)
		{
			List<Set<Integer>> subsets = new ArrayList<Set<Integer>>();
			subsets = getSubsets(positions, i);
			for (Set<Integer> temp : subsets)
			{
				possibleStates.add(createGameState(numberOfPlayers, temp));
			}

		}
		return possibleStates;

	}

	/**
	 * Funkcja tworzaca obiekt reprezentujacy pojedynczy stan w grze.
	 * 
	 * @param numberOfPlayers
	 *            Liczba graczy.
	 * @param posOfSaboteurs
	 *            Pozycja sabotazystow.
	 * @return Stan
	 */
	private static State createGameState(int numberOfPlayers, Set<Integer> posOfSaboteurs)
	{
		boolean[] saboteurs = new boolean[numberOfPlayers];
		for (int i = 0; i < numberOfPlayers; ++i)
		{
			if (posOfSaboteurs.contains(i))
				saboteurs[i] = true;
			else
				saboteurs[i] = false;
		}

		return new State(saboteurs);
	}

	/**
	 * Funkcja generujaca podzbiory danego zbioru.
	 * 
	 * @param superSet
	 *            Zbior wejsciowy.
	 * @param k
	 *            Rozmiar podzbiorow
	 * @param idx
	 *            Indeks od ktorego zaczynamy.
	 * @param current
	 *            Aktualny zbior.
	 * @param solution
	 *            Lista podzbiorow.
	 */
	private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current,
			List<Set<Integer>> solution)
	{
		if (current.size() == k)
		{
			solution.add(new HashSet<Integer>(current));
			return;
		}
		if (idx == superSet.size())
			return;
		Integer x = superSet.get(idx);
		current.add(x);
		getSubsets(superSet, k, idx + 1, current, solution);
		current.remove(x);
		getSubsets(superSet, k, idx + 1, current, solution);
	}

	/**
	 * Funkcja generujaca podzbiory danego zbioru.
	 * 
	 * @param superSet
	 *            Zbior wejsciowy.
	 * @param k
	 *            Rozmiar zbiorow wyjsciowy.
	 * @return Lista wygenerowanych podzbiorow.
	 */
	private static List<Set<Integer>> getSubsets(List<Integer> superSet, int k)
	{
		List<Set<Integer>> res = new ArrayList<Set<Integer>>();
		getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
		return res;
	}
}
