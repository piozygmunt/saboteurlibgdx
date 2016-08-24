package pl.zygmunt.view;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
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

	private SelectBox<String> amountOfPlayers;
	
	public int getAmountOfPlayers()
	{
		return amountOfPlayers.getSelectedIndex() +3;
	}

	public boolean getIsHumanPlayer()
	{
		return isHumanPlayer.getSelectedIndex() == 0;
	}

	private SelectBox<String> isHumanPlayer;
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
	
		String[] players = new String[5];
		for(int i = 0; i <5; ++i)
			players[i] = (i+3 )+ " players";
		
		String[] humanPlayer = new String[2];
		humanPlayer[0] = "With human player";
		humanPlayer[1] = "Without human player";

		
		this.amountOfPlayers = new SelectBox<String>(skin);
		amountOfPlayers.setItems(players);
		
		this.isHumanPlayer = new SelectBox<String>(skin);
		isHumanPlayer.setItems(humanPlayer);
		
		super.getContentTable().getCells().get(0).colspan(2);
		super.getContentTable().row();
		super.getContentTable().add(new Label("Select number of players: ", skin));
		super.getContentTable().add(amountOfPlayers);
		
		super.getContentTable().row();
		super.getContentTable().add(new Label("How do you want to play: ", skin));
		super.getContentTable().add(isHumanPlayer);

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
