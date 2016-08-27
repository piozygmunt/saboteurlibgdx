package pl.zygmunt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import pl.zygmunt.common.Common;
import pl.zygmunt.listeners.MenuListener;
import pl.zygmunt.model.Card;
import pl.zygmunt.model.GameProperties;

/**
 * Klasa reprezentujaca Menu gry za pomoca ktorego sterujemy przebiegiem
 * rozgrywki.
 * 
 * @author Piotr Zygmunt
 *
 */
public class MenuStage extends Stage
{
	/**
	 * Tabela w ktorej umieszczone sa wszystkie elementy.
	 */
	private Table menuTable;
	/**
	 * Przycisk cofniecia ruchu.
	 */
	private TextButton backButton;
	/**
	 * Przycisk nastepnego gracza.
	 */
	private TextButton nextRoundButton;
	/**
	 * Przycisk odrzucenia karty.
	 */
	private TextButton discardButton;
	/**
	 * Przycisk konca gry.
	 */
	private TextButton exitButton;
	/**
	 * Przycisk nowej gry.
	 */
	private TextButton newGameButton;
	/**
	 * Przycisk do rotacji.
	 */
	private TextButton rotateButton;
	/**
	 * Tabela zawieracja graczy.
	 */
	private Table playersContainer;

	private Texture blocked;
	private Texture deblocked;
	private Texture background;
	private Label playerLabel;
	private Label playersRoleLabel;
	private Label roleLabel;
	private Cell<Image> lastPlayedCardCell;
	private Skin skin;
	private AssetManager manager;

	/**
	 * Konstruktor i inicjalizacja sceny.
	 * 
	 * @param skin
	 *            Skin.
	 * @param manager
	 *            Zarzadca wczytywnych elementow.
	 */
	public MenuStage(Skin skin, AssetManager manager)
	{
		this.skin = skin;
		this.manager = manager;
		menuTable = new Table();
		backButton = new TextButton("Back", skin);
		backButton.setName("BackBtn");

		nextRoundButton = new TextButton("Next Player", skin);
		nextRoundButton.setName("nextRoundBtn");

		discardButton = new TextButton("Discard", skin);
		discardButton.setName("discardBtn");

		rotateButton = new TextButton("Rotate", skin);
		rotateButton.setName("Rotate");

		exitButton = new TextButton("Exit", skin);
		exitButton.setName("exitBtn");

		newGameButton = new TextButton("New Game", skin);
		newGameButton.setName("newGameBtn");

		playerLabel = new Label("Player played", skin);
		playersRoleLabel = new Label("Role: ", skin);
		playersRoleLabel.setVisible(false);
		roleLabel = new Label(null, skin);

		updateFontSize(24);

		Pixmap blockedpix = new Pixmap(20, 20, Format.RGBA8888);
		blockedpix.setColor(Color.rgba8888(Color.RED));
		blockedpix.fill();

		blocked = new Texture(blockedpix);

		blockedpix.setColor(Color.rgba8888(Color.WHITE));
		blockedpix.fill();

		deblocked = new Texture(blockedpix);

		blockedpix.setColor(Color.rgba8888(Color.DARK_GRAY));
		blockedpix.fill();

		background = new Texture(blockedpix);

		playersContainer = new Table(skin);

		playersContainerReset();

		HorizontalGroup group = new HorizontalGroup();

		menuTable.add(newGameButton).padBottom(5).colspan(2).padTop(5);
		menuTable.row();
		menuTable.add(nextRoundButton).padBottom(5).colspan(2);
		
		menuTable.row();
		menuTable.add(discardButton).padBottom(5).padLeft(5).padRight(2);
		menuTable.add(rotateButton).padBottom(5).padRight(5);
		menuTable.row();
		menuTable.add(backButton).padBottom(5);
		menuTable.add(exitButton).padBottom(5);
		menuTable.row();

		group.addActor(playersRoleLabel);
		group.addActor(roleLabel);

		menuTable.add(group).colspan(2);
		menuTable.row();
		ScrollPane pane = new ScrollPane(playersContainer, skin, "default2");
		pane.setScrollbarsOnTop(false);
		pane.setFadeScrollBars(false);
		menuTable.add(pane).padBottom(10).padLeft(5).padRight(5).expandX().fillX().colspan(2);
		menuTable.row();
		menuTable.add(playerLabel).colspan(2);
		;
		menuTable.row();
		menuTable.add(new Label("Card played: ", skin)).colspan(2);
		;
		menuTable.row();
		lastPlayedCardCell = menuTable.add(Common.getImageCardFromAssetManager(manager, null)).colspan(2).pad(10, 10,
				10, 10);
		menuTable.setFillParent(true);

		this.addActor(menuTable);
	}

