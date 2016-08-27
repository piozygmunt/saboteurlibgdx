package pl.zygmunt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import pl.zygmunt.common.Common;
import pl.zygmunt.listeners.BoardListener;
import pl.zygmunt.model.TunnelCard;

/**
 * Klasa reprezentujaca plansze do gry.
 * 
 * @author Piotr Zygmunt
 *
 */
public class BoardStage extends Stage
{
	/**
	 * Zarzadca wczytywanych obrazow, tekstur itp.
	 */
	private AssetManager manager;
	/**
	 * Obiekt w ktorym ulozona jest plansza. Dzieki niemu mozliwe jest
	 * poruszanie sie po planszy.
	 */
	private ScrollPane pane;
	/**
	 * Plansza, ktora umozliwa zblizanie i oddalanie (na telefonach).
	 */
	private ZoomableBoard board;

	/**
	 * Konstruktor
	 * 
	 * @param skin
	 *            Skin
	 * @param manager
	 *            Zarzadca wczytywanych elementow.
	 */
	public BoardStage(Skin skin, AssetManager manager)
	{
		this.manager = manager;
		board = new ZoomableBoard();
		board.resetBoard();
		board.layout();

		pane = new ScrollPane(board, skin, "default2");
		pane.layout();

		pane.setScrollbarsOnTop(false);
		pane.setFadeScrollBars(false);

		pane.setFillParent(true);

		pane.setScrollX(3.7f * 150 * Gdx.graphics.getWidth() / 960);
		pane.setScrollY(1.45f * 200 * Gdx.graphics.getHeight() / 540);

		this.addActor(pane);

	}

	public void setGameListener(BoardListener gl)
	{
		board.addListener(gl);
	}

	public Table getBoardTable()
	{
		return board.getTable();
	}
	
	public ZoomableBoard getZoomableBoard()
	{
		return board;
	}

	/**
	 * Wyczyszczenie danego pola do gry.
	 * 
	 * @param x
	 *            Wspolrzedna X.
	 * @param y
	 *            Wspolrzedna Y.
	 */
	public void setBoardFieldDefault(int x, int y)
	{
		Cell<Actor> cell = board.getTable().getCells().get(y * Common.initialColumns + x);

		final Actor new_actor = new Actor();

		cell.setActor(new_actor);

	}

	/**
	 * Wyczyszczenie calej planszy.
	 */
	public void resetBoardTable()
	{
		board.resetBoard();
	}

	/**
	 * Umiejscowienie karty na planszy.
	 * 
	 * @param x
	 *            Wspolrzedna X.
	 * @param y
	 *            Wspolrzedna Y.
	 * @param card
	 *            Karta tuneli.
	 */
	public void placeCardOnBoard(final int x, final int y, final TunnelCard card)
	{
		Cell<Actor> cell = board.getTable().getCells().get(y * Common.initialColumns + x);
		Image cardImage = Common.getImageCardFromAssetManager(manager, card);
		cardImage.setOrigin(cell.getActorWidth() / 2, cell.getActorHeight() / 2);
		cell.setActor(cardImage);

	}

	/**
	 * Umiejscowanie karty na planszy
	 * 
	 * @param target
	 *            Miejsce w ktorym ma byc polozona/
	 * @param selectedCard
	 *            Karta do polozenia.
	 */
	public void placeCardOnBoard(final Actor target, final Actor selectedCard)
	{
		Cell<Actor> cell = board.getTable().getCell(target);
		selectedCard.setOrigin(cell.getActorWidth() / 2, cell.getActorHeight() / 2);
		selectedCard.clearListeners();
		selectedCard.setColor(Color.WHITE);
		cell.setActor(selectedCard);

	}

}
