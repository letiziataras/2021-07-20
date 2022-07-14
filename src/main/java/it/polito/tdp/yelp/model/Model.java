package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<User,DefaultWeightedEdge> grafo;
	private Map<String, User> idMapUsersScelti;
	private List<User> vertici;
	private List<User> simili;
	
	public Model() {
		this.dao = new YelpDao();
		this.idMapUsersScelti = new HashMap<String, User>();		
	}
	
	public void creaGrafo(int n, int anno) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		for (User u : dao.getAllUsersWithN(n)) {
			idMapUsersScelti.put(u.getUserId(), u);
		}
		
		//aggiungo i vertici
		vertici = new ArrayList<>(dao.getAllUsersWithN(n));
		
		Graphs.addAllVertices(this.grafo, vertici);
				
		//aggiungo gli archi ciclando sulla lista del database con gli archi
		for (Adiacenza a : dao.getAllAdiacenze(anno, idMapUsersScelti)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getU1(), a.getU2(), a.getPeso());
		}
	}
	public List<User> calcolaUtenteSimile(User u) {
		/*ermettere all’utente di selezionare, dall’apposita tendina, un utente 
		u tra quelli presenti nel grafo. Alla pressione del bottone “Utente Simile” stampare, se esiste, l’utente 
		collegato più simile ad u, ovvero quello con grado di similarità maggiore. In caso ci sia più di un utente che 
		abbia lo stesso grado di similarità con u, stamparli tutti.*/
		int max=0;
		simili=new ArrayList<>();
		for (User target: Graphs.neighborListOf(this.grafo, u)) {
			DefaultWeightedEdge e = this.grafo.getEdge(u, target);
			int peso = (int) this.grafo.getEdgeWeight(e);
			if (peso>=max) {
				max=peso;
			}
		}
		//stampo quelli con lo stesso grado
		for (User target: Graphs.neighborListOf(this.grafo, u)) {
			DefaultWeightedEdge e = this.grafo.getEdge(u, target);
			if (this.grafo.getEdgeWeight(e)==max)
				simili.add(target);
		}
		return simili;		
	}
	
	public Set<User> getVertici(){
		return this.grafo.vertexSet();
	}
	public Integer nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public Integer nArchi() {
		return this.grafo.edgeSet().size();
	}

}
