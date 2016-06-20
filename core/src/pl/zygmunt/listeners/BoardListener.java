package pl.zygmunt.listeners;

import java.util.concurrent.BlockingQueue;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.BoardClickedEvent;

/**
 * Klasa reprezentujaca obiekt nasluchujacy na zdarzenia
 * "przychodzace z planszy".
 * 
 * @author Piotr Zygmunt
 *
 */
public class BoardListener extends ClickListener
{

	/**
	 * Kolejka zdarzen do ktorej dodawane sa nowe zdarzenia.
	 */
	private BlockingQueue<ApplicationEvent> bq;

	public BoardListener(final BlockingQueue<ApplicationEvent> bq)
	{
		this.bq = bq;
	}

	/**
	 * Funkcja wywolywana w momencie klikniecia na plansze.
	 */
	public void clicked(InputEvent event, float x, float y)
	{

		try
		{
			bq.put(new BoardClickedEvent(event.getTarget()));
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
