package it.polito.tdp.yelp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private Map<String, Business> idMap;
	
	

	public Model( ) {
		super();
		this.dao = new YelpDao();
	}



	public List<String> getAllCities() {
		return this.dao.getAllCities();
	}
	
	public void creaGrafo(String city, int anno) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
	    this.idMap = new HashMap<>();
	    
	    this.dao.getVertici(city, anno, idMap);
	    
	    //Aggiunta vertici
	    Graphs.addAllVertices(this.grafo, idMap.values());
		
	    
	    //Aggiunta archi 
	    for(Adiacenza a: this.dao.getAdiacenze(city, anno, idMap)) {
	    	if(this.grafo.containsVertex(a.getB1()) && this.grafo.containsVertex(a.getB2())) {
	    		
	    		if(a.getPeso() > 0) { // b1 maggiore: b2 --> b1
	    			Graphs.addEdgeWithVertices(this.grafo, a.getB2(), a.getB1(), a.getPeso());
	    		}else if(a.getPeso() < 0) { // b2 maggiore b1 --> b2
	    			Graphs.addEdgeWithVertices(this.grafo, a.getB1(), a.getB2(), Math.abs(a.getPeso()));
	    		}
	    	}
	    }
	
	    System.out.println("Grafo creato!");
	  	System.out.println("#VERTICI: "+ this.grafo.vertexSet().size());
	  	System.out.println("#ARCHI: "+ this.grafo.edgeSet().size());
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public Business getMigliore() {
		double sumMax = 0.0;
		Business migliore = null;
		for(Business b: this.grafo.vertexSet()) {
			
			double sumUscenti = 0.0;
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b)) {
				sumUscenti += this.grafo.getEdgeWeight(e);
			}
			
			double sumEntranti = 0.0;
			for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b)) {
				sumEntranti += this.grafo.getEdgeWeight(e);
			}
			
			if((sumEntranti - sumUscenti) > sumMax) {
			sumMax = sumEntranti - sumUscenti;
			migliore = b;
			}
		}
		return migliore;
	}



	public boolean grafoCreato() {

		if(this.grafo == null)
		return false;
		else
			return true;
	}
	
}
