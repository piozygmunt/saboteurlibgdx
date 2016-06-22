package pl.zygmunt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import pl.zygmunt.common.Common;

/**
 * Obiekt reprezentujaca plansze do gry ktora mozna przyblizac lub oddalac.
 * 
 * @author Piotr Zygmunt
 *
 */
public class ZoomableBoard extends Stack
{
	/**
	 * Tabela ktora reprezentuje plansze.
	 */
	private Table contentTable;

	/**
	 * Konstruktor domyslny.
	 */
	public ZoomableBoard()
	{
		contentTable = new Table();
		contentTable.debug();

		this.add(contentTable);

		layout();

		// dodanie nasluchiwacza gestow co umolizwi przyblizanie / oddalanie na
		// urzadzeniach mobilnych
		this.addListener(new ActorGestureListener()
		{
			@Override
			public void zoom(InputEvent event, float initialDistance, float distance)
			{
				float zoom = initialDistance / distance;
				if (zoom < 1 && contentTable.getScaleX() < 1.15)
				{
					ZoomableBoard.this.zoom(0.005f * zoom);
					Gdx.app.log("GestureController", "" + zoom);

				}
				else if (zoom > 1 && contentTable.getScaleX() > 0.5)
				{
					ZoomableBoard.this.zoom(0.005f * (-(zoom / 10)));
					Gdx.app.log("GestureController", "" + (-(zoom / 10)));

				}
				Gdx.app.log("GestureController",
						"scale : " + contentTable.getScaleX() + " " + contentTable.getScaleY());
			}

		});

		contentTable.setTransform(true);
	}

	/**
	 * Zresetowanie tabeli. Wypelnienie pustymi aktorami.
	 */
	public void resetBoard()
	{
		contentTable.clearChildren();
		for (int i = 0; i < Common.initialRows; ++i)
		{
			for (int k = 0; k < Common.initialColumns; ++k)
			{
				final Actor actor = new Actor();
				contentTable.add(actor).size(150 * Gdx.graphics.getWidth() / 960, 200 * Gdx.graphics.getHeight() / 540);

			}
			contentTable.row();
		}
	}

	@Override
	public float getPrefWidth()
	{
		return contentTable.getWidth() * contentTable.getScaleX();
	}

	@Override
	public float getPrefHeight()
	{
		return contentTable.getHeight() * contentTable.getScaleY();
	}

	@Override
	public void layout()
	{
		float width = this.getWidth();
		float height = this.getHeight();

		contentTable.setSize(contentTable.getPrefWidth(), contentTable.getPrefHeight());

		float tableWidth = contentTable.getWidth();
		float tableHeight = contentTable.getHeight();

		contentTable.setOrigin(tableWidth / 2, tableHeight / 2);
		contentTable.setPosition((width / 2 - (tableWidth / 2)), (height / 2 - (tableHeight / 2)));
	}

	public Table getTable()
	{
		return contentTable;
	}

	/**
	 * Przyblizenie.
	 * 
	 * @param amount
	 *            Wartosc o jaka przyblizamy/ oddalamy( zwiekszamy/ zmniejszamy)
	 *            tabele w srodku.
	 */
	public void zoom(float amount)
	{
		contentTable.scaleBy(amount);
		invalidateHierarchy();
	}
}
