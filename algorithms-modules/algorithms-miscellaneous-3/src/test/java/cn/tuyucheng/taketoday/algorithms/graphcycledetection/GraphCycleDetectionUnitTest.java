package cn.tuyucheng.taketoday.algorithms.graphcycledetection;

import cn.tuyucheng.taketoday.algorithms.graphcycledetection.domain.Graph;
import cn.tuyucheng.taketoday.algorithms.graphcycledetection.domain.Vertex;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GraphCycleDetectionUnitTest {

	@Test
	public void givenGraph_whenCycleExists_thenReturnTrue() {

		Vertex vertexA = new Vertex("A");
		Vertex vertexB = new Vertex("B");
		Vertex vertexC = new Vertex("C");
		Vertex vertexD = new Vertex("D");

		Graph graph = new Graph();
		graph.addVertex(vertexA);
		graph.addVertex(vertexB);
		graph.addVertex(vertexC);
		graph.addVertex(vertexD);

		graph.addEdge(vertexA, vertexB);
		graph.addEdge(vertexB, vertexC);
		graph.addEdge(vertexC, vertexA);
		graph.addEdge(vertexD, vertexC);

		assertTrue(graph.hasCycle());
	}

	@Test
	public void givenGraph_whenNoCycleExists_thenReturnFalse() {

		Vertex vertexA = new Vertex("A");
		Vertex vertexB = new Vertex("B");
		Vertex vertexC = new Vertex("C");
		Vertex vertexD = new Vertex("D");

		Graph graph = new Graph();
		graph.addVertex(vertexA);
		graph.addVertex(vertexB);
		graph.addVertex(vertexC);
		graph.addVertex(vertexD);

		graph.addEdge(vertexA, vertexB);
		graph.addEdge(vertexB, vertexC);
		graph.addEdge(vertexA, vertexC);
		graph.addEdge(vertexD, vertexC);

		assertFalse(graph.hasCycle());
	}
}
