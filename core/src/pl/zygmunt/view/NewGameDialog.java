package pl.zygmunt.view;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import pl.zygmunt.listeners.DialogListener;

/**
 * Klasa reprezentujaca dialog rozpoczecia nowej gry. Rozszerza podstawowy
 * dialog.
 * 
 * @author Piotr Zygmunt
 *
 */
public class NewGameDialog extends Dialog
{
	/**
	 * Nasluchiwacz na zdarzenia.
	 */
	private DialogListener dialogListener;

	/**
	 * Konstruktor a w nim inicjalizacja przyciskow i tekstu informacyjnego.
	 * 
	 * @param title
	 *            Tytul dialogu.
	 * @param skin
	 *            Skin.
	 * @param dialogListener
	 *            Nasluchiwacz.
	 */
	public NewGameDialog(String title, Skin skin, DialogListener dialogListener)
	{
		super(title, skin, "custom");
		super.text("Do you really want to start new game?");

		TextButton yesbutton = new TextButton("Yes", skin, "yes");
		TextButton nobutton = new TextButton("No", skin, "no");

		super.setMovable(false);
		super.button(yesbutton, "AcceptNewGame");
		super.button(nobutton, "RefuseNewGame");
		this.dialogListener = dialogListener;
	}

	@Override
	protected void result(Object object)
	{
		dialogListener.result(object);
	}
}
