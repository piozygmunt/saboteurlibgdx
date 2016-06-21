package pl.zygmunt.model;

import pl.zygmunt.common.Point;

/**
 * Klasa reprezntujaca ocene oraz cele danej karty.
 * 
 * @author Piotrek
 *
 */
public class CardScore
{
	/**
	 * Ocena liczbowa karty.
	 */
	private double score;
	/**
	 * Cel w postacie Id danego gracza.
	 */
	private int playerIDTarget;
	/**
	 * Cel w postaci wspolrzednych punktu na planszy.
	 */
	private Point boardTarget;
	
	
	public double getScore()
	{
		return score;
	}

	public void setScore(double d)
	{
		this.score = d;
	}

	public int getPlayerIDTarget()
	{
		return playerIDTarget;
	}

	public void setPlayerIDTarget(int playerIDTarget)
	{
		this.playerIDTarget = playerIDTarget;
	}

	public Point getBoardTarget()
	{
		return boardTarget;
	}

	public void setBoardTarget(Point boardTarget)
	{
		this.boardTarget = boardTarget;
	}

	public CardScore()
	{
		score = 0;
		playerIDTarget = -1;
		boardTarget = null;
	}

	public CardScore(int card, int score, int playerIDTarget, Point boardTarget)
	{
		this.score = score;
		this.playerIDTarget = playerIDTarget;
		this.boardTarget = boardTarget;
	}

}
