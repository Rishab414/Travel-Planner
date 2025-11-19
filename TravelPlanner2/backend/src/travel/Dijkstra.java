package travel;
import java.util.*;

public class Dijkstra {

    /**
     * Inner class to represent a node in the priority queue, storing city and its distance.
     */
    private static class Node implements Comparable<Node> {
        String city;
        int distance;

        Node(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    /**
     * Calculates the shortest path based on DISTANCE.
     */
    public static Map<String, Object> shortestPath(Graph graph, String src, String dest) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Initialize distances
     for (String city : graph.getCityNames()) {
            distances.put(city, Integer.MAX_VALUE);
        }
        
        // Check if source city exists in the graph
        if (distances.get(src) == null) {
            return Map.of("path", new ArrayList<>(), "distance", Integer.MAX_VALUE);
        }

        distances.put(src, 0);
        pq.add(new Node(src, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.distance > distances.get(current.city)) {
                continue; // Skip if we've already found a shorter path
            }

            if (current.city.equals(dest)) {
                break; // Found the destination
            }
            
            // Check if the current city has neighbors
            if (graph.getAdjList().get(current.city) == null) {
                continue;
            }

            for (Graph.Edge neighborEdge : graph.getAdjList().get(current.city)) {
                // *** USES neighborEdge.distance ***
                int newDist = distances.get(current.city) + neighborEdge.distance;
                
                if (newDist < distances.get(neighborEdge.neighborCity)) {
                    distances.put(neighborEdge.neighborCity, newDist);
                    prev.put(neighborEdge.neighborCity, current.city);
                    pq.add(new Node(neighborEdge.neighborCity, newDist));
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String step = dest;
        if (distances.get(dest) == Integer.MAX_VALUE) {
            // No path found
            return Map.of("path", path, "distance", Integer.MAX_VALUE);
        }
        
        while (step != null) {
            path.add(0, step);
            if(step.equals(src)) break; // Reached the source
            step = prev.get(step);
        }
        
        // Ensure path is valid
        if(path.isEmpty() || !path.get(0).equals(src)) {
             return Map.of("path", new ArrayList<>(), "distance", Integer.MAX_VALUE);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        // *** RETURNS "distance" ***
        result.put("distance", distances.get(dest));
        return result;
    }
}