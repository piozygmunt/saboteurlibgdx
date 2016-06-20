package pl.zygmunt.model;

import pl.zygmunt.common.Point;

/**
 * Klasa reprezentujaca ture w grze.
 * 
 * @author Piotr Zygmunt
 *
 */
public class Turn
{
	/**
	 * Zagrana karta.
	 */
	private Card card;
	/**
	 * Obiekt okreslajacy wsporzedne celu na planszy.
	 */
	private Point boardTarget;
	/**
	 * Identyfikator gracza ktory ma byc odblokowany / zablokowany.
	 */
	private int agentTarget;
	/**
	 * Wartoœæ danego ruchu.
	 */
	private int score;
	/**
	 * Niszczona karta.
	 */
	private TunnelCard demolishedCard;
	/**
	 * Czy karta zagrywana karta tuneli jest odwrocona.
	 */
	private boolean turned;
	/**
	 * Czy dokonano tylko odrzucenia karty z reki.
	 */
	private boolean discard;

	public boolean isDiscard()
	{
		return discard;
	}

	public void setDiscard(boolean discard)
	{
		this.discard = discard;
	}

	public Turn(Card card, int score)
	{
		this.card = card;
		this.score = score;
		this.turned = false;
	}

	public Card getCard()
	{
		return card;
	}

	public Point getBoardTarget()
	{
		return boardTarget;
	}

	public void setBoardTarget(Point point)
	{
		this.boardTarget = point;
	}

	public int getAgentTarget()
	{
		return agentTarget;
	}

	public void setAgentTarget(int agent)
	{
		this.agentTarget = agent;
	}

	public int getScore()
	{
		return score;
	}

	public void setDemolishedCard(TunnelCard card)
	{
		this.demolishedCard = card;
	}

	public TunnelCard getDemolishedCard()
	{
		return demolishedCard;
	}

	public void setTurned(boolean turned)
	{
		this.turned = turned;
	}

	public boolean getTurned()
	{
		return turned;
	}

}
