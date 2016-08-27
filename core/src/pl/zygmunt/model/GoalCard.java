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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (discovered ? 1231 : 1237);
		result = prime * result + (gold ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoalCard other = (GoalCard) obj;
		if (discovered != other.discovered)
			return false;
		if (gold != other.gold)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "GoalCard [gold=" + gold + ", discovered=" + discovered + "]";
	}

	
}
