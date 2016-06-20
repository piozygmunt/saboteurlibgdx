package pl.zygmunt.listeners;

import java.util.concurrent.BlockingQueue;

import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.ButtonOkInfoClickedEvent;
import pl.zygmunt.events.ContinueGameEvent;
import pl.zygmunt.events.ExitApplicationEvent;
import pl.zygmunt.events.StartNewGameEvent;

/**
 * Klasa reprezntujaca obiekt nasluchujacy na zdarzenia przychodzace z Dialogow.
 * 
 * @author Piotr Zygmunt
 *
 */
public class DialogListener
{
	/**
	 * Kolejka zdarzen do ktorej dodawana sa nowe zdarzenia.
	 */
	private BlockingQueue<ApplicationEvent> bq;

	public DialogListener(BlockingQueue<ApplicationEvent> bq)
	{
		this.bq = bq;
	}

	/**
	 * Glowna metoda ktora okresla dane zdarzenie a nastepnia dodaje nowy obiekt
	 * do kolejki zdarzen.
	 * 
	 * @param object
	 */
	public void result(Object object)
	{
		if (object.toString().equals("AcceptExit"))
		{
			try
			{
				bq.put(new ExitApplicationEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (object.toString().equals("RefuseExit"))
		{
			try
			{
				bq.put(new ContinueGameEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (object.toString().equals("AcceptNewGame"))
		{
			try
			{
				bq.put(new StartNewGameEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (object.toString().equals("RefuseNewGame"))
		{
			try
			{
				bq.put(new ContinueGameEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (object.toString().equals("OKNewGame"))
		{
			try
			{
				bq.put(new StartNewGameEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (object.toString().equals("OKInfo"))
		{
			try
			{
				bq.put(new ButtonOkInfoClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
