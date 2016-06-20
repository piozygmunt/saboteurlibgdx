package pl.zygmunt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Klasa reprezentujaca wyswietlane okno gry.
 * 
 * @author Piotrek
 *
 */
public class GameScreen implements Screen
{
	/**
	 * Skin - wyglad poszczegolnych elementow.
	 */
	private Skin skin;
	private Viewport viewportB;
	private Viewport viewportUC;
	private Viewport viewportUI;

	/**
	 * Scena z plansza.
	 */
	private BoardStage boardStage;
	/**
	 * Scena z menu.
	 */
	private MenuStage menuStage;
	/**
	 * Scena z kartami do gry.
	 */
	private UserStage userStage;

	/**
	 * Zarzadca wczytywanymi plikami(teksturami, atlasami itp).
	 */
	private AssetManager assetManager;

	/**
	 * Obiekt odpowiedzialny na przyjmowanie zdarzen wejsciowych (klikniecia,
	 * przesuwanie)
	 */
	private InputMultiplexer inputMultiplexer;

	private double verticalRatio;

	public BoardStage getBoardStage()
	{
		return boardStage;
	}

	public void setBoardStage(BoardStage boardStage)
	{
		this.boardStage = boardStage;
	}

	public MenuStage getMenuStage()
	{
		return menuStage;
	}

	public void setMenuStage(MenuStage menuStage)
	{
		this.menuStage = menuStage;
	}

	public UserStage getUserStage()
	{
		return userStage;
	}

	public void setUserStage(UserStage userStage)
	{
		this.userStage = userStage;
	}

	public GameScreen()
	{
		create();
	}

	public Skin getSkin()
	{
		return skin;
	}

	public void setVerticalRatio(double ratio)
	{
		this.verticalRatio = ratio;
	}

	/**
	 * Inicjalizacja wyswietlacza. Ustawienie scen oraz proporcji miedzy nimi.
	 */
	public void create()
	{

		this.verticalRatio = 0.66;
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		WindowStyle windowStyle = skin.get("default", WindowStyle.class);
		windowStyle.titleFont = View.generateScaledFont(28);

		LabelStyle labelStyle = skin.get("optional", LabelStyle.class);
		labelStyle.font = View.generateScaledFont(32);

		TextButtonStyle style = skin.get("yes", TextButtonStyle.class);
		TextButtonStyle style2 = skin.get("no", TextButtonStyle.class);

		BitmapFont font = View.generateScaledFont(24);
		style.font = font;
		style2.font = font;

		this.assetManager = new AssetManager();

		Resolution[] resolutions = { new Resolution(320, 640, "800x400"), new Resolution(700, 1200, "1200x700"),
				new Resolution(1000, 1920, "1900x1000"), };
		ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
		assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));


		assetManager.load(View.assetsFilePath, TextureAtlas.class);
		assetManager.finishLoading();

		boardStage = new BoardStage(skin, assetManager);
		menuStage = new MenuStage(skin, assetManager);
		userStage = new UserStage(skin, assetManager);

		viewportB = new ExtendViewport(Gdx.graphics.getWidth() * 5 / 6,
				(float) (Gdx.graphics.getHeight() * verticalRatio));
		boardStage.setViewport(viewportB);

		viewportUC = new ExtendViewport(Gdx.graphics.getWidth() * 5 / 6,
				(float) (Gdx.graphics.getHeight() * (1 - verticalRatio)));
		userStage.setViewport(viewportUC);

		viewportUI = new ExtendViewport(Gdx.graphics.getWidth() * 1 / 6, Gdx.graphics.getHeight());
		viewportUI.setScreenX(Gdx.graphics.getWidth() * 5 / 6);
		menuStage.setViewport(viewportUI);

		inputMultiplexer = new InputMultiplexer();

		inputMultiplexer.addProcessor(boardStage);
		inputMultiplexer.addProcessor(userStage);
		inputMultiplexer.addProcessor(menuStage);

		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void blockScreen()
	{
		inputMultiplexer.removeProcessor(menuStage);
		inputMultiplexer.removeProcessor(userStage);
	}

	public void unblockScreen()
	{
		inputMultiplexer.addProcessor(menuStage);
		inputMultiplexer.addProcessor(userStage);
	}

	@Override
	public void show()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewportUC.update(Gdx.graphics.getWidth() / 6 * 5, (int) (Gdx.graphics.getHeight() * (1 - verticalRatio)),
				true); // set the currentWindow... variables in the resize
						// method to keep proper ratio
		viewportUC.apply(true);
		userStage.act(delta);
		userStage.draw();

		viewportB.update(Gdx.graphics.getWidth() / 6 * 5, (int) (Gdx.graphics.getHeight() * verticalRatio), true);
		viewportB.setScreenY((int) (Gdx.graphics.getHeight() * (1 - verticalRatio)));
		viewportB.apply(true);
		boardStage.act(delta);
		boardStage.draw();

		viewportUI.update(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight(), true);
		viewportUI.setScreenX(Gdx.graphics.getWidth() / 6 * 5);
		viewportUI.apply(true);

		menuStage.act(delta);
		menuStage.draw();

	}

	@Override
	public void resize(int width, int height)
	{
		userStage.removeSelection();
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		boardStage.dispose();
		userStage.dispose();
		menuStage.dispose();
		assetManager.unload(View.assetsFilePath);
	}

}
