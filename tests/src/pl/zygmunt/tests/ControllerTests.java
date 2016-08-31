package pl.zygmunt.tests;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import pl.zygmunt.common.Direction;
import pl.zygmunt.controller.Controller;
import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.model.BlockCard;
import pl.zygmunt.model.Card;
import pl.zygmunt.model.DeblockCard;
import pl.zygmunt.model.GameProperties;
import pl.zygmunt.model.Model;
import pl.zygmunt.model.TunnelCard;
import pl.zygmunt.testUtils.TestLibGDXHelper;
import pl.zygmunt.view.View;

@RunWith(TestLibGDXHelper.class)
public class ControllerTests
{
	private BlockingQueue<ApplicationEvent> bq;
	private View view;
	private Model model;
	private Controller controller;
	
	@Before
	public void init()
	{
		GameProperties.setHumanPlayer(true);
		bq = new LinkedBlockingQueue<ApplicationEvent>();
		view = new View(bq);
		model = new Model();
		controller = new Controller(model, view ,bq);
	}
	
	@Test
	public void testControllerInit()
	{
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run()
			{
				Assert.assertEquals(controller.getModel(), model);
				Assert.assertEquals(controller.getView(), view);
				Assert.assertEquals(controller.getBq(), bq);
				Assert.assertNotNull(controller.getEventDictionary());
			}
			
		});
	}
	
	@Test
	public void eventProccesing()
	{
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run()
			{				
				Assert.assertFalse(view.isCardSelected());
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(1).getActor());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertEquals(1, view.getSelectedCardID());
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(1).getActor());
				
				controller.processEvents(false);
				
				Assert.assertFalse(view.isCardSelected());
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(2).getActor());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertEquals(2, view.getSelectedCardID());
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(4).getActor());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertEquals(4, view.getSelectedCardID());

			}
			
		});
	}
	
	@Test
	public void eventProccesing2()
	{
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run()
			{
				Set<Direction> tunnels = new HashSet<Direction>();
				tunnels.add(Direction.Up);
				tunnels.add(Direction.Left);
				tunnels.add(Direction.Down);
				
				Set<Direction> openTunnels = new HashSet<Direction>();
				openTunnels.add(Direction.Up);
				openTunnels.add(Direction.Left);
				openTunnels.add(Direction.Down);
				
				TunnelCard tunnelCard = new TunnelCard(tunnels,openTunnels);
				
				model.getActivePlayer().getCards().clear();
				for(int i = 0 ; i < 6 ; ++i)
					model.getActivePlayer().getCards().add((TunnelCard) tunnelCard.clone());
				
				view.drawCards(model.getActivePlayer().getCards());
				
				Assert.assertFalse(view.isCardSelected());
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(1).getActor());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertEquals(1, view.getSelectedCardID());
				Assert.assertFalse(view.getRotated());
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getRotateButton());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertTrue(view.getRotated());	
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getRotateButton());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertFalse(view.getRotated());	
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(1).getActor());
				
				controller.processEvents(false);
				
				Assert.assertFalse(view.isCardSelected());

			}
			
		});
	}
	
	@Test
	public void eventProccesing3()
	{
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run()
			{
				// inicjalizaacja kart
				BlockCard blockCard = new BlockCard();
				DeblockCard deblockCard = new DeblockCard();
				
				//dodanie kart 
				model.getActivePlayer().getCards().clear();
				model.getActivePlayer().getCards().add((Card) deblockCard.clone());
				model.getActivePlayer().getCards().add((Card) deblockCard.clone());
				for(int i = 0 ; i < 4 ; ++i)
					model.getActivePlayer().getCards().add((Card) blockCard.clone());
				
				view.drawCards(model.getActivePlayer().getCards());
				
				// sprawdzenie zaznaczenia i zaznaczenie
				Assert.assertFalse(view.isCardSelected());
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(3).getActor());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertEquals(3, view.getSelectedCardID());
				Assert.assertFalse(view.getRotated());
				
				// sprawdzenie koloru przy graczu (bialy odblokowany, czerwony zablokowany)
				Table table = (Table) view.getGameScreen().getMenuStage().getPlayersContainer().getCells().get(1).getActor();
				Image image = (Image) table.getCells().get(0).getActor();
				Assert.assertEquals(image.getColor(), Color.WHITE);
				
				TestLibGDXHelper.simulateClick(image);
				controller.processEvents(false);
				
				Assert.assertFalse(view.isCardSelected());
				image = (Image) table.getCells().get(0).getActor();

				Assert.assertEquals(image.getColor(), Color.RED);
				
				
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getBackButton());
				
				controller.processEvents(false);
				
				image = (Image) table.getCells().get(0).getActor();

				Assert.assertEquals(image.getColor(), Color.WHITE);
						
			}
			
		});
	}
	
	
	@Test
	public void eventProccesing4()
	{
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run()
			{
				Set<Direction> tunnels = new HashSet<Direction>();
				tunnels.add(Direction.Up);
				tunnels.add(Direction.Left);
				tunnels.add(Direction.Down);
				
				Set<Direction> openTunnels = new HashSet<Direction>();
				openTunnels.add(Direction.Up);
				openTunnels.add(Direction.Left);
				openTunnels.add(Direction.Down);
				
				TunnelCard tunnelCard = new TunnelCard(tunnels,openTunnels);
				
				Table gameTable = view.getGameScreen().getBoardStage().getBoardTable();
				
				model.getActivePlayer().getCards().clear();
				for(int i = 0 ; i < 6 ; ++i)
					model.getActivePlayer().getCards().add((TunnelCard) tunnelCard.clone());
				
				view.drawCards(model.getActivePlayer().getCards());
				
				Assert.assertFalse(view.isCardSelected());
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(1).getActor());
				
				controller.processEvents(false);
				
				Assert.assertTrue(view.isCardSelected());
				Assert.assertEquals(1, view.getSelectedCardID());
				Assert.assertFalse(view.getRotated());
				
				// na polu 5x2 nie ma jeszcze karty
				Assert.assertFalse(gameTable.getCells().get(5 + 2*13).getActor() instanceof Image);
						
				TestLibGDXHelper.simulateClick(gameTable.getCells().get(5 + 2*13).getActor());
				
				controller.processEvents(false);
				
				// karta nie zostala umieszczona bo nie pasuje 
				Assert.assertFalse(gameTable.getCells().get(5 + 2*13).getActor() instanceof Image);
				
				// rotacja
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getRotateButton());

				controller.processEvents(false);
				
				TestLibGDXHelper.simulateClick(gameTable.getCells().get(5 + 2*13).getActor());
				
				controller.processEvents(false);
				
				// po rotacji karta zostala umieszczona 
				Assert.assertTrue(gameTable.getCells().get(5 + 2*13).getActor() instanceof Image);
				
				// wycofanie ruchu
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getBackButton());

				controller.processEvents(false);
				
				//sprawdzenie czy karta zostala usunieta
				Assert.assertFalse(gameTable.getCells().get(5 + 2*13).getActor() instanceof Image);
				
				//zaznaczenie karty jeszcze raz
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(1).getActor());
				
				controller.processEvents(false);
				
				//ulozenie karty jeszcze raz
				TestLibGDXHelper.simulateClick(gameTable.getCells().get(5 + 2*13).getActor());
				
				controller.processEvents(false);
							
				// zatwierdzenie 
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getNextRoundButton());

				controller.processEvents(false);
				
				// sprawdzamy, czy model zostal zauktualizowany (pamietajmy ze karta zostala odwrocona)
				Assert.assertEquals(model.getBoard()[5][2], TunnelCard.createRotatedCard(tunnelCard));
				
			}
			
		});
	}
	
	@Test
	public void eventProccesing5()
	{
		Gdx.app.postRunnable(new Runnable(){

			@Override
			public void run()
			{
				model.initialize();
				view.resetView();
				// inicjalizaacja kart
				BlockCard blockCard = new BlockCard();
				DeblockCard deblockCard = new DeblockCard();
				
				//dodanie kart 
				model.getActivePlayer().getCards().clear();
				model.getActivePlayer().getCards().add((Card) deblockCard.clone());
				model.getActivePlayer().getCards().add((Card) deblockCard.clone());
				for(int i = 0 ; i < 4 ; ++i)
					model.getActivePlayer().getCards().add((Card) blockCard.clone());
				
				view.drawCards(model.getActivePlayer().getCards());
				
				// sprawdzenie zaznaczenia i zaznaczenie
				Assert.assertFalse(view.isCardSelected());
				TestLibGDXHelper.simulateClick(view.getGameScreen().getUserStage().cardsTable().getCells().get(3).getActor());
				
				controller.processEvents(false);
				
				// sprawdzenie koloru przy graczu 1 (bialy odblokowany, czerwony zablokowany)
				Table table = (Table) view.getGameScreen().getMenuStage().getPlayersContainer().getCells().get(1).getActor();
				Image image = (Image) table.getCells().get(0).getActor();
				Assert.assertEquals(Color.WHITE, image.getColor());
				
				TestLibGDXHelper.simulateClick(image);
				controller.processEvents(false);
				
				Assert.assertFalse(view.isCardSelected());
				image = (Image) table.getCells().get(0).getActor();

				// sprawdzenie czy widok zostal zauktualizowany
				Assert.assertEquals( Color.RED, image.getColor());
				
				// sprawdzenie ze gracz nie byl zablokwany
				Assert.assertFalse(model.getPlayers().get(1).getBlocked());
				
				//zatwierdzenie ruchu
				TestLibGDXHelper.simulateClick(view.getGameScreen().getMenuStage().getNextRoundButton());
				controller.processEvents(false);
				
				// sprawdzenie ze gracz zostal zablokowany po ruchu
				Assert.assertTrue(model.getPlayers().get(1).getBlocked());
				
			}
			
		});
	}
}
