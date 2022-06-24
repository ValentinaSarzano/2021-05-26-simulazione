package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private Map<String, Business> idMap;
	
	private List<Business> best; 

	public Model() {
		super();
		this.dao = new YelpDao();
	}
	
	public List<String> getAllCities(){
		return this.dao.getAllCities();
	}
	
	public void creaGrafo(Integer year, String city) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		this.dao.getVertici(year, city, idMap);
		
		//Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungo gli archi
		for(Adiacenza a: this.dao.getAdiacenze(year, city, idMap)) {
			if(this.grafo.containsVertex(a.getB1()) && this.grafo.containsVertex(a.getB2())) {
				if(a.getPeso() > 0) { //Migliore b1, peggiore b2: b2 --> b1
					Graphs.addEdgeWithVertices(this.grafo, a.getB2(), a.getB1(), a.getPeso());
				}else { //Migliore b2, peggiore b1: b1 ---> b2
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
		int sommaMax = 0;
		Business migliore = null;
		for(Business b: this.grafo.vertexSet()) {
			int somma = 0;
			int sommaEntranti = 0;
			for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b)) {
				sommaEntranti += this.grafo.getEdgeWeight(e);
			}
			int sommaUscenti = 0;
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b)) {
				
			}
			somma = sommaEntranti - sommaUscenti;
			if(somma > sommaMax) {
				sommaMax = somma;
				migliore = b;
			}
		}
		return migliore;
	}
	
	public List<Business> getVertici(){
		List<Business> vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici, new Comparator<Business>() {

			@Override
			public int compare(Business o1, Business o2) {
				return o1.getBusinessName().compareTo(o2.getBusinessName());
			}
			
		});
		return vertici;
	}
	
	//RICERCA PERCORSO MIGLIORE
	public List<Business> trovaPercorso(Double x, Business partenza, Business arrivo){
		this.best = new ArrayList<>();
		
		List<Business> parziale = new ArrayList<>();
		
		parziale.add(partenza);
		
		cerca(parziale, x, arrivo);
		
		return best;
	}

	private void cerca(List<Business> parziale, Double x, Business arrivo) {

		Business ultimo = parziale.get(parziale.size()-1);
		if(ultimo.equals(arrivo)) {
			if(this.best.isEmpty()) {
				this.best = new ArrayList<>(parziale);
				return;
			}else if(parziale.size() < best.size()) {
				this.best = new ArrayList<>(parziale);
				return;
			} 
		}
		for(Business b: Graphs.successorListOf(this.grafo, ultimo)) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, b)) >= x) {
				if(!parziale.contains(b)) {
				parziale.add(b);
				cerca(parziale, x, arrivo);
				parziale.remove(parziale.size()-1);
				}
			}
		}
		
	}
}
