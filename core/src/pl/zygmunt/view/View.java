package pl.zygmunt.view;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import pl.zygmunt.common.Point;
import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.listeners.BoardListener;
import pl.zygmunt.listeners.DialogListener;
import pl.zygmunt.listeners.MenuListener;
import pl.zygmunt.listeners.UserListener;
import pl.zygmunt.model.BlockCard;
import pl.zygmunt.model.Card;
import pl.zygmunt.model.DeblockCard;
import pl.zygmunt.model.DemolishCard;
import pl.zygmunt.model.StartCard;
import pl.zygmunt.model.TunnelCard;
import pl.zygmunt.model.Turn;

/**
 * Klasa reprezentujaca widok w grze sabotazysta.
 * 
 * @author Piotr Zygmunt
 *
 */
public class View
{
	/**
	 * Glowny wyswietlacz gry.
	 */
	private GameScreen screen;
	/**
	 * Miejsce polozenia ostatnio zagrywanej karty.
	 */
	private Cell<Actor> lastPlayedCardsCell;
	/**
	 * Kolejka zdarzen.
	 */
	private BlockingQueue<ApplicationEvent> bq;
	/**
	 * Plik z obrazami kart.
	 */
	static public final String assetsFilePath = "data/pack.atlas";

	/**
	 * Konstruktor. Inicjuje plansze oraz listenery do poszczegolnych scen.
	 * 
	 * @param bq
	 *            Kolejka zdarzen.
	 */
	public View(final BlockingQueue<ApplicationEvent> bq)
	{
		screen = new GameScreen();
		lastPlayedCardsCell = null;
		this.bq = bq;

		placeCardOnBoard(6, 2, new StartCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight));

		placeCardOnBoard(4, 10, null);
		placeCardOnBoard(6, 10, null);
		placeCardOnBoard(8, 10, null);

		UserListener userlist = new UserListener(bq);
		BoardListener boardlist = new BoardListener(bq);
		MenuListener menulist = new MenuListener(bq);

		screen.getUserStage().setGameListener(userlist);
		screen.getBoardStage().setGameListener(boardlist);
		screen.getMenuStage().setGameListener(menulist);

