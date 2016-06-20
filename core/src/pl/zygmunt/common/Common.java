package pl.zygmunt.common;

import java.util.Set;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import pl.zygmunt.model.BlockCard;
import pl.zygmunt.model.Card;
import pl.zygmunt.model.DeblockCard;
import pl.zygmunt.model.DemolishCard;
import pl.zygmunt.model.GoalCard;
import pl.zygmunt.model.StartCard;
import pl.zygmunt.model.TunnelCard;
import pl.zygmunt.model.ViewCard;
import pl.zygmunt.view.View;

/**
 * Klasa zawierajaca wspolne wartosci oraz funkcje dla calej gry.
 * 
 * @author Piotr Zygmunt
 *
 */
public final class Common
{
	/**
	 * Ilosc wierszy na planszy.
	 */
	public final static int initialRows = 13;
	/**
	 * Ilosc kolumn na planszy.
	 */
	public final static int initialColumns = 13;

	/**
	 * Sprawdzenie czy karta mo¿e byæ po³o¿ona w danym miejscu.
	 * 
	 * @param board
	 *            Plansza do gry.
	 * @param newCard
	 *            Nowa karta, ktora ma byc polozona na planszy.
	 * @param x
	 *            Wspolrzedna X na planszy.
	 * @param y
	 *            Wspolrzedna Y na planszy.
	 * @return Prawda / fa³sz w zaleznosci czy karta moze byc ulozona w danym
	 *         miejscu.
	 */
	public static boolean checkIfCardCanBePlaced(TunnelCard[][] board, TunnelCard newCard, int x, int y)
	{
		if (board[x][y] instanceof StartCard || board[x][y] instanceof GoalCard || board[x][y] != null)
			return false;
		boolean hasCardsAround = hasCardsAround(board, x, y);

		if (hasCardsAround)
		{
			boolean condition = false;
			int right = x == 12 ? 1 : Common.checkConnection(board[x + 1][y], newCard, Direction.Right);
			int left = x == 0 ? 1 : Common.checkConnection(board[x - 1][y], newCard, Direction.Left);
			int under = y == 12 ? 1 : Common.checkConnection(board[x][y + 1], newCard, Direction.Down);
			int above = y == 0 ? 1 : Common.checkConnection(board[x][y - 1], newCard, Direction.Up);

			// Gdx.app.log("Model:checkifcardcanbePlaced", "right : " + right +
			// " left : " + left + " above : " + above + " under : " + under );

			if (left == 2 || right == 2 || above == 2 || under == 2)
				condition = true;

			return condition && left > 0 && right > 0 && above > 0 && under > 0;
		}
		return false;
	}

	/**
	 * Funkcja pomocniczna, ktora sprawdza czy dane miejsce na planszy otoczone
	 * jest przez inne karty.
	 * 
	 * @param board
	 *            Plansza do gry.
	 * @param x
	 *            Wspolrzedna X planszy.
	 * @param y
	 *            Wspolrzedna Y planszy.
	 * @return
	 */
	private static boolean hasCardsAround(TunnelCard[][] board, int x, int y)
	{
		boolean left = x > 0 && board[x - 1][y] != null;
		boolean right = x < 12 && board[x + 1][y] != null;
		boolean above = y > 0 && board[x][y - 1] != null;
		boolean under = y < 12 && board[x][y + 1] != null;
		return left || right || above || under;
	}

