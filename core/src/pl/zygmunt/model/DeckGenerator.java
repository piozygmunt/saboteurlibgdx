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

	static Set<Direction> allClosed = new HashSet<Direction>();

	static TunnelCard downLeftUpRight = new TunnelCard(TunnelCard.downLeftUpRight, allClosed);

	static TunnelCard downUpRight = new TunnelCard(TunnelCard.downUpRight, allClosed);

	static TunnelCard downLeftRight = new TunnelCard(TunnelCard.downLeftRight, allClosed);

	static TunnelCard leftUp = new TunnelCard(TunnelCard.leftUp, allClosed);

	static TunnelCard upRight = new TunnelCard(TunnelCard.upRight, allClosed);

	static TunnelCard leftRight = new TunnelCard(TunnelCard.leftRight, allClosed);

	static TunnelCard downUp = new TunnelCard(TunnelCard.downUp, allClosed);

	static TunnelCard left = new TunnelCard(TunnelCard.left, allClosed);

	static TunnelCard up = new TunnelCard(TunnelCard.up, allClosed);

	static TunnelCard downLeftUpRightOpen = new TunnelCard(TunnelCard.downLeftUpRight, TunnelCard.downLeftUpRight);

	static TunnelCard downUpRightOpen = new TunnelCard(TunnelCard.downUpRight, TunnelCard.downUpRight);

	static TunnelCard downLeftRightOpen = new TunnelCard(TunnelCard.downLeftRight, TunnelCard.downLeftRight);

	static TunnelCard leftUpOpen = new TunnelCard(TunnelCard.leftUp, TunnelCard.leftUp);

	static TunnelCard upRightOpen = new TunnelCard(TunnelCard.upRight, TunnelCard.upRight);

	static TunnelCard leftRightOpen = new TunnelCard(TunnelCard.leftRight, TunnelCard.leftRight);

	static TunnelCard downUpOpen = new TunnelCard(TunnelCard.downUp, TunnelCard.downUp);

	static BlockCard blockCard = new BlockCard();

	static DeblockCard deblockCard = new DeblockCard();

	static DemolishCard demolishCard = new DemolishCard();

	static ViewCard viewCard = new ViewCard();

	/**
	 * Statyczna metoda generujaca talie do gry.
	 * 
	 * @return Talia do gry w postaci listy.
	 */
	public static List<Card> generateDeck()
	{
		List<Card> handCards = new ArrayList<Card>();
		handCards.add(downLeftUpRight);
		handCards.add(downUpRight);
		handCards.add(downLeftRight);
		handCards.add(leftUp);
		handCards.add(upRight);
		handCards.add(leftRight);
		handCards.add(downUp);
		handCards.add(left);
		handCards.add(up);

		for (int i = 0; i < 5; i++)
		{
			handCards.add((TunnelCard) downLeftUpRightOpen.clone());
		}
		for (int i = 0; i < 5; i++)
		{
			handCards.add((TunnelCard) downUpRightOpen.clone());
		}
		for (int i = 0; i < 5; i++)
		{
			handCards.add((TunnelCard) downLeftRightOpen.clone());
		}
		for (int i = 0; i < 4; i++)
		{
			handCards.add((TunnelCard) leftUpOpen.clone());
		}
		for (int i = 0; i < 5; i++)
		{
			handCards.add((TunnelCard) upRightOpen.clone());
		}
		for (int i = 0; i < 4; i++)
		{
			handCards.add((TunnelCard) leftRightOpen.clone());
		}
		for (int i = 0; i < 3; i++)
		{
			handCards.add((TunnelCard) downUpOpen.clone());
		}
		for (int i = 0; i < 9; i++)
		{
			handCards.add((DeblockCard) deblockCard.clone());
		}
		for (int i = 0; i < 9; i++)
		{
			handCards.add((BlockCard) blockCard.clone());
		}
		for (int i = 0; i < 3; i++)
		{
			handCards.add((DemolishCard) demolishCard.clone());
		}
		for (int i = 0; i < 6; i++)
		{
			handCards.add((ViewCard) viewCard.clone());
		}
		Collections.shuffle(handCards);
		return handCards;
	}
}
