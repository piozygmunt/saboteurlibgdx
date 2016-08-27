package pl.zygmunt.common;

/**
 * Klasa reprezentujaca kierunek (gora, dol, lewo, prawo).
 * 
 * @author Piotr Zygmunt
 *
 */
public enum Direction
{
	Up, Down, Left, Right;

	/**
	 * Funkcja pomocnicza ktora zwraca przeciwny kierunek.
	 * 
	 * @param direction
	 *            Aktualny kierunek.
	 * @return Przeciwny kierunek.
	 */
	public static Direction getOppositeDirection(Direction direction)
	{
		switch (direction)
		{
			case Up:
				return Direction.Down;
			case Down:
				return Direction.Up;
			case Left:
				return Direction.Right;
			case Right:
				return Direction.Left;
		}
		return null;
	}
	
	
}