	public TextButton getBackButton()
	{
		return backButton;
	}

	public TextButton getNextRoundButton()
	{
		return nextRoundButton;
	}

	public TextButton getDiscardButton()
	{
		return discardButton;
	}

	public TextButton getRotateButton()
	{
		return rotateButton;
	}

	/**
	 * Ustawienie nasluchiwacza zdarzen.
	 * 
	 * @param gl
	 *            Nasluchiwacz
	 */
	public void setGameListener(MenuListener gl)
	{
		menuTable.addListener(gl);
	}

	/**
	 * Blokada lub odblokowanie danego gracz.
	 * 
	 * @param blocked
	 *            Czy gracz ma zostac zablokowany.
	 * @param playerID
	 *            Identyfikator gracza.
	 */
	public void setBlocked(boolean blocked, int playerID)
	{
		Cell<Table> playerCell = playersContainer.getCells().get(playerID);
		Table playerTable = playerCell.getActor();

		Image newImage = blocked ? new Image(this.blocked) : new Image(this.deblocked);
		newImage.setColor(blocked? Color.RED : Color.WHITE);
		
		playerTable.getCells().get(0).setActor(newImage);
	}

	/**
	 * Aktualizacja ostatnio zagrywanej karty.
	 * 
	 * @param card
	 *            Karta.
	 */
	public void updatePlayedCard(Card card)
	{
		lastPlayedCardCell.setActor(Common.getImageCardFromAssetManager(manager, card));
	}

	/**
	 * Aktualizacja informacji o graczu ktory wykonywal ruch.
	 * 
	 * @param player
	 */
	public void updatePlayedPlayer(int player)
	{
		playerLabel.setText("Current player: " + player);
	}

	/**
	 * Ustawienie roli gracza.
	 * 
	 * @param saboteur
	 *            Rola.
	 */
	public void setPlayerRole(final Boolean saboteur)
	{
		if(saboteur == null )
		{
			playersRoleLabel.setVisible(false);
			roleLabel.setVisible(false);
			return;
			
		}
		playersRoleLabel.setVisible(true);
		LabelStyle style = new LabelStyle(roleLabel.getStyle());

		if (saboteur)
		{
			roleLabel.setText("SABOTEUR");
			style.fontColor = Color.RED;
		}
		else
		{
			roleLabel.setText("DWARF");
			style.fontColor = Color.GOLD;
		}
		roleLabel.setStyle(style);

	}

	/**
	 * Zresetowanie tabeli zawierajacej graczy.
	 */
	public void playersContainerReset()
	{
		playersContainer.clearChildren();

		for (int i = 0; i < GameProperties.numberOfPlayers; ++i)
		{
			final Table table = new Table(skin);
			table.add(new Image(deblocked)).padLeft(10);
			table.add(new Label("Player " + (i + 1), skin, "optional")).expandY().fillY().padRight(10).padLeft(5);
			table.setName("" + i);
			table.addListener(new FocusListener()
			{
				@Override
				public boolean handle(Event event)
				{

					if (event.toString().equals("mouseMoved"))
					{
						table.background(new TextureRegionDrawable(new TextureRegion(background)));
						return false;
					}
					else if (event.toString().equals("exit"))
					{
						// table1.setBackground(null);
						// table1.background("");
						table.setBackground((Drawable) null);

						return false;
					}
					return true;
				}
			});
			playersContainer.add(table);
			playersContainer.row();
		}
	}

	/**
	 * Aktualizacja rozmiaru czcionki z zgodnie z rozmiarem wyswietlacza.
	 * 
	 * @param fontsize
	 *            Rozmiar czcionki.
	 */
	private void updateFontSize(int fontsize)
	{
		BitmapFont font = View.generateScaledFont(fontsize);
		TextButtonStyle style = nextRoundButton.getStyle();
		style.font = font;
		style.disabledFontColor = Color.GRAY;

		nextRoundButton.setStyle(style);
		backButton.setStyle(style);
		rotateButton.setStyle(style);
		discardButton.setStyle(style);
		exitButton.setStyle(style);
		newGameButton.setStyle(style);

		LabelStyle labelStyle = this.playerLabel.getStyle();
		labelStyle.font = font;
		playerLabel.setStyle(labelStyle);
		playersRoleLabel.setStyle(labelStyle);
		roleLabel.setStyle(labelStyle);

	}
	
	public Table getPlayersContainer()
	{
		return playersContainer;
	}
	
	public Texture getBlockedTexture()
	{
		return blocked;
	}

	public Texture getDeblockedTexture()
	{
		return deblocked;
	}


	public void dispose()
	{
		this.background.dispose();
		this.blocked.dispose();
		this.deblocked.dispose();
	}

}
