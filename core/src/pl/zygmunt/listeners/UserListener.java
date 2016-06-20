package pl.zygmunt.listeners;

import java.util.concurrent.BlockingQueue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.SelectedCardEvent;

/**
 * Klasa reprezentujaca obiekty nasluchujace na zdarzenia przychodzace ze
 * "sceny gracza" (zaznaczanie i odznaczanie kart).
 * 
 * @author Piotr Zygmunt
 *
 */
public class UserListener extends ClickListener
{

	/**
	 * Kolejka blokujaca zdarzen
	 */
	private BlockingQueue<ApplicationEvent> bq;

	public UserListener(final BlockingQueue<ApplicationEvent> bq)
	{
		this.bq = bq;
	}

	/**
	 * Klikniecie na dana karte.
	 */
	public void clicked(InputEvent event, float x, float y)
	{
		Actor target = event.getTarget();

		if (event.getTarget().getClass().equals(Image.class))
		{
			try
			{
				bq.put(new SelectedCardEvent(target));
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
