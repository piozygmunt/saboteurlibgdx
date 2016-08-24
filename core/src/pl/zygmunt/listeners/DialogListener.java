package pl.zygmunt.listeners;

import java.util.concurrent.BlockingQueue;

import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.ButtonOkInfoClickedEvent;
import pl.zygmunt.events.ContinueGameEvent;
import pl.zygmunt.events.ExitApplicationEvent;
import pl.zygmunt.events.StartNewGameEvent;
import pl.zygmunt.model.GameProperties;
import pl.zygmunt.view.NewGameDialog;

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
	private NewGameDialog newGameDialog;

	public void setNewGameDialog(NewGameDialog newGameDialog)
	{
		this.newGameDialog = newGameDialog;
	}

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
			StartNewGameEvent event = new StartNewGameEvent();
			if(newGameDialog != null )
			{
				event.setHumanPlayer(newGameDialog.getIsHumanPlayer());
				event.setNumberOfPlayers(newGameDialog.getAmountOfPlayers());
			}	
			try
			{
				bq.put(event);
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
			StartNewGameEvent event = new StartNewGameEvent();
			if(newGameDialog != null )
			{
				event.setHumanPlayer(GameProperties.humanPlayer);
				event.setNumberOfPlayers(GameProperties.numberOfPlayers);
			}
			try
			{
				bq.put(event);
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
