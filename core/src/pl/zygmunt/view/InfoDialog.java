package pl.zygmunt.view;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import pl.zygmunt.listeners.DialogListener;

/**
 * Klasa reprezentujaca dialog informacyjny. Rozszerza podstawowy dialog.
 * 
 * @author Piotr Zygmunt
 *
 */
public class InfoDialog extends Dialog
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
	 * @param info
	 *            Informacje.
	 */
	public InfoDialog(String title, Skin skin, DialogListener dialogListener, String info)
	{
		super(title, skin, "custom");
		super.text(info);
		super.button("OK", "OKInfo");
		this.dialogListener = dialogListener;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void result(Object object)
	{
		dialogListener.result(object);
	}

}
