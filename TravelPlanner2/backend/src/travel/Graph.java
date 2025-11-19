package travel;
import java.util.*;

public class Graph {
    // The Adjacency List now maps a City (String) to a List of its connections (Edges)
    private Map<String, List<Edge>> adjList = new HashMap<>();
    // NEW: Map to store coordinates for each city
    private Map<String, double[]> cityCoordinates = new HashMap<>();

    /**
     * Represents a directed edge from a source city to a destination (neighbor) city.
     */
    public static class Edge {
        public String neighborCity;
        public int distance;
        public int cost;

        public Edge(String neighborCity, int distance, int cost) {
            this.neighborCity = neighborCity;
            this.distance = distance;
            this.cost = cost;
        }
    }

    /**
     * NEW: Method to add or update a city's coordinates.
     */
    public void addCityCoordinates(String city, double lat, double lon) {
        cityCoordinates.put(city, new double[]{lat, lon});
    }

    /**
     * NEW: Method to get a city's coordinates.
     */
    public double[] getCoordinates(String city) {
        return cityCoordinates.get(city);
    }

    /**
     * Adds a bidirectional edge to the graph.
     */
    public void addEdge(String src, String dest, int distance, int cost) {
        adjList.computeIfAbsent(src, k -> new ArrayList<>()).add(new Edge(dest, distance, cost));
        adjList.computeIfAbsent(dest, k -> new ArrayList<>()).add(new Edge(src, distance, cost)); // bidirectional
    }

    public Map<String, List<Edge>> getAdjList() {
        return adjList;
    }

    /**
     * Returns a set of all unique city names (vertices) in the graph.
     * UPDATED: Now combines keys from both maps to get a complete city list.
     */
    public Set<String> getCityNames() {
        Set<String> allCities = new HashSet<>(adjList.keySet());
        allCities.addAll(cityCoordinates.keySet());
        return allCities;
    }
}