		setBackButtonDisabled(true);
		setNextPlayerButtonDisabled(true);
		setDiscardButtonDisabled(true);
		setRotateButtonDisabled(true);

	}

	/**
	 * Zresetowanie calego widoku do ustawien poczatkowych.
	 */
	public void resetView()
	{

		screen.getBoardStage().resetBoardTable();
		screen.getMenuStage().playersContainerReset();
		updatePlayedCard(null);

		placeCardOnBoard(6, 2, new StartCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight));

		placeCardOnBoard(4, 10, null);
		placeCardOnBoard(6, 10, null);
		placeCardOnBoard(8, 10, null);

		setBackButtonDisabled(true);
		setNextPlayerButtonDisabled(true);
		setDiscardButtonDisabled(true);
		setRotateButtonDisabled(true);

	}

	public GameScreen getGameScreen()
	{
		return screen;
	}

	/**
	 * Ustawienie nasluchiwaczy do poszczegolnych scen.
	 * 
	 * @param userlist
	 *            Nasluchiwacz dla sceny z kartami.
	 * @param boardlist
	 *            Nasluchiwacz dla planszy.
	 * @param menulist
	 *            Nasluchwiacz dla menu.
	 */
	public void setListeners(final UserListener userlist, final BoardListener boardlist, final MenuListener menulist)
	{
		screen.getUserStage().setGameListener(userlist);
		screen.getBoardStage().setGameListener(boardlist);
		screen.getMenuStage().setGameListener(menulist);
	}

	/**
	 * Rotacja zaznaczonej karty.
	 */
	public void rotateSelected()
	{
		screen.getUserStage().rotateSelected();

	}

	/**
	 * Zaznaczenie wybranej karty.
	 * 
	 * @param actor
	 *            Karta do zaznaczenia.
	 */
	public void setSelectedCard(final Actor actor)
	{
		screen.getUserStage().setSelectedCard(actor);
	}

	/**
	 * Polozenie karty na planszy.
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
		screen.getBoardStage().placeCardOnBoard(x, y, card);

	}

	/**
	 * Polozenie zaznaczonej karty na planszy.
	 * 
	 * @param target
	 *            Cel na planzszy.
	 */
	public void placeSelectedCardOnBoard(final Actor target)
	{

		Actor selectedCard = screen.getUserStage().getSelectedCard();

		if (selectedCard != null && !(target instanceof Image))
		{

			screen.getBoardStage().placeCardOnBoard(target, selectedCard);
			screen.getUserStage().removeSelection();
		}
	}

	/**
	 * Wyswietlenia kart.
	 * 
	 * @param cards
	 *            Lista kart do wyswietlenia.
	 */
	public void drawCards(final List<Card> cards)
	{
		screen.getUserStage().setCards(cards);

	}

	/**
	 * Zwraca wspolrzedna w postaci obiektu punktu na podstawie znajdujacego sie
	 * tam actora.
	 * 
	 * @param selectedActor
	 *            Actor ktorego wspolrzedne obliczamy.
	 * @return Wsolrzedna w postaci obiektu punktu.
	 */
	public Point getSelectedBoardCoord(final Actor selectedActor)
	{
		Cell<Actor> cell = screen.getBoardStage().getBoardTable().getCell(selectedActor);
		return new Point(cell.getColumn(), cell.getRow());
	}

	/**
	 * 
	 * @return Zwraca ID zaznaczonej karty.
	 */
	public int getSelectedCardID()
	{
		Cell<Actor> cell = screen.getUserStage().getUserTable().getCell(screen.getUserStage().getSelectedCard());
		return cell.getColumn();
	}

	/**
	 * Usuwana zaznaczona karte.
	 */
	public void removeSelectedCard()
	{
		Actor selectedCard = screen.getUserStage().getSelectedCard();
		screen.getUserStage().removeSelection();
		lastPlayedCardsCell = screen.getUserStage().getUserTable().getCell(selectedCard);
		lastPlayedCardsCell.clearActor();
		lastPlayedCardsCell.padRight(0);

	}

	/**
	 * Sprawdza czy jest zaznaczona jakas karta.
	 * 
	 * @return
	 */
	public boolean isCardSelected()
	{
		return screen.getUserStage().getSelectedCard() == null ? false : true;
	}

	/**
	 * Zablokowanie lub odblokowanie danego gracza
	 * 
	 * @param blocked
	 *            Blokada lub nie
	 * @param playerID
	 *            Identyfikator gracza.
	 */
	public void setBlocked(final boolean blocked, final int playerID)
	{
		screen.getMenuStage().setBlocked(blocked, playerID);
	}

	/**
	 * Czy zaznaczona karta jest odwrocona.
	 * 
	 * @return
	 */
	public boolean getRotated()
	{
		return screen.getUserStage().getRotated();
	}

	/**
	 * Zresetowanie wartosci odwrocenia.
	 */
	public void resetRotated()
	{
		screen.getUserStage().resetRotated();
	}

	/**
	 * Usuniecie karty z planszy.
	 * 
	 * @param x
	 *            Wspolrzedne X.
	 * @param y
	 *            Wspolrzedna Y.
	 */
	public void removeFromBoard(final int x, final int y)
	{
		screen.getBoardStage().setBoardFieldDefault(x, y);
	}

	/**
	 * Aktualizacja ostanio zagranej karty.
	 * 
	 * @param card
	 *            Karta ktora bylo ostanio zagrana.
	 */
	public void updatePlayedCard(final Card card)
	{
		screen.getMenuStage().updatePlayedCard(card);
	}

	/**
	 * Aktualizacja gracza ktory ostatnio wykonywal ruch.
	 * 
	 * @param playerID
	 *            Identyfikator gracza.
	 */
	public void updatePlayerPlayed(int playerID)
	{
		screen.getMenuStage().updatePlayedPlayer(playerID);
	}

	/**
	 * Aktualizacja widoku na podstawie ostatnio wykonanego ruch(tury).
	 * 
	 * @param turn
	 *            Obiekt ruchu.
	 * @param playerID
	 *            Identyfikator gracza.
	 */
	public void updateView(final Turn turn, final int playerID)
	{
		if (turn != null)
		{
			Card card = turn.getCard();
			Point boardTarget = turn.getBoardTarget();
			int agentTarget = turn.getAgentTarget();
			if (card instanceof TunnelCard)
			{
				placeCardOnBoard(boardTarget.getX(), boardTarget.getY(), (TunnelCard) card);
			}
			else if (card instanceof DemolishCard)
			{
				removeFromBoard(boardTarget.getX(), boardTarget.getY());
			}
			else if (card instanceof BlockCard)
			{
				setBlocked(true, agentTarget);
			}
			else if (card instanceof DeblockCard)
			{
				setBlocked(false, agentTarget);
			}

			updatePlayedCard(card);
			updatePlayerPlayed(playerID);
		}
		else// discard
		{
			updatePlayerPlayed(playerID);
			updatePlayedCard(null);

		}
	}

	/**
	 * Zmiana roli gracza.
	 * 
	 * @param saboteur
	 *            Rola gracza.
	 */
	public void setPlayerRole(Boolean saboteur)
	{
		screen.getMenuStage().setPlayerRole(saboteur);
	}

	/**
	 * Pokazanie dialogu konca gry.
	 */
	public void showExitDialog()
	{
		ExitDialog dialog = new ExitDialog("", screen.getSkin(), new DialogListener(bq));
		screen.blockScreen();
		dialog.show(screen.getBoardStage());
	}

	/**
	 * Pokazanei dialogu rozpoczecia nowej gry.
	 */
	public void showNewGameDialog()
	{
		DialogListener dl = new DialogListener(bq);
		NewGameDialog dialog = new NewGameDialog("", screen.getSkin(),dl);
		dl.setNewGameDialog(dialog);
		screen.blockScreen();
		dialog.show(screen.getBoardStage());
	}

	/**
	 * Pokazanie dialogu informujacego na koniec gry.
	 * 
	 * @param info
	 *            Informacja.
	 */
	public void showEndGameInfoDialog(final String info)
	{
		EndGameInfoDialog dialog = new EndGameInfoDialog("", screen.getSkin(), new DialogListener(bq), info);
		screen.blockScreen();
		dialog.show(screen.getBoardStage());
	}

	/**
	 * Pokazanie dialogu informujacego.
	 * 
	 * @param info
	 *            Informacja.
	 */
	public void showInfoDialog(final String info)
	{
		InfoDialog dialog = new InfoDialog("", screen.getSkin(), new DialogListener(bq), info);
		screen.blockScreen();
		dialog.show(screen.getBoardStage());
	}

	/**
	 * Zablokowanie 2 scen - menu oraz sceny z kartami.
	 */
	public void blockScreen()
	{
		screen.blockScreen();
	}

	/**
	 * Odblokowanie 2 scen - menu oraz sceny z kartami.
	 */
	public void unblockScreen()
	{
		screen.unblockScreen();
	}

	/**
	 * Wylaczenie/ wlaczenie przycisku cofniecia.
	 * 
	 * @param disabled
	 */
	public void setBackButtonDisabled(boolean disabled)
	{
		screen.getMenuStage().getBackButton().setDisabled(disabled);
		if (disabled)
			screen.getMenuStage().getBackButton().setTouchable(Touchable.disabled);
		else
			screen.getMenuStage().getBackButton().setTouchable(Touchable.enabled);
	}

	/**
	 * Wylaczenie/ wlaczenie przycisku rotacji karty.
	 * 
	 * @param disabled
	 */
	public void setRotateButtonDisabled(boolean disabled)
	{
		screen.getMenuStage().getRotateButton().setDisabled(disabled);
		if (disabled)
			screen.getMenuStage().getRotateButton().setTouchable(Touchable.disabled);
		else
			screen.getMenuStage().getRotateButton().setTouchable(Touchable.enabled);
	}

	/**
	 * Wylaczenie/ wlaczenie przycisku odrzucenia jednej z kart.
	 * 
	 * @param disabled
	 */
	public void setDiscardButtonDisabled(boolean disabled)
	{
		screen.getMenuStage().getDiscardButton().setDisabled(disabled);
		if (disabled)
			screen.getMenuStage().getDiscardButton().setTouchable(Touchable.disabled);
		else
			screen.getMenuStage().getDiscardButton().setTouchable(Touchable.enabled);
	}

	/**
	 * Wylaczenie/ wlaczenie przycisku nastepnego gracza.
	 * 
	 * @param disabled
	 */
	public void setNextPlayerButtonDisabled(boolean disabled)
	{
		screen.getMenuStage().getNextRoundButton().setDisabled(disabled);
		if (disabled)
			screen.getMenuStage().getNextRoundButton().setTouchable(Touchable.disabled);
		else
			screen.getMenuStage().getNextRoundButton().setTouchable(Touchable.enabled);
	}

	/**
	 * Ustawienie proporcji sceny z kartami do sceny z plansza.
	 * 
	 * @param ratio
	 *            Proporcja.
	 */
	public void setVerticalRatio(double ratio)
	{
		screen.setVerticalRatio(ratio);
	}

	static BitmapFont generateScaledFont(int fontsize)
	{
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/arial.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = Gdx.graphics.getWidth() * fontsize / 1280;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
		return font;
	}
}