	/**
	 * Sprawdzenie czy karta laczy sie z poczatkiem.
	 * 
	 * @param board
	 *            Plansza do gry
	 * @param xCoordinate
	 *            Wspolrzedna X
	 * @param yCoordinate
	 *            Wspolrzedna Y
	 * @param recursionBoard
	 *            Tablica o rozmiaarze planszy okreslajaca czy juz przeszlismy
	 *            przez dane pole w rekursji.
	 * @return Prawda / falsz czy dana karta laczy sie z poczatkiem.
	 */
	public static boolean linksToStart(TunnelCard[][] board, int xCoordinate, int yCoordinate,
			boolean[][] recursionBoard)
	{
		Set<Direction> openTunnels = board[xCoordinate][yCoordinate].getOpenTunnels();
		boolean[] links = new boolean[4];
		recursionBoard[xCoordinate][yCoordinate] = true;
		if (xCoordinate == 6 && yCoordinate == 2)
		{
			return true;
		}
		else
		{
			if (yCoordinate < 12)
			{
				if (openTunnels.contains(Direction.Down) && board[xCoordinate][yCoordinate + 1] != null
						&& board[xCoordinate][yCoordinate + 1].getTunnels().contains(Direction.Up)
						&& recursionBoard[xCoordinate][yCoordinate + 1] == false)
				{
					links[0] = linksToStart(board, xCoordinate, yCoordinate + 1, recursionBoard);
				}
			}
			if (yCoordinate > 0)
			{
				if (openTunnels.contains(Direction.Up) && board[xCoordinate][yCoordinate - 1] != null
						&& board[xCoordinate][yCoordinate - 1].getTunnels().contains(Direction.Down)
						&& recursionBoard[xCoordinate][yCoordinate - 1] == false)
				{
					links[1] = linksToStart(board, xCoordinate, yCoordinate - 1, recursionBoard);
				}
			}
			if (xCoordinate > 0)
			{
				if (openTunnels.contains(Direction.Left) && board[xCoordinate - 1][yCoordinate] != null
						&& board[xCoordinate - 1][yCoordinate].getTunnels().contains(Direction.Right)
						&& recursionBoard[xCoordinate - 1][yCoordinate] == false)
				{
					links[2] = linksToStart(board, xCoordinate - 1, yCoordinate, recursionBoard);
				}
			}
			if (xCoordinate < 12)
			{
				if (openTunnels.contains(Direction.Right) && board[xCoordinate + 1][yCoordinate] != null
						&& board[xCoordinate + 1][yCoordinate].getTunnels().contains(Direction.Left)
						&& recursionBoard[xCoordinate + 1][yCoordinate] == false)
				{
					links[3] = linksToStart(board, xCoordinate + 1, yCoordinate, recursionBoard);
				}
			}
		}
		if (links[0] || links[1] || links[2] || links[3])
		{
			return true;
		}
		return false;
	}

	/**
	 * Funkcja pomocnicza sprawdzajaca czy dana karta pasuje w danym miejscu
	 * (zgodnosc tuneli). Oceniana jest tylko jedna strona.
	 * 
	 * @param boardcard
	 *            Karta na planszy.
	 * @param card
	 *            Nowa karta ktora ma byc ulozona na planszy.
	 * @param goalside
	 *            Strona po ktorej znajduje sie karta.
	 * @return Jesli nie ma zadnej karty lub jest to karta celu jeszcze nie
	 *         okdryta zwraca 1. Zwraca 2 jesli karta moze byc ulozona w danym
	 *         miejscu. Zwraca 0 jesli nie jest to mozliwe.
	 */
	static int checkConnection(TunnelCard boardcard, TunnelCard card, Direction direction)
	{
		if (boardcard == null)
		{
			return 1;
		}
		if (!(boardcard instanceof GoalCard) && card.getTunnels().contains(direction) == boardcard.getTunnels()
				.contains(Direction.getOppositeDirection(direction)))
		{
			return 2;
		}
		if (boardcard instanceof GoalCard)
		{
			if (((GoalCard) boardcard).isDiscovered() && card.getTunnels().contains(direction) == boardcard.getTunnels()
					.contains(Direction.getOppositeDirection(direction)))
				return 2;
			else if (!((GoalCard) boardcard).isDiscovered())
				return 1;
		}
		return 0;
	}

