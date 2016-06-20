package pl.zygmunt.view;

import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

import pl.zygmunt.common.Common;
import pl.zygmunt.listeners.UserListener;
import pl.zygmunt.model.Card;

/**
 * Klasa reprezentujaca Scene ktora zawiera karty do gry gracza.
 * 
 * @author Piotr Zygmunt
 *
 */
public class UserStage extends Stage
{
	/**
	 * Obraz reprezentujacy obramowanie zaznaczonej karty.
	 */
	private Image borderImage;

	/**
	 * Tabela w ktorej znajduja sie karty.
	 */
	private Table cardsTable;

	/**
	 * Referencja na zaznaczona karte.
	 */
	private Actor selectedCard;

	/**
	 * Czy zaznaczona karta jest odwrocona.
	 */
	private boolean rotated;

	/**
	 * Zarzadca wczytywanych elementow.
	 */
	private AssetManager manager;

	public UserStage(final Skin skin, AssetManager manager)
	{
		this.manager = manager;

		cardsTable = new Table();

		selectedCard = null;

		borderImage = new Image(new Texture(getPixmapRoundedRectangle(100, 100, 10, Color.rgba8888(Color.WHITE))));

		rotated = false;

		cardsTable.setFillParent(true);

		this.addActor(cardsTable);
	}

	public Image getBorderImage()
	{
		return borderImage;
	}

	public Table cardsTable()
	{
		return cardsTable;
	}

	public void setGameListener(UserListener gl)
	{
		cardsTable.addListener(gl);
	}

	/**
	 * Ustawienie zaznaczenie na danej karcie.
	 * 
	 * @param card
	 *            Karta
	 */
	public void setSelectedCard(Actor card)
	{
		Actor prevSelected = selectedCard;
		if (getRotated())
			rotateSelected();
		// usuniecie poprzedniego zaznaczenie
		removeSelection();
		if (card != prevSelected)
			drawSelection(card);
		cardsTable.toFront();
	}

	/**
	 * Wyswietlenie zaznaczenia(obramowania) dla danej karty.
	 * 
	 * @param actor
	 *            Karta
	 */
	void drawSelection(final Actor actor)
	{
		this.selectedCard = actor;

		borderImage.setSize(actor.getWidth() + 2 * 3, actor.getHeight() + 2 * 3);
		borderImage.setPosition(actor.getX() - 3, actor.getY() - 3);
		borderImage.toBack();

		actor.toFront();

		this.addActor(borderImage);
	}

	/**
	 * Usuniecie zaznaczenia.
	 */
	void removeSelection()
	{
		if (selectedCard != null)
		{
			this.selectedCard = null;
			borderImage.remove();
		}
	}

	public Actor getSelectedCard()
	{
		return selectedCard;
	}

	public Table getUserTable()
	{
		// TODO Auto-generated method stub
		return cardsTable;
	}

	/**
	 * Odwrocenie zaznaczonej karty.
	 */
	public void rotateSelected()
	{
		if (selectedCard != null)
		{

			selectedCard.setOrigin(selectedCard.getWidth() / 2, selectedCard.getHeight() / 2);

			selectedCard.rotateBy(180);

			rotated = !rotated;
		}

	}

	/**
	 * Aktualizacja sceny- wyswietlenie kart.
	 * 
	 * @param cards
	 */
	public void setCards(final List<Card> cards)
	{
		cardsTable.clearChildren();
		removeSelection();
		for (Card card : cards)
		{
			final Actor actor = Common.getImageCardFromAssetManager(manager, card);
			actor.addListener(new FocusListener()
			{
				@Override
				public boolean handle(Event event)
				{

					if (event.toString().equals("mouseMoved"))
					{
						actor.setColor(Color.GRAY);
						return false;
					}
					else if (event.toString().equals("exit"))
					{
						// table1.setBackground(null);
						// table1.background("");
						actor.setColor(Color.WHITE);

						return false;
					}
					return true;
				}
			});
			cardsTable.add(actor).padRight(5);

		}
		cardsTable.pad(10, 10, 10, 10);
	}

	public boolean getRotated()
	{
		return rotated;
	}

	/**
	 * Zresetowanie wartosc ktora okresla odwrocenie.
	 */
	public void resetRotated()
	{
		rotated = false;
	}

	/**
	 * Funkcja ktora tworzy prostokat z zaokraglonymi rogami.( do zaznaczenia).
	 * 
	 * @param width
	 *            Szerokosc
	 * @param height
	 *            Wysokosc
	 * @param radius
	 *            Zaokraglenie.
	 * @param color
	 *            Kolor.
	 * @return
	 */
	private Pixmap getPixmapRoundedRectangle(int width, int height, int radius, int color)
	{

		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		pixmap.setColor(color);

		pixmap.fillRectangle(0, radius, pixmap.getWidth(), pixmap.getHeight() - 2 * radius);

		pixmap.fillRectangle(radius, 0, pixmap.getWidth() - 2 * radius, pixmap.getHeight());

		pixmap.fillCircle(radius, radius, radius);

		pixmap.fillCircle(radius, pixmap.getHeight() - radius, radius);

		pixmap.fillCircle(pixmap.getWidth() - radius, radius, radius);

		pixmap.fillCircle(pixmap.getWidth() - radius, pixmap.getHeight() - radius, radius);
		return pixmap;
	}

}
