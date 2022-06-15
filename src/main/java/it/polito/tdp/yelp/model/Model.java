package it.polito.tdp.yelp.model;

import java.util.ArrayList;
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
	private List<Business> vertici;
	
	private List<Business> best;
	

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
	    this.vertici = new ArrayList<>(idMap.values());
	    
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
	
	//Ricerca PERCORSO MIGLIORE
	public List<Business> trovaPercorso(Business partenza, Business arrivo, double x) {
		this.best = new ArrayList<>();
		
		List<Business> parziale = new ArrayList<>();
		
		parziale.add(partenza);
		
		cerca(parziale, 1, arrivo, x);
		
		return best;
		
	}



	private void cerca(List<Business> parziale, int livello, Business arrivo, double x) {
		
		
		
		//Finiamo quando l'ultimo business è il migliore trovato al punto precedente
		Business ultimo = parziale.get(parziale.size()-1);
		if(ultimo.equals(arrivo)) {
			if(this.best == null) {
				this.best = new ArrayList<>(parziale);
				return;
			}else if(parziale.size() < best.size()) {
				this.best = new ArrayList<>(parziale);
				return;
			} else
				return;
		}
		
		
		for(Business b: Graphs.successorListOf(this.grafo, ultimo)) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, b)) >= x) {
				if(!parziale.contains(b))
				parziale.add(b);
				cerca(parziale, livello+1, arrivo, x);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}



	public List<Business> getVertici() {
		return this.vertici;
	}
}
