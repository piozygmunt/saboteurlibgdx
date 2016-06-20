package pl.zygmunt.saboteur;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Game;

import pl.zygmunt.controller.Controller;
import pl.zygmunt.events.ApplicationEvent;
import pl.zygmunt.model.GameProperties;
import pl.zygmunt.model.Model;
import pl.zygmunt.view.View;

/**
 * Glowna klasa gry.
 * 
 * @author Piotr Zygmunt
 *
 */
public class Saboteur extends Game
{
	private View view;
	private Model model;
	private Controller controller;
	private BlockingQueue<ApplicationEvent> bq;

	@Override
	public void create()
	{
		GameProperties.setAmountOfPlayers(5);

		bq = new LinkedBlockingQueue<ApplicationEvent>();
		view = new View(bq);
		model = new Model();

		controller = new Controller(model, view, bq);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				controller.dzialaj();
			}
		}).start();
		setScreen(view.getGameScreen());

	}

	@Override
	public void render()
	{
		super.render();
	}
}
