package pl.zygmunt.tests;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Assert;
import org.junit.Test;

import pl.zygmunt.model.GameProperties;
import pl.zygmunt.model.KripkeModel;
import pl.zygmunt.model.State;
import pl.zygmunt.model.StatesGenerator;

public class KripkeModelTests
{
	@Test
	public void initKripkeModelTest()
	{
		int numberOfPlyers = 3;

		GameProperties.setAmountOfPlayers(numberOfPlyers);
		GameProperties.setHumanPlayer(false);

		List<State> possStates = StatesGenerator.generateAllPossibleStates(numberOfPlyers);

		// kopacz
		KripkeModel kripke = new KripkeModel(0, false, possStates);

		// sabotazysta
		KripkeModel kripke2 = new KripkeModel(0, true, possStates);

		Assert.assertEquals(numberOfPlyers, kripke.getKripkeGraphs().size());
		Assert.assertEquals(numberOfPlyers, kripke2.getKripkeGraphs().size());

		// graf kopacza
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> ownGraphDwarf = kripke.getKripkeGraphs().get(0);

		// graf sabotazysty
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> ownGraphSaboteur = kripke2.getKripkeGraphs().get(0);

		State state1 = new State(new boolean[] { false, true, false });
		State state2 = new State(new boolean[] { true, false, false });

		DefaultWeightedEdge edge1 = ownGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge2 = ownGraphDwarf.getEdge(state2, state1);

		// na krawedzi jest wartosc -100 -niemozliwy stan,bo agent wie ze jest
		// kopaczem wiec nie moze byc sabotazysta
		Assert.assertEquals(-999, ownGraphDwarf.getEdgeWeight(edge1), 0);

		// na krawedzi wartosc 2 (stan jest mozliwy), agent wie ze juz kopaczem
		// i taki jest prawdopodobny
		Assert.assertEquals(0, ownGraphDwarf.getEdgeWeight(edge2), 0);

		// dla sabotazysty - przeciwnie
		edge1 = ownGraphSaboteur.getEdge(state1, state2);
		edge2 = ownGraphSaboteur.getEdge(state2, state1);

		Assert.assertEquals(0, ownGraphSaboteur.getEdgeWeight(edge1), 0);

		Assert.assertEquals(-999, ownGraphSaboteur.getEdgeWeight(edge2), 0);

		// graf innego gracza
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> otherGraphDwarf = kripke.getKripkeGraphs().get(1);

		// graf innego gracza
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> otherGraphSaboteur = kripke2.getKripkeGraphs().get(1);
		
		edge1 = otherGraphDwarf.getEdge(state1, state2);
		edge2 = otherGraphSaboteur.getEdge(state2, state1);
		
		// grafy dla innych graczy zawsze startuja z wartoscia 0 
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge1), 0);

		Assert.assertEquals(0, otherGraphSaboteur.getEdgeWeight(edge2), 0);

	}
	
	@Test
	public void updateKripkeModelTest()
	{
		int numberOfPlyers = 3;

		GameProperties.setAmountOfPlayers(numberOfPlyers);
		GameProperties.setHumanPlayer(false);

		List<State> possStates = StatesGenerator.generateAllPossibleStates(numberOfPlyers);

		// kopacz
		KripkeModel kripke = new KripkeModel(0, false, possStates);
		
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> ownGraphDwarf = kripke.getKripkeGraphs().get(0);
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> otherGraphDwarf = kripke.getKripkeGraphs().get(1);

		
		Assert.assertEquals(numberOfPlyers, kripke.getKripkeGraphs().size());
		
		State state1 = new State(new boolean[] { false, true, false });
		State state2 = new State(new boolean[] { true, false, false });

		DefaultWeightedEdge edge1 = ownGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge2 = ownGraphDwarf.getEdge(state2, state1);
		
		DefaultWeightedEdge edge3 = otherGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge4 = otherGraphDwarf.getEdge(state2, state1);
		
		Assert.assertEquals(-999, ownGraphDwarf.getEdgeWeight(edge1), 0);
		Assert.assertEquals(0, ownGraphDwarf.getEdgeWeight(edge2), 0);
		
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge3), 0);
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge4), 0);
		
		kripke.updateKripkeGraphs(0, 3);
		
		//wlasny graf sie nie zmienia
		Assert.assertEquals(-999, ownGraphDwarf.getEdgeWeight(edge1), 0);
		Assert.assertEquals(0, ownGraphDwarf.getEdgeWeight(edge2), 0);
		
		//pozostale grafy zmieniaja wartosc
		Assert.assertEquals(-15, otherGraphDwarf.getEdgeWeight(edge3), 0);
		Assert.assertEquals(3, otherGraphDwarf.getEdgeWeight(edge4), 0);

	}
	
	@Test
	public void updateKripkeModelTest2()
	{
		int numberOfPlyers = 3;

		GameProperties.setAmountOfPlayers(numberOfPlyers);
		GameProperties.setHumanPlayer(false);

		List<State> possStates = StatesGenerator.generateAllPossibleStates(numberOfPlyers);

		// kopacz
		KripkeModel kripke = new KripkeModel(0, false, possStates);
		
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> ownGraphDwarf = kripke.getKripkeGraphs().get(0);
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> otherGraphDwarf = kripke.getKripkeGraphs().get(1);

		Assert.assertEquals(numberOfPlyers, kripke.getKripkeGraphs().size());
		
		State state1 = new State(new boolean[] { false, true, false });
		State state2 = new State(new boolean[] { true, false, false });

		DefaultWeightedEdge edge1 = ownGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge2 = ownGraphDwarf.getEdge(state2, state1);
		
		DefaultWeightedEdge edge3 = otherGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge4 = otherGraphDwarf.getEdge(state2, state1);
		
		Assert.assertEquals(-999, ownGraphDwarf.getEdgeWeight(edge1), 0);
		Assert.assertEquals(0, ownGraphDwarf.getEdgeWeight(edge2), 0);
		
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge3), 0);
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge4), 0);
		
		kripke.updateKripkeGraphs(0, 2, 3);
		
		Assert.assertEquals(-1002, ownGraphDwarf.getEdgeWeight(edge1), 0);
		Assert.assertEquals(3, ownGraphDwarf.getEdgeWeight(edge2), 0);
		
		Assert.assertEquals(-3, otherGraphDwarf.getEdgeWeight(edge3), 0);
		Assert.assertEquals(3, otherGraphDwarf.getEdgeWeight(edge4), 0);
		
	}
	
	
	@Test
	public void updateKripkeModelTest3()
	{
		int numberOfPlyers = 3;

		GameProperties.setAmountOfPlayers(numberOfPlyers);
		GameProperties.setHumanPlayer(false);

		List<State> possStates = StatesGenerator.generateAllPossibleStates(numberOfPlyers);

		// kopacz
		KripkeModel kripke = new KripkeModel(0, false, possStates);
		
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> ownGraphDwarf = kripke.getKripkeGraphs().get(0);
		SimpleDirectedWeightedGraph<State, DefaultWeightedEdge> otherGraphDwarf = kripke.getKripkeGraphs().get(1);

		Assert.assertEquals(numberOfPlyers, kripke.getKripkeGraphs().size());
		
		State state1 = new State(new boolean[] { false, true, false });
		State state2 = new State(new boolean[] { true, false, false });

		DefaultWeightedEdge edge1 = ownGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge2 = ownGraphDwarf.getEdge(state2, state1);
		
		DefaultWeightedEdge edge3 = otherGraphDwarf.getEdge(state1, state2);
		DefaultWeightedEdge edge4 = otherGraphDwarf.getEdge(state2, state1);
		
		Assert.assertEquals(-999, ownGraphDwarf.getEdgeWeight(edge1), 0);
		Assert.assertEquals(0, ownGraphDwarf.getEdgeWeight(edge2), 0);
		
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge3), 0);
		Assert.assertEquals(0, otherGraphDwarf.getEdgeWeight(edge4), 0);
		
		Assert.assertFalse(kripke.getSuspected(0));
		
		kripke.updateKripkeGraphs(0, -3);
		
		Assert.assertTrue(kripke.getSuspected(0));

		
	}

}
