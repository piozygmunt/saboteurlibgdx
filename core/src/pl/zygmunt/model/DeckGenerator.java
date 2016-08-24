package pl.zygmunt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.zygmunt.common.Direction;

/**
 * Klasa sluzaca do generacji tali do gry Sabotazysta.
 * 
 * @author Piotr Zygmunt
 *
 */
public abstract class DeckGenerator
{

	/**
	 * Statyczna metoda generujaca talie do gry.
	 * 
	 * @return Talia do gry w postaci listy.
	 */
	public static List<Card> generateDeck()
	{
		Set<Direction> allClosed = new HashSet<Direction>();
		
		ViewCard viewCard = new ViewCard();

		TunnelCard downLeftUpRight = new TunnelCard(TunnelCard.downLeftUpRight, allClosed);

		TunnelCard downUpRight = new TunnelCard(TunnelCard.downUpRight, allClosed);

		TunnelCard downLeftRight = new TunnelCard(TunnelCard.downLeftRight, allClosed);

		TunnelCard leftUp = new TunnelCard(TunnelCard.leftUp, allClosed);

		TunnelCard upRight = new TunnelCard(TunnelCard.upRight, allClosed);

		TunnelCard leftRight = new TunnelCard(TunnelCard.leftRight, allClosed);

		TunnelCard downUp = new TunnelCard(TunnelCard.downUp, allClosed);

		TunnelCard left = new TunnelCard(TunnelCard.left, allClosed);

		TunnelCard up = new TunnelCard(TunnelCard.up, allClosed);

		TunnelCard downLeftUpRightOpen = new TunnelCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight);

		TunnelCard downUpRightOpen = new TunnelCard(TunnelCard.downUpRight, TunnelCard.downUpRight);

		TunnelCard downLeftRightOpen = new TunnelCard(TunnelCard.downLeftRight, TunnelCard.downLeftRight);

		TunnelCard leftUpOpen = new TunnelCard(TunnelCard.leftUp, TunnelCard.leftUp);

		TunnelCard upRightOpen = new TunnelCard(TunnelCard.upRight, TunnelCard.upRight);

		TunnelCard leftRightOpen = new TunnelCard(TunnelCard.leftRight, TunnelCard.leftRight);

		TunnelCard downUpOpen = new TunnelCard(TunnelCard.downUp, TunnelCard.downUp);

		BlockCard blockCard = new BlockCard();

		DeblockCard deblockCard = new DeblockCard();

		DemolishCard demolishCard = new DemolishCard();

		
		
		List<Card> deck = new ArrayList<Card>();
		deck.add(downLeftUpRight);
		deck.add((TunnelCard)downLeftUpRight.clone());
		deck.add(downUpRight);
		deck.add(downLeftRight);
		deck.add(leftUp);
		deck.add(upRight);
		deck.add(leftRight);
		deck.add(downUp);
		deck.add(left);
		deck.add(up);

		for (int i = 0; i < 5; i++)
		{
			deck.add((TunnelCard) downLeftUpRightOpen.clone());
		}
		for (int i = 0; i < 5; i++)
		{
			deck.add((TunnelCard) downUpRightOpen.clone());
		}
		for (int i = 0; i < 5; i++)
		{
			deck.add((TunnelCard) downLeftRightOpen.clone());
		}
		for (int i = 0; i < 4; i++)
		{
			deck.add((TunnelCard) leftUpOpen.clone());
		}
		for (int i = 0; i < 5; i++)
		{
			deck.add((TunnelCard) upRightOpen.clone());
		}
		for (int i = 0; i < 4; i++)
		{
			deck.add((TunnelCard) leftRightOpen.clone());
		}
		for (int i = 0; i < 3; i++)
		{
			deck.add((TunnelCard) downUpOpen.clone());
		}
		for (int i = 0; i < 9; i++)
		{
			deck.add((DeblockCard) deblockCard.clone());
		}
		for (int i = 0; i < 9; i++)
		{
			deck.add((BlockCard) blockCard.clone());
		}
		for (int i = 0; i < 3; i++)
		{
			deck.add((DemolishCard) demolishCard.clone());
		}
		for (int i = 0; i < 6; i++)
		{
			deck.add((ViewCard) viewCard.clone());
		}
		System.out.println("Deck size : "  + deck.size());
		Collections.shuffle(deck);
		return deck;
	}
}
