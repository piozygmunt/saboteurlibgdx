package pl.zygmunt.listeners;

import java.util.concurrent.BlockingQueue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.ButtonBackClickedEvent;
import pl.zygmunt.events.ButtonDiscardClickedEvent;
import pl.zygmunt.events.ButtonExitClickedEvent;
import pl.zygmunt.events.ButtonNewGameClickedEvent;
import pl.zygmunt.events.ButtonNextPlayerClickedEvent;
import pl.zygmunt.events.PlayerLabelClickedEvent;
import pl.zygmunt.events.RotateButtonClickedEvent;

/**
 * Klasa reprezentujaca obiekty nasluchajace na zdarzenia przychodzace z
 * bocznego menu.
 * 
 * @author Piotr Zygmunt
 *
 */
public class MenuListener extends ClickListener
{

	/**
	 * Kolejka blokujaca zdarzen.
	 */
	private BlockingQueue<ApplicationEvent> bq;

	public MenuListener(final BlockingQueue<ApplicationEvent> bq)
	{
		this.bq = bq;
	}

	/**
	 * Zdarzenia klikniecia na dany przycisk.
	 */
	public void clicked(InputEvent event, float x, float y)
	{

		Actor target = event.getTarget();
		Actor targetsParent = target.getParent();

		// przycisk cofniecia tury
		if (target.getName() != null && target.getName().equals("BackBtn")
				|| targetsParent.getName() != null && targetsParent.getName().equals("BackBtn"))
		{
			try
			{
				bq.put(new ButtonBackClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// przycisk nastepnego gracza
		else if (target.getName() != null && target.getName().equals("nextRoundBtn")
				|| targetsParent.getName() != null && targetsParent.getName().equals("nextRoundBtn"))
		{
			try
			{
				bq.put(new ButtonNextPlayerClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// przycisk nowej gry
		else if (target.getName() != null && target.getName().equals("newGameBtn")
				|| targetsParent.getName() != null && targetsParent.getName().equals("newGameBtn"))
		{
			try
			{
				bq.put(new ButtonNewGameClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// przycisk rotacjii karty
		else if (target.getName() != null && target.getName().equals("Rotate")
				|| targetsParent.getName() != null && targetsParent.getName().equals("Rotate"))
		{
			try
			{
				bq.put(new RotateButtonClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// przycisk odrzucenia karty
		else if (target.getName() != null && target.getName().equals("discardBtn")
				|| targetsParent.getName() != null && targetsParent.getName().equals("discardBtn"))
		{
			try
			{
				bq.put(new ButtonDiscardClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// przycisk wyjscia z aplikacji
		else if (target.getName() != null && target.getName().equals("exitBtn")
				|| targetsParent.getName() != null && targetsParent.getName().equals("exitBtn"))
		{
			try
			{
				bq.put(new ButtonExitClickedEvent());
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// klikniecia na label danego gracza
		else
		{
			try
			{
				bq.put(new PlayerLabelClickedEvent(targetsParent));
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
