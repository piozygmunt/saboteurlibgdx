package pl.zygmunt.model;

/**
 * Klasa reprezntujaca karta w modelu.
 * 
 * @author Piotrek
 *
 */
public class Card implements Cloneable
{

	public Card()
	{
	}

	// implementacja klonowania
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
