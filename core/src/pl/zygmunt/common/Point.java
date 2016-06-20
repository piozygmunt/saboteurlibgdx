package pl.zygmunt.common;

/**
 * Klasa pomocnicza reprezntujaca punkt(jego wspolrzedne).
 * 
 * @author Piotrek
 *
 */
public class Point
{
	/**
	 * Wspolrzedna X.
	 */
	private int x;
	/**
	 * Wspolrzedna Y.
	 */
	private int y;

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}
}
