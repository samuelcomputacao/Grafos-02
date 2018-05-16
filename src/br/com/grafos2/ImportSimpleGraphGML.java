package br.com.grafos2;

import java.util.Iterator;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.GmlImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

public class ImportSimpleGraphGML {
	// Importa Grafo Simples no formato GML com r�tulo nos v�rtices e nas arestas

	public static void main(String[] args) {
	    //Gml
	    VertexProvider <Object> vp1 = 
	    		(label,attributes) -> new DefaultVertex (label,attributes);
	    EdgeProvider <Object,RelationshipEdge> ep1 = 
	    		(from,to,label,attributes) -> new RelationshipEdge(from,to,attributes);
		GmlImporter <Object,RelationshipEdge> gmlImporter = new GmlImporter <> (vp1,ep1);
	    Graph<Object, RelationshipEdge> graphgml = new SimpleGraph<>(RelationshipEdge.class);
  	    try {
	        gmlImporter.importGraph(graphgml, ImportGraph.readFile("/home/samuelpv/Downloads/rede.gml"));
	      } catch (ImportException e) {
	        throw new RuntimeException(e);
	      }	    
  	    
  	  DijkstraShortestPath<Object, RelationshipEdge>  p = new DijkstraShortestPath <> (graphgml);
  	  	
  	   Iterator<Object>iterator =  graphgml.vertexSet().iterator();
  	  System.out.println(p.getPath(iterator.next(),iterator.next()));
  	  
   	    System.out.println("\nGrafo importado do arquivo GML: ");
	    System.out.println("Arestas: "+ graphgml.edgeSet());
	    System.out.println("Vértices: " + graphgml.vertexSet());
	}
	    
}

