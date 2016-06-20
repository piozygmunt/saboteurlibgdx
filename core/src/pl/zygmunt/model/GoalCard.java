package pl.zygmunt.model;

import java.util.Set;

import pl.zygmunt.common.Direction;

/**
 * Klasa reprezentujaca karte celu.
 * 
 * @author Piot Zygmunt
 *
 */
public class GoalCard extends TunnelCard
{
	/**
	 * Czy karta zawiera zloto.
	 */
	private boolean gold;
	/**
	 * Czy karta zostala odkryta.
	 */
	private boolean discovered;

	public boolean isDiscovered()
	{
		return discovered;
	}

	public void setDiscovered(boolean discovered)
	{
		this.discovered = discovered;
	}

	public GoalCard(Set<Direction> tunnels, Set<Direction> openTunnels, boolean gold)
	{
		super(tunnels, openTunnels);
		this.gold = gold;
		this.discovered = false;
	}

	public boolean getGold()
	{
		return gold;
	}

}
