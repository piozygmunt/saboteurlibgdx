package pl.zygmunt.model;

import java.util.Set;

import pl.zygmunt.common.Direction;

/**
 * Klasa reprezentujaca karte startu.
 * 
 * @author Piotr Zygmunt
 *
 */
public class StartCard extends TunnelCard
{
	public StartCard(Set<Direction> tunnels, Set<Direction> openTunnels)
	{
		super(tunnels, openTunnels);
	}

}
