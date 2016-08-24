package pl.zygmunt.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import pl.zygmunt.common.Common;
import pl.zygmunt.common.Point;
import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.BoardClickedEvent;
import pl.zygmunt.events.ButtonBackClickedEvent;
import pl.zygmunt.events.ButtonDiscardClickedEvent;
import pl.zygmunt.events.ButtonExitClickedEvent;
import pl.zygmunt.events.ButtonNewGameClickedEvent;
import pl.zygmunt.events.ButtonNextPlayerClickedEvent;
import pl.zygmunt.events.ButtonOkInfoClickedEvent;
import pl.zygmunt.events.ContinueGameEvent;
import pl.zygmunt.events.ExitApplicationEvent;
import pl.zygmunt.events.PlayerLabelClickedEvent;
import pl.zygmunt.events.RotateButtonClickedEvent;
import pl.zygmunt.events.SelectedCardEvent;
import pl.zygmunt.events.StartNewGameEvent;
import pl.zygmunt.model.Agent;
import pl.zygmunt.model.BlockCard;
import pl.zygmunt.model.Card;
import pl.zygmunt.model.DeblockCard;
import pl.zygmunt.model.DemolishCard;
import pl.zygmunt.model.GameProperties;
import pl.zygmunt.model.GoalCard;
import pl.zygmunt.model.Model;
import pl.zygmunt.model.Player;
import pl.zygmunt.model.StartCard;
import pl.zygmunt.model.TunnelCard;
import pl.zygmunt.model.Turn;
import pl.zygmunt.model.ViewCard;
import pl.zygmunt.view.View;

/**
 * Klasa kontrolera. Klasa odpowiedzialna za wspó³dzialanie modelu i widoku.
 * Przyjmuje zdarzenia oraz odpowiednio na nie reaguje.
 * 
 * @author Piotr Zygmunt
 */
public final class Controller
{
	/** Referencja na obiekt klasy model */
	private final Model model;
	/** Referencja na obiekt klasy widok */
	private final View view;
	/** Mapa ktora dopasowuje strategie do danego zdarzenia */
	private final Map<Class<? extends ApplicationEvent>, ApplicationStrategy> eventDictionary = new HashMap<Class<? extends ApplicationEvent>, ApplicationStrategy>();
	/** Referencja na kolejke zdarzen */
	private final BlockingQueue<ApplicationEvent> bq;

	/**
	 * Konstruktor podstawowy przyjmuje referencje na : model, widok , kolejke
	 * zdarzen.
	 * 
	 * @param model
	 *            Referencja na model.
	 * @param view
	 *            Referencja na widok.
	 * @param bq
	 *            Referencja na kolejke zdarzeñ.
	 */
	public Controller(final Model model, final View view, final BlockingQueue<ApplicationEvent> bq)
	{
		this.model = model;
		this.view = view;
		this.bq = bq;
		/* Zapelnienie mapy */
		eventDictionary.put(SelectedCardEvent.class, new SelectedCardStrategy());
		eventDictionary.put(RotateButtonClickedEvent.class, new RotateButtonClickedStrategy());
		eventDictionary.put(BoardClickedEvent.class, new BoardClickedStrategy());
		eventDictionary.put(ButtonBackClickedEvent.class, new ButtonBackClickedStrategy());
		eventDictionary.put(ButtonNextPlayerClickedEvent.class, new ButtonNextRoundClickedStrategy());
		eventDictionary.put(PlayerLabelClickedEvent.class, new PlayerLabelClickedStrategy());
		eventDictionary.put(ButtonDiscardClickedEvent.class, new ButtonDiscardClickedStrategy());
		eventDictionary.put(ButtonExitClickedEvent.class, new ButtonExitClickedStrategy());
		eventDictionary.put(ExitApplicationEvent.class, new ExitApplicationStrategy());
		eventDictionary.put(ButtonNewGameClickedEvent.class, new ButtonNewGameClickedStrategy());
		eventDictionary.put(StartNewGameEvent.class, new StartNewGameStrategy());
		eventDictionary.put(ContinueGameEvent.class, new ContinueGameStrategy());
		eventDictionary.put(ButtonOkInfoClickedEvent.class, new ButtonOkInfoClickedStrategy());

		view.updatePlayerPlayed(model.getActivePlayer().getID() + 1);
		if(GameProperties.humanPlayer ) view.setPlayerRole(model.getActivePlayer().getRole());

		// jesli poczatkowy gracz to agent - plansza na caly ekran
		if (model.getActivePlayer() instanceof Agent)
		{
			view.setNextPlayerButtonDisabled(false);
			view.setVerticalRatio(1);
		}
		else
			view.drawCards(model.getActivePlayer().getCards());
	}

