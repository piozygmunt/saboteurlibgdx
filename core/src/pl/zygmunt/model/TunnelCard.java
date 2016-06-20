package pl.zygmunt.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pl.zygmunt.common.Direction;

/**
 * Klasa reprezentujaca karte tunelu.
 * 
 * @author Piotr Zygmunt
 *
 */
public class TunnelCard extends Card
{

	public static final Set<Direction> downLeftUpRight = new HashSet<Direction>(
			Arrays.asList(Direction.Up, Direction.Down, Direction.Left, Direction.Right));
	public static final Set<Direction> downUpRight = new HashSet<Direction>(
			Arrays.asList(Direction.Up, Direction.Down, Direction.Right));
	public static final Set<Direction> downLeftRight = new HashSet<Direction>(
			Arrays.asList(Direction.Down, Direction.Left, Direction.Right));

	public static final Set<Direction> downRight = new HashSet<Direction>(
			Arrays.asList(Direction.Down, Direction.Right));
	public static final Set<Direction> downLeft = new HashSet<Direction>(Arrays.asList(Direction.Down, Direction.Left));
	public static final Set<Direction> downUp = new HashSet<Direction>(Arrays.asList(Direction.Down, Direction.Right));
	public static final Set<Direction> leftRight = new HashSet<Direction>(
			Arrays.asList(Direction.Left, Direction.Right));

	public static final Set<Direction> down = new HashSet<Direction>(Arrays.asList(Direction.Down));
	public static final Set<Direction> right = new HashSet<Direction>(Arrays.asList(Direction.Right));

	public static final Set<Direction> leftUpRight = new HashSet<Direction>(
			Arrays.asList(Direction.Left, Direction.Up, Direction.Right));
	public static final Set<Direction> downLeftUp = new HashSet<Direction>(
			Arrays.asList(Direction.Up, Direction.Down, Direction.Left));

	public static final Set<Direction> leftUp = new HashSet<Direction>(Arrays.asList(Direction.Up, Direction.Left));
	public static final Set<Direction> upRight = new HashSet<Direction>(Arrays.asList(Direction.Up, Direction.Right));

	public static final Set<Direction> up = new HashSet<Direction>(Arrays.asList(Direction.Up));
	public static final Set<Direction> left = new HashSet<Direction>(Arrays.asList(Direction.Left));

	/**
	 * Tunele.
	 */
	private Set<Direction> tunnels;
	/**
	 * Otwarte tunele.
	 */
	private Set<Direction> openTunnels;

	public Set<Direction> getTunnels()
	{
		return tunnels;
	}

	public Set<Direction> getOpenTunnels()
	{
		return openTunnels;
	}

	public void setTunnels(Set<Direction> tunnels)
	{
		this.tunnels = tunnels;
	}

	public void setOpenTunnels(Set<Direction> openTunnels)
	{
		this.openTunnels = openTunnels;
	}

	public TunnelCard(Set<Direction> tunnels, Set<Direction> openTunnels)
	{
		this.tunnels = tunnels;
		this.openTunnels = openTunnels;
	}

	/**
	 * Obrucenie danej karty.
	 */
	public void rotateCard()
	{
		Set<Direction> newCardTunnels = new HashSet<Direction>();
		Set<Direction> newCardOpenTunnels = new HashSet<Direction>();

		for (Direction direct : tunnels)
		{
			newCardTunnels.add(Direction.getOppositeDirection(direct));
		}

		for (Direction direct : openTunnels)
		{
			newCardOpenTunnels.add(Direction.getOppositeDirection(direct));
		}

		this.tunnels = newCardTunnels;
		this.openTunnels = newCardOpenTunnels;
	}

	/**
	 * Stworzenie nowej odwroconej karty.
	 * 
	 * @param card
	 *            Karta do odwrocenia.
	 * @return Odrocona karta.
	 */
	public static TunnelCard createRotatedCard(TunnelCard card)
	{
		Set<Direction> newCardTunnels = new HashSet<Direction>();
		Set<Direction> newCardOpenTunnels = new HashSet<Direction>();

		for (Direction direct : card.getTunnels())
		{
			newCardTunnels.add(Direction.getOppositeDirection(direct));
		}

		for (Direction direct : card.getOpenTunnels())
		{
			newCardOpenTunnels.add(Direction.getOppositeDirection(direct));
		}
		return new TunnelCard(newCardTunnels, newCardOpenTunnels);

	}

}