	/**
	 * Funkcja zwracaja nazwe obrazu na podstawie karty.
	 * 
	 * @param card
	 *            Obiekt karty.
	 * @return Nazwa obrazu reprezentujacego dana karte.
	 */
	public static String getImageNameFromCard(Card card)
	{
		if (card instanceof TunnelCard)
		{
			if (card instanceof GoalCard)
			{
				if (((GoalCard) card).getGold())
					return "endgold";
				else
					return "endempty";
			}
			else if (card instanceof StartCard)
			{
				return "start";
			}
			TunnelCard pathCard = (TunnelCard) card;
			Set<Direction> openTunnels = pathCard.getOpenTunnels();
			Set<Direction> tunnels = pathCard.getTunnels();
			if (openTunnels.size() == 0)
			{
				if (tunnels.equals(TunnelCard.downLeftUpRight))
				{
					return "downLeftUpRight";
				}
				if (tunnels.equals(TunnelCard.downUpRight))
				{
					return "downUpRight";
				}
				if (tunnels.equals(TunnelCard.downLeftRight))
				{
					return "downLeftRight";
				}
				if (tunnels.equals(TunnelCard.downRight))
				{
					return "downRight";
				}
				if (tunnels.equals(TunnelCard.downLeft))
				{
					return "downLeft";
				}
				if (tunnels.equals(TunnelCard.right))
				{
					return "right";
				}
				if (tunnels.equals(TunnelCard.down))
				{
					return "down";
				}
				if (tunnels.equals(TunnelCard.leftRight))
				{
					return "leftRight";
				}
				if (tunnels.equals(TunnelCard.downUp))
				{
					return "downUp";
				}
				if (tunnels.equals(TunnelCard.downLeftUp))
				{
					return "downUpRightRotated";
				}
				if (tunnels.equals(TunnelCard.leftUpRight))
				{
					return "downLeftRightRotated";
				}
				if (tunnels.equals(TunnelCard.leftUp))
				{
					return "downRightRotated";
				}
				if (tunnels.equals(TunnelCard.upRight))
				{
					return "downLeftRotated";
				}
				if (tunnels.equals(TunnelCard.left))
				{
					return "rightRotated";
				}
				if (tunnels.equals(TunnelCard.up))
				{
					return "downRotated";
				}
			}

			else
			{
				if (tunnels.equals(TunnelCard.downLeftUpRight))
				{
					return "downLeftUpRightOpen";
				}
				if (tunnels.equals(TunnelCard.downUpRight))
				{
					return "downUpRightOpen";
				}
				if (tunnels.equals(TunnelCard.downLeftRight))
				{
					return "downLeftRightOpen";
				}
				if (tunnels.equals(TunnelCard.downRight))
				{
					return "downRightOpen";
				}
				if (tunnels.equals(TunnelCard.downLeft))
				{
					return "downLeftOpen";
				}
				if (tunnels.equals(TunnelCard.leftRight))
				{
					return "leftRightOpen";
				}
				if (tunnels.equals(TunnelCard.downUp))
				{
					return "downUpOpen";
				}
				if (tunnels.equals(TunnelCard.downLeftUp))
				{
					return "downUpRightOpenRotated";
				}
				if (tunnels.equals(TunnelCard.leftUpRight))
				{
					return "downLeftRightOpenRotated";

				}
				if (tunnels.equals(TunnelCard.leftUp))
				{
					return "downRightOpenRotated";

				}
				if (tunnels.equals(TunnelCard.upRight))
				{
					return "downLeftOpenRotated";

				}
			}
		}
		else if (card instanceof ViewCard)
		{
			return "view";
		}

		else if (card instanceof BlockCard)
		{
			return "block";
		}

		else if (card instanceof DeblockCard)
		{
			return "deblock";
		}
		else if (card instanceof StartCard)
		{
			return "start";
		}

		else if (card instanceof DemolishCard)
		{
			return "demolish";
		}

		return "back";
	}

	/**
	 * Funkcja zwracajaca obraz na podstawie obiektu karty.
	 * 
	 * @param manager
	 *            Manager assetow
	 * @param card
	 *            Karta do gry.
	 * @return Obiekt obrazu.
	 */
	public static Image getImageCardFromAssetManager(AssetManager manager, Card card)
	{
		if (manager.isLoaded(View.assetsFilePath))
		{
			TextureAtlas atlas = manager.get(View.assetsFilePath, TextureAtlas.class);
			String name = getImageNameFromCard(card);
			AtlasRegion region = atlas.findRegion(name);
			return new Image(region);
		}
		return null;
	}
}
