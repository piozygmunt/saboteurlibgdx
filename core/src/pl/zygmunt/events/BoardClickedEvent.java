package pl.zygmunt.events;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Klasa reprezentujaca zdarzenia klikniecia na planszy.
 *
 * @author Piotr Zygmunt
 */
public final class BoardClickedEvent extends ApplicationEvent
{
	/** Pole na planszy ktore zostalo wybrane. */
	private final Actor target;

	public BoardClickedEvent(final Actor target)
	{
		this.target = target;
	}

	/**
	 * Zwraca wybrane pole.
	 */
	public Actor getTarget()
	{
		return target;
	}
}
