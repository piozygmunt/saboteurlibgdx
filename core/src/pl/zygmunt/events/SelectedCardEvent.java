package pl.zygmunt.events;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Klasa reprezentujaca zdarzenie zaznaczenia karty.
 *
 * @author Piotr Zygmunt
 */
public class SelectedCardEvent extends ApplicationEvent
{
	private Actor target;

	public SelectedCardEvent(final Actor target)
	{
		this.target = target;
	}

	public Actor getTarget()
	{
		return target;
	}

}