	/**
	 * Glowna funkcja kontrolera. W nieskonczonej petli oczekuje na zdarzenia.
	 * Jesli jakies jest to jest odbiera i odpowiednio reaguje ( wybiera
	 * strategie ).
	 */
	public void processEvents()
	{
		while (true)
		{
			try
			{
				final ApplicationEvent evnt = bq.take();
				final ApplicationStrategy strategy = eventDictionary.get(evnt.getClass());
				Gdx.app.postRunnable(new Runnable()
				{
					@Override
					public void run()
					{
						strategy.execute(evnt);
					}
				});
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie zaznaczenia karty.
	 */
	private final class SelectedCardStrategy extends ApplicationStrategy
	{
		@Override
		void execute(final ApplicationEvent evnt)
		{
			// zaznaczenie karty
			view.setSelectedCard(((SelectedCardEvent) evnt).getTarget());
			// czy zaznaczona jest jakas karta
			if (view.isCardSelected())
			{
				view.setDiscardButtonDisabled(false);
				Card card = model.getActivePlayer().getCards().get(view.getSelectedCardID());
				// jesli jest to karta tuneli to odblokowanie buttona do rotacji
				if (card instanceof TunnelCard)
					view.setRotateButtonDisabled(false);
				else
					view.setRotateButtonDisabled(true);

			}
			else
			{
				view.setDiscardButtonDisabled(true);
				view.setRotateButtonDisabled(true);
			}
		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie rotacji karty.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class RotateButtonClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(final ApplicationEvent evnt)
		{
			// jesli jest zaznaczona karta i aktualny gracz nie jest agenteme
			if (view.isCardSelected() && !(model.getActivePlayer() instanceof Agent))
			{
				Card card = model.getActivePlayer().getCards().get(view.getSelectedCardID());

				// jesli jest to karta tuneli
				if (card instanceof TunnelCard)
					view.rotateSelected();
			}
		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie klikniecia na planszy..
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class BoardClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(final ApplicationEvent evnt)
		{
			// jesli jest zaznaczona karta
			if (view.isCardSelected() && model.getLastTurn() == null && !(model.getActivePlayer() instanceof Agent))
			{
				Card card = model.getActivePlayer().getCards().get(view.getSelectedCardID());

				Actor target = ((BoardClickedEvent) evnt).getTarget();
				Point point = view.getSelectedBoardCoord(target);

				// jesli jest to karta tuneli i gracz nie jest zablokowany
				if (card instanceof TunnelCard && !model.getActivePlayer().getBlocked())
				{
					// jesli karta jest odwrocona
					if (view.getRotated())
					{
						((TunnelCard) card).rotateCard();
						view.resetRotated();
					}

					// sprawdzenie czy karta moze byc polozona w danym miejscu
					if (Common.checkIfCardCanBePlaced(model.getBoard(), (TunnelCard) card, (int) point.getX(),
							(int) point.getY()))
					{
						view.placeSelectedCardOnBoard(target);
						Turn turn = new Turn(card, 0);
						turn.setBoardTarget(new Point((int) point.getX(), (int) point.getY()));
						turn.setTurned(view.getRotated());
						view.removeSelectedCard();
						view.resetRotated();
						model.setLastTurn(turn);
						view.setBackButtonDisabled(false);
					}

				}

				// jesli jest to karta niszczenia
				if (card instanceof DemolishCard)
				{
					TunnelCard cardToRemove = model.getBoard()[(int) point.getX()][(int) point.getY()];

					// jesli wybrano karte do zniszczenia ktora nie jest karta
					// startowa i karta celu
					if (cardToRemove != null && !(cardToRemove instanceof StartCard)
							&& !(cardToRemove instanceof GoalCard))
					{
						view.removeFromBoard((int) point.getX(), (int) point.getY());
						Turn turn = new Turn(card, 0);
						turn.setBoardTarget(new Point((int) point.getX(), (int) point.getY()));
						turn.setDemolishedCard(cardToRemove);
						view.removeSelectedCard();
						model.setLastTurn(turn);
						view.setBackButtonDisabled(false);

					}
				}

				// jesli jest to karta widoku
				if (card instanceof ViewCard)
				{
					TunnelCard targetCard = model.getBoard()[point.getX()][point.getY()];
					if (targetCard != null && targetCard instanceof GoalCard
							&& model.getActivePlayer().knowsGoal(point.getX() / 2 - 1) == 0)
					{
						view.placeCardOnBoard((int) point.getX(), (int) point.getY(), (GoalCard) targetCard);
						Turn turn = new Turn(card, 0);
						turn.setBoardTarget(new Point((int) point.getX(), (int) point.getY()));
						view.removeSelectedCard();
						view.setBackButtonDisabled(true);
						model.setLastTurn(turn);
					}
				}

				view.setNextPlayerButtonDisabled(false);
				view.setRotateButtonDisabled(true);
				view.setDiscardButtonDisabled(true);

			}

		}

	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie klikniecia przycisku cofniecia.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ButtonBackClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(final ApplicationEvent evnt)
		{
			Turn lastTurn = model.getLastTurn();
			if (lastTurn != null)
			{
				Card card = lastTurn.getCard();
				Point boardTarget = lastTurn.getBoardTarget();
				int agentTarget = lastTurn.getAgentTarget();

				// jesli w poprzednim ruchu nie zostala odrzucona karta
				if (!lastTurn.isDiscard())
				{
					if (card instanceof TunnelCard)
					{
						view.removeFromBoard(boardTarget.getX(), boardTarget.getY());
					}

					else if (card instanceof DemolishCard)
					{
						view.placeCardOnBoard(boardTarget.getX(), boardTarget.getY(), lastTurn.getDemolishedCard());
					}

					else if (card instanceof BlockCard)
					{
						view.setBlocked(false, agentTarget);
					}

					else if (card instanceof DeblockCard)
					{
						view.setBlocked(true, agentTarget);

					}

					else if (card instanceof ViewCard)
					{
						return;
					}
				}
				view.drawCards(model.getActivePlayer().getCards());
				view.setBackButtonDisabled(true);
				view.setNextPlayerButtonDisabled(true);
				view.setDiscardButtonDisabled(true);
				view.setRotateButtonDisabled(true);
				model.setLastTurn(null);
			}
		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie nacisniecia przycisku nastepnego gracza.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ButtonNextRoundClickedStrategy extends ApplicationStrategy
	{
		@Override
		void execute(final ApplicationEvent evnt)
		{
			Turn lastTurn = model.getLastTurn();
			Player activePlayer = model.getActivePlayer();
			if (activePlayer instanceof Agent || lastTurn != null)
			{
				// jesli wykonano jakis ruch
				if (lastTurn != null)
				{
					model.removeCardFromPlayer(lastTurn.getCard(), activePlayer.getID());
					if (lastTurn.isDiscard())
						lastTurn = null;
					else if (lastTurn.getCard() instanceof ViewCard)
						view.placeCardOnBoard(lastTurn.getBoardTarget().getX(), lastTurn.getBoardTarget().getY(), null);
					model.updateModel(lastTurn);
					model.incrementTurn();
					model.setLastTurn(null);
					for (Integer pos : model.getDiscoveredGoalCardPositions())
						view.placeCardOnBoard(pos, 10, model.getBoard()[pos][10]);

					// sprawdzenie czy gra dobiegla konca
					if (model.checkForEndGame())
					{
						view.showEndGameInfoDialog("Game has finished. Winners: " + model.getWinners());
						model.printResults();
						return;
					}

				}

				// jesli ruch wykonuje agent
				if (model.getActivePlayer() instanceof Agent)
				{
					lastTurn = model.nextTurn();
					view.updateView(lastTurn, model.getActivePlayer().getID() + 1);
					for (Integer pos : model.getDiscoveredGoalCardPositions())
						view.placeCardOnBoard(pos, 10, model.getBoard()[pos][10]);
					if (model.checkForEndGame())
					{
						view.showEndGameInfoDialog("Game has finished. Winners: " + model.getWinners());
						model.printResults();
						return;
					}
					model.incrementTurn();
				}

				// jesli aktualnym graczem nie jest agent
				if (!(model.getActivePlayer() instanceof Agent))
				{
					view.setNextPlayerButtonDisabled(true);
					view.drawCards(model.getActivePlayer().getCards());
					view.showInfoDialog("Your turn now");
				}
				else
				{
					view.setNextPlayerButtonDisabled(false);
				}
				view.setBackButtonDisabled(true);
				view.setRotateButtonDisabled(true);
				view.setDiscardButtonDisabled(true);

			}
		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie klikniecia danego gracza.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class PlayerLabelClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{
			Actor targetsParent = ((PlayerLabelClickedEvent) evnt).getTargetsParent();

			// jesli jest zaznaczona karta
			if (view.isCardSelected() && model.getLastTurn() == null && !(model.getActivePlayer() instanceof Agent))
			{
				Card selectedCard = model.getActivePlayer().getCards().get(view.getSelectedCardID());

				// jesli jest to karta blokowanie lub odblokowanie i kliknieto
				// odpowiedni elements
				if ((selectedCard instanceof BlockCard || selectedCard instanceof DeblockCard)
						&& targetsParent.getClass().equals(Table.class) && targetsParent.getName() != null)
				{
					int playerID = Integer.parseInt(targetsParent.getName());
					Turn turn = new Turn(selectedCard, 0);
					turn.setAgentTarget(playerID);

					if (selectedCard instanceof BlockCard && !model.getPlayer(playerID).getBlocked())
					{
						view.setBlocked(true, playerID);
						view.removeSelectedCard();
						model.setLastTurn(turn);
						view.setBackButtonDisabled(false);
						view.setNextPlayerButtonDisabled(false);
					}
					else if (selectedCard instanceof DeblockCard && model.getPlayer(playerID).getBlocked())
					{
						view.setBlocked(false, playerID);
						view.removeSelectedCard();
						model.setLastTurn(turn);
						view.setBackButtonDisabled(false);
						view.setNextPlayerButtonDisabled(false);
					}

					view.setRotateButtonDisabled(true);
					view.setDiscardButtonDisabled(true);

				}
			}

		}

	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie nacisniecia przycisku odrzucenia jednej kart.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ButtonDiscardClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{

			if (view.isCardSelected() && model.getLastTurn() == null && !(model.getActivePlayer() instanceof Agent))
			{
				Card selectedCard = model.getActivePlayer().getCards().get(view.getSelectedCardID());
				Turn turn = new Turn(selectedCard, 0);
				turn.setDiscard(true);
				view.removeSelectedCard();
				view.setBackButtonDisabled(false);
				view.setNextPlayerButtonDisabled(false);
				view.setRotateButtonDisabled(true);
				view.setDiscardButtonDisabled(true);

				model.setLastTurn(turn);
			}

		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie nacisniecia przycisku konca gry.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ButtonExitClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{

			view.showExitDialog();

		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie zwierdzenie wyjsca z gry.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ExitApplicationStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{
			Gdx.app.exit();

		}

	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie nacisniecia przycisku nowej gry.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ButtonNewGameClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{

			view.showNewGameDialog();

		}
	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie rozpoczecia nowej gry.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class StartNewGameStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{
			GameProperties.setAmountOfPlayers(((StartNewGameEvent) evnt).getNumberOfPlayers());
			GameProperties.humanPlayer = ((StartNewGameEvent) evnt).isHumanPlayer();
			model.initialize();
			view.resetView();
			if (!(model.getActivePlayer() instanceof Agent))
				view.drawCards(model.getActivePlayer().getCards());
			view.updatePlayerPlayed(model.getActivePlayer().getID() + 1);
			if(GameProperties.humanPlayer ) 
			{
				view.setPlayerRole(model.getActivePlayer().getRole());
				view.setVerticalRatio(0.66);
			}
			else
			{
				view.setPlayerRole(null);
				view.setVerticalRatio(1);
				view.setNextPlayerButtonDisabled(false);
			}
			view.unblockScreen();

		}

	}

	/**
	 * Prywatna klasa dziedziczaca po ApplicationStrategy Reprezentuje strategie
	 * na zdarzenie kontynuacji gry, np po wyswietleniu dialogu.
	 * 
	 * @author Piotr Zygmunt
	 *
	 */
	private final class ContinueGameStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{
			// TODO Auto-generated method stub
			view.unblockScreen();
		}

	}

	private final class ButtonOkInfoClickedStrategy extends ApplicationStrategy
	{

		@Override
		void execute(ApplicationEvent evnt)
		{
			// TODO Auto-generated method stub
			view.unblockScreen();
			view.updatePlayedCard(null);
			view.updatePlayerPlayed(model.getActivePlayer().getID() + 1);
		}

	}

}