package pl.zygmunt.events;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Klasa reprezentujaca zdarzenie klikniecia na label gracza w menu bocznym.
 *
 * @author Piotr Zygmunt
 */
public final class PlayerLabelClickedEvent extends ApplicationEvent
{
	private Actor targetsParent;

	public PlayerLabelClickedEvent(final Actor targetsParent)
	{
		this.targetsParent = targetsParent;
	}

	public Actor getTargetsParent()
	{
		return targetsParent;
	}
}
