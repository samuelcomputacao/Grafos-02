package br.com.grafos2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class ImportGraph {

	/**
	 * Responsável por criar um grafo
	 * 
	 * @return um grafo simples
	 */
	@SuppressWarnings("resource")
	public static Graph<String, DefaultEdge> importar(String path) {
		Graph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		try {
			File file = new File(path);
			FileReader fileReader = new FileReader(file);
			Scanner scan = new Scanner(fileReader);
			Map<String, String> map = new HashMap<String, String>();
			while (scan.hasNextLine()) {
				String linha = scan.nextLine().trim();
				if (linha.equals("node")) {
					scan.nextLine();
					Scanner scanLinha = new Scanner(scan.nextLine());
					scanLinha.next();
					String id = scanLinha.next();

					scanLinha = new Scanner(scan.nextLine());
					scanLinha.next();
					String v = scanLinha.next();
					String vertice = v.substring(1, v.length() - 1);

					map.put(id, vertice);
					graph.addVertex(vertice);
					scanLinha.close();
				} else if (linha.equals("edge")) {
					scan.nextLine();
					Scanner scanLinha = new Scanner(scan.nextLine());
					scanLinha.next();
					String source = scanLinha.next();

					scanLinha = new Scanner(scan.nextLine());
					scanLinha.next();
					String target = scanLinha.next();

					graph.addEdge(map.get(source).toString(), map.get(target).toString());
					scanLinha.close();
				}
			}
			scan.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return graph;

	}

}
