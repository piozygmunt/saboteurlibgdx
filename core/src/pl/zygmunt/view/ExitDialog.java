package pl.zygmunt.view;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import pl.zygmunt.listeners.DialogListener;

/**
 * Klasa reprezentujaca dialog wyswietlany podczas wychodzenia z gry. Rozszerza
 * podstawowy dialog.
 * 
 * @author Piotr Zygmunt
 *
 */
public class ExitDialog extends Dialog
{
	/**
	 * Nasluchiwacz na zdarzenia.
	 */
	private DialogListener dialogListener;

	/**
	 * Konstruktor a wnimi inicjalizacja przyciskow i tekstu informacyjnego.
	 * 
	 * @param title
	 *            Tytul dialogu.
	 * @param skin
	 *            Skin.
	 * @param dialogListener
	 *            Nasluchiwacz.
	 */
	public ExitDialog(String title, Skin skin, DialogListener dialogListener)
	{
		super(title, skin, "custom");
		super.text("Do you really want to exit?");

		TextButton yesbutton = new TextButton("Yes", skin, "yes");
		TextButton nobutton = new TextButton("No", skin, "no");
		super.button(yesbutton, "AcceptExit");
		super.button(nobutton, "RefuseExit");

		this.dialogListener = dialogListener;
	}

	@Override
	protected void result(Object object)
	{
		dialogListener.result(object);
	}

}
