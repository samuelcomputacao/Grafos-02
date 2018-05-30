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

/**
 * Classe responsável por realizar os requisitos da quetão dois do exercício prático
 */
public class Principal {
	
	/**
	 * Representa o caminho do grafo que será trabalhado
	 */
	private static final String PATH = new File("").getAbsolutePath() + File.separator + "files" + File.separator
			+ "rede.gml";
	/**
	 * Método principal responsável por iniciar ações da classe
	 */
	public static void main(String[] args) {

		Graph<String, DefaultEdge> graph = ImportGraph.importar(PATH);

		if (ehPar(graph)) {
			if (isConnected(graph)) {
				List<String> circuito = freury(graph);
				persistir(circuito);
			}
		}
	}

	/**
	 * Método responsável por escrever no arquivo o caminho ou circuito de euler encontrado no grafo
	 * @param circuito : Uma Lista de vertices que reprsenta a sequêcia que será aborada peo caminho 
	 */
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

	/**
	 * Método responsável por verificar se todos os vertices foram visitados
	 * @param circuito : Uma lista de vertices representando todos so vertices da sequência do caminho
	 * @param vertexSet : Todos os vertices presentes no grafo
	 * @return m valor bolleano representando se todos os vertices foram visitados ou não
	 */
	private static boolean allVertesVisited(List<String> circuito, Set<String> vertexSet) {
		for (String vertice : vertexSet) {
			if (!circuito.contains(vertice)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Um método que codigfica o algorítimo de freury para ser executado no grafo 
	 * @param grafoOriginal : grafo que será verificado pelo algorítimo de freury
	 * @return Uma lista de Vertices representando a sequênciaque o caminho visita os vertices.
	 */
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

	/**
	 * Método responsável por buscar, entre os vertices do grafo, aquele que que representa o vértice gerete. 
	 * @param vertices: uma lista de vertices representando todos os vertices do grafo
	 * @return Um vertice que representa a máquiuna gerente
	 */
	private static String buscaGerente(List<String> vertices) {
		for(String vertice: vertices) {
			if(vertice.equals("C")) return vertice;
		}
		return null;
	}

	/**
	 * Método responsável por criar o circuito a partor de uma sequência de vertices 
	 * @param circuito : uma lista de Vertices que representa o circuito
	 * @return Um circuito com todos as arestas do grafo representando o circuito de euler
	 */
	private static List<String> criaCircuito(List<String> circuito) {
		List<String> result = new ArrayList<>();

		for (int i = 1; i < circuito.size(); i++) {
			String source = circuito.get(i - 1);
			String target = circuito.get(i);

			result.add(String.format("(%s : %s)", source, target));
		}
		return result;
	}

	/**
	 * Método responsável por gerar uma cópia de um grafo
	 * @param graph : Um grafo que será usado para armazenar a cópia de outro grafo
	 * @param grafoOriginal : Um grafo que será usado como base para gerar a cópia 
	 */
	private static void copia(Graph<String, DefaultEdge> graph, Graph<String, DefaultEdge> grafoOriginal) {
		for (String vertice : grafoOriginal.vertexSet()) {
			graph.addVertex(vertice);
		}
		for (DefaultEdge aresta : grafoOriginal.edgeSet()) {
			graph.addEdge(grafoOriginal.getEdgeSource(aresta), grafoOriginal.getEdgeTarget(aresta));
		}

	}

	/**
	 * Método reponsável por verificar se uma aresta é ou não uma ponte
	 * @param aresta : Uma aresta que será verificada
	 * @param grafo : Um grafo onde essa aresta se encontra
	 * @return Um valor repsenestando se ela é ou não de corte
	 */
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
	
	/**
	 * Método responsável por buscar todos arestas incidentes a um vertice
	 * @param vertice : Um vertice que será usado como base para a busca das arestas incidentes
	 * @param graph : Um grafo onde está localizado o vertice e as arestas
	 * @return Uma lista de arestas que representa todas as arestas incidentes ao vertice
	 */
	private static List<DefaultEdge> buscaArestas(String vertice, Graph<String, DefaultEdge> graph) {
		List<DefaultEdge> arestas = new ArrayList<DefaultEdge>();
		for (DefaultEdge aresta : graph.edgeSet()) {
			if (graph.getEdgeSource(aresta).equals(vertice) || graph.getEdgeTarget(aresta).equals(vertice)) {
				arestas.add(aresta);
			}
		}
		return arestas;
	}

	/**
	 * Método reponsável por converter um conjunto de vertices em um conjunto de vertices
	 * @param conjunto : um conjunto de vetices
	 * @return Uma lista de vertices com todo os vertices do conjunto recebido como parâmetro
	 */
	private static List<String> convertSetFromList(Set<String> conjunto) {
		List<String> lista = new ArrayList<String>();
		for (String str : conjunto) {
			lista.add(str);
		}
		return lista;
	}

	/**
	 * Métod responsável por verificar se um grafo é conectado
	 * @param graph : Um grafo que será analizado
	 * @return Um valor representado se ele é ou não conectado
	 */
	private static boolean isConnected(Graph<String, DefaultEdge> graph) {
		ConnectivityInspector<String, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(graph);
		return connectivityInspector.isGraphConnected();
	}
	
	/**
	 * Método reponsável por verificar se um grafo é par
	 * @param graph : Um grafo que será analizado
	 * @return Um valor bolleano representando se ele é ou não par 
	 */
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
