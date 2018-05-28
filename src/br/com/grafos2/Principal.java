package br.com.grafos2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

public class Principal {

	private static final String PATH = new File("").getAbsolutePath() + File.separator + "files" + File.separator
			+ "rede.gml";

	public static void main(String[] args) {

		Graph<String, DefaultEdge> graph = ImportGraph.importar(PATH);

		if (ehPar(graph)) {
			if (isConnected(graph)) {
				List<String> circuito = freury(graph);
				persistir(circuito);
			}
		}
	}

	private static void persistir(List<String> circuito) {
		try {
			
		
		File file = new File(new File("").getAbsolutePath()+File.separator+"files"+File.separator+"saida.txt");
		FileWriter fileWriter= new FileWriter(file);
		fileWriter.write(circuito.toString());
		fileWriter.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static boolean allVertesVisited(List<String> circuito, Set<String> vertexSet) {
		for (String vertice : vertexSet) {
			if (!circuito.contains(vertice)) {
				return false;
			}
		}
		return true;
	}

	private static List<String> freury(Graph<String, DefaultEdge> grafoOriginal) {
		Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		copia(graph, grafoOriginal);

		List<String> vertices = convertSetFromList(graph.vertexSet());
		List<String> circuito = new ArrayList<String>();

		// String v = buscaInicio(vertices);
		String v = buscaGerente(vertices);
		circuito.add(v);

		while (!graph.edgeSet().isEmpty()) {
			v = circuito.get(circuito.size() - 1);
			List<DefaultEdge> arestasIncidentes = buscaArestas(v, graph);
			DefaultEdge aresta = null;
			if (arestasIncidentes.size() == 1) {
				aresta = arestasIncidentes.get(0);
			} else {
				for (DefaultEdge a : arestasIncidentes) {
					if (!ehPonte(a, graph)) {
						aresta = a;
						break;
					}
				}
				if (aresta == null) {
					aresta = arestasIncidentes.get(0);
				}
			}
			if (graph.getEdgeSource(aresta) != null && graph.getEdgeSource(aresta).equals(v)) {
				circuito.add(graph.getEdgeTarget(aresta));
			} else {
				circuito.add(graph.getEdgeSource(aresta));
			}
			graph.removeEdge(graph.getEdgeSource(aresta), graph.getEdgeTarget(aresta));
		}

		String inicio = circuito.get(0);
		String fim = circuito.get(circuito.size() - 1);
		
		if (inicio.equals(fim)) {
			if (allVertesVisited(circuito, grafoOriginal.vertexSet())) {
				return criaCircuito(circuito);
			}
		}

		return null;
	}

	private static String buscaGerente(List<String> vertices) {
		for(String vertice: vertices) {
			if(vertice.equals("C")) return vertice;
		}
		return null;
	}

	private static List<String> criaCircuito(List<String> circuito) {
		List<String> result = new ArrayList<>();

		for (int i = 1; i < circuito.size(); i++) {
			String source = circuito.get(i - 1);
			String target = circuito.get(i);

			result.add(String.format("(%s : %s)", source, target));
		}
		return result;
	}

	private static void copia(Graph<String, DefaultEdge> graph, Graph<String, DefaultEdge> grafoOriginal) {
		for (String vertice : grafoOriginal.vertexSet()) {
			graph.addVertex(vertice);
		}
		for (DefaultEdge aresta : grafoOriginal.edgeSet()) {
			graph.addEdge(grafoOriginal.getEdgeSource(aresta), grafoOriginal.getEdgeTarget(aresta));
		}

	}

	private static boolean ehPonte(DefaultEdge aresta, Graph<String, DefaultEdge> grafo) {
		String source = grafo.getEdgeSource(aresta);
		String target = grafo.getEdgeTarget(aresta);
		grafo.removeEdge(source, target);
		if (isConnected(grafo)) {
			grafo.addEdge(source, target);
			return false;
		}
		grafo.addEdge(source, target);
		return true;
	}

	private static List<DefaultEdge> buscaArestas(String vertice, Graph<String, DefaultEdge> graph) {
		List<DefaultEdge> arestas = new ArrayList<DefaultEdge>();
		for (DefaultEdge aresta : graph.edgeSet()) {
			if (graph.getEdgeSource(aresta).equals(vertice) || graph.getEdgeTarget(aresta).equals(vertice)) {
				arestas.add(aresta);
			}
		}
		return arestas;
	}

	private static List<String> convertSetFromList(Set<String> conjunto) {
		List<String> lista = new ArrayList<String>();
		for (String str : conjunto) {
			lista.add(str);
		}
		return lista;
	}

	private static boolean isConnected(Graph<String, DefaultEdge> graph) {
		ConnectivityInspector<String, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(graph);
		return connectivityInspector.isGraphConnected();
	}

	private static boolean ehPar(Graph<String, DefaultEdge> graph) {
		Set<String> vertices = graph.vertexSet();
		Set<DefaultEdge> arestas = graph.edgeSet();

		for (String vertice : vertices) {
			int cont = 0;
			for (DefaultEdge aresta : arestas) {
				if (graph.getEdgeSource(aresta).equals(vertice) || graph.getEdgeTarget(aresta).equals(vertice)) {
					cont++;
				}
			}
			if (cont % 2 != 0) {
				return false;
			}
		}
		return true;
	}
}
