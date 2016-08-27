/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package pl.zygmunt.tests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.events.BoardClickedEvent;
import pl.zygmunt.events.ButtonBackClickedEvent;
import pl.zygmunt.events.ButtonDiscardClickedEvent;
import pl.zygmunt.events.ButtonNextPlayerClickedEvent;
import pl.zygmunt.events.RotateButtonClickedEvent;
import pl.zygmunt.testUtils.LibGdxTestRunner;
import pl.zygmunt.view.MenuStage;
import pl.zygmunt.view.View;
import pl.zygmunt.view.ZoomableBoard;

@RunWith(LibGdxTestRunner.class)
public class ViewTests
{
	BlockingQueue<ApplicationEvent> bq = new LinkedBlockingQueue<ApplicationEvent>();
	View view = new View(bq);
	
	@Test
	public void testStagesInitialization()
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				Assert.assertNotNull(view.getGameScreen());
				
				Assert.assertNotNull(view.getGameScreen().getBoardStage());

				Assert.assertNotNull(view.getGameScreen().getMenuStage());

				Assert.assertNotNull(view.getGameScreen().getUserStage());
				
				Assert.assertNotNull(view.getGameScreen().getMenuStage().getBackButton());

				Assert.assertNotNull(view.getGameScreen().getMenuStage().getDiscardButton());

				Assert.assertNotNull(view.getGameScreen().getMenuStage().getNextRoundButton());

			}
		});
	}
	
	@Test
	public void testBoardInitialization()
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				
				Assert.assertTrue(bq.isEmpty());
				Table boardTable = view.getGameScreen().getBoardStage().getBoardTable();
				
				int boardSize = 13 * 13;
				
				Assert.assertEquals(boardSize, boardTable.getCells().size);
				
				Assert.assertTrue(bq.isEmpty());

			}
		});

	}
	
	@Test
	public void testEvents1()
	{

		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				Assert.assertTrue(bq.isEmpty());
				Table boardTable = view.getGameScreen().getBoardStage().getBoardTable();
				
				Assert.assertTrue(bq.isEmpty());
				
				LibGdxTestRunner.simulateClick(boardTable.getCells().get(0).getActor());
				
				Assert.assertFalse(bq.isEmpty());
				
				ApplicationEvent event = null;
				
				try
				{
					event = bq.take();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Assert.assertTrue(event instanceof BoardClickedEvent);
				
			}
		});

	}
	
	
	@Test
	public void testEvents2()
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				Assert.assertTrue(bq.isEmpty());
				Table boardTable = view.getGameScreen().getBoardStage().getBoardTable();
				MenuStage menuStage = view.getGameScreen().getMenuStage();
				
				Assert.assertTrue(bq.isEmpty());
				
				LibGdxTestRunner.simulateClick(boardTable.getCells().get(0).getActor());
				LibGdxTestRunner.simulateClick(boardTable.getCells().get(0).getActor());
				LibGdxTestRunner.simulateClick(menuStage.getBackButton());
				LibGdxTestRunner.simulateClick(menuStage.getDiscardButton());
				
				
				Assert.assertFalse(bq.isEmpty());
				Assert.assertEquals(4, bq.size());
				
				ApplicationEvent event1 = null;
				ApplicationEvent event2 = null;
				ApplicationEvent event3 = null;
				ApplicationEvent event4 = null;

				
				try
				{
					event1 = bq.take();
					event2 = bq.take();
					event3 = bq.take();
					event4 = bq.take();

				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Assert.assertTrue(event1 instanceof BoardClickedEvent);
				Assert.assertTrue(event2 instanceof BoardClickedEvent);
				Assert.assertTrue(event3 instanceof ButtonBackClickedEvent);
				Assert.assertTrue(event4 instanceof ButtonDiscardClickedEvent);
				
			}
		});

	}
	
	@Test
	public void testEvents3()
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				Assert.assertTrue(bq.isEmpty());
				Table boardTable = view.getGameScreen().getBoardStage().getBoardTable();
				MenuStage menuStage = view.getGameScreen().getMenuStage();
				
				Assert.assertTrue(bq.isEmpty());
				
				LibGdxTestRunner.simulateClick(menuStage.getBackButton());
				LibGdxTestRunner.simulateClick(menuStage.getDiscardButton());
				LibGdxTestRunner.simulateClick(menuStage.getNextRoundButton());
				LibGdxTestRunner.simulateClick(menuStage.getRotateButton());
				LibGdxTestRunner.simulateClick(boardTable.getCells().get(0).getActor());
				LibGdxTestRunner.simulateClick(boardTable.getCells().get(0).getActor());			
				
				Assert.assertFalse(bq.isEmpty());
				Assert.assertEquals(6, bq.size());
				
				ApplicationEvent event1 = null;
				ApplicationEvent event2 = null;
				ApplicationEvent event3 = null;
				ApplicationEvent event4 = null;
				ApplicationEvent event5 = null;
				ApplicationEvent event6 = null;
			
				try
				{
					event1 = bq.take();
					event2 = bq.take();
					event3 = bq.take();
					event4 = bq.take();
					event5 = bq.take();
					event6 = bq.take();

				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Assert.assertTrue(event1 instanceof ButtonBackClickedEvent);
				Assert.assertTrue(event2 instanceof ButtonDiscardClickedEvent);
				Assert.assertTrue(event3 instanceof ButtonNextPlayerClickedEvent);
				Assert.assertTrue(event4 instanceof RotateButtonClickedEvent);
				Assert.assertTrue(event5 instanceof BoardClickedEvent);
				Assert.assertTrue(event6 instanceof BoardClickedEvent);
				
				Assert.assertTrue(bq.isEmpty());
				
			}
		});

	}
	
	@Test
	public void testZoom()
	{
		Gdx.app.postRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				Assert.assertTrue(bq.isEmpty());
				ZoomableBoard zoomboard = view.getGameScreen().getBoardStage().getZoomableBoard();
				
				float scaleX = zoomboard.getTable().getScaleX();
				float scaleY = zoomboard.getTable().getScaleY();
				
				float zoomValue = 2.4f;
				
				Assert.assertEquals(1, scaleX, 0);
				Assert.assertEquals(1, scaleY, 0);
				
				zoomboard.zoom(zoomValue);
				
				float newScaleX = zoomboard.getTable().getScaleX();
				float newScaleY = zoomboard.getTable().getScaleY();
				
				Assert.assertEquals(newScaleX, scaleX + zoomValue, 0);
				Assert.assertEquals(newScaleY, scaleY + zoomValue , 0);
			}
		});

	}
}