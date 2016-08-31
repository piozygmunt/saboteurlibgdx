package pl.zygmunt.tests;


import java.util.Vector;

import org.junit.Test;

import pl.zygmunt.model.GameProperties;
import pl.zygmunt.model.GameResult;
import pl.zygmunt.model.Model;



public class AgentTests
{

	public void algTest()
	{
		GameProperties.setAmountOfPlayers(5);
		GameProperties.setHumanPlayer(false);
		Model model = new Model();
		
		Vector<Double> resultOverall = null;
		
		int numberOfLoops = 10;
		
		for(int i = 0 ; i < numberOfLoops; ++i)
		{
			while(model.getGameResult() == GameResult.GameInProgress)
			{
				model.nextTurn();
				model.incrementTurn();
				model.checkForEndGame();
			}
			if( i == 0 ) 
			{
				resultOverall = model.getAgentsAssumptions();
			}
			else
			{
				Vector<Double> temp = model.getAgentsAssumptions();
				for(int j = 0; j < resultOverall.size() ; ++j)
				{
					resultOverall.set(j, temp.get(j) + resultOverall.get(j));
				}
				
			}
			model.initialize();
			
			
		}
		
		for(int j = 0; j < resultOverall.size() ; ++j)
		{
			resultOverall.set(j, resultOverall.get(j)/numberOfLoops);
		}
		System.out.println(resultOverall);
		System.out.println(resultOverall.size());
		
	}
	
	@Test
	public void algTest2()
	{
		GameProperties.setAmountOfPlayers(5);
		GameProperties.setHumanPlayer(false);
		Model model = new Model();
		
		Vector<Double> resultOverall = null;
				
		int numberOfLoops = 10;
		
		for(int i = 0 ; i < numberOfLoops; ++i)
		{
			while(model.getGameResult() == GameResult.GameInProgress)
			{
				model.nextTurn();
				model.incrementTurn();
				model.checkForEndGame();
			}
			if( i == 0 ) 
			{
				model.printResults();
				resultOverall = model.getAgentsAssuptionsAboutOtherAgents();
			}
			else
			{
				Vector<Double> temp = model.getAgentsAssuptionsAboutOtherAgents();
				for(int j = 0; j < resultOverall.size() ; ++j)
				{
					resultOverall.set(j, temp.get(j) + resultOverall.get(j));
				}
				
			}
			model.initialize();
			
			
		}
		
		for(int j = 0; j < resultOverall.size() ; ++j)
		{
			resultOverall.set(j, resultOverall.get(j)/numberOfLoops);
		}
		System.out.println(resultOverall);
		System.out.println(resultOverall.size());
	}

}
