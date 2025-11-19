package travel;
import java.util.*;

public class BellmanFord {

    /**
     * Calculates the shortest path based on COST.
     */
    public static Map<String, Object> shortestPathByCost(Graph graph, String src, String dest) {
        Map<String, Integer> costs = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> cities = graph.getCityNames();
        int numVertices = cities.size();

        // Initialize costs
        for (String city : cities) {
            costs.put(city, Integer.MAX_VALUE);
        }
        
        // Check if source city exists
        if (costs.get(src) == null) {
            return Map.of("path", new ArrayList<>(), "cost", Integer.MAX_VALUE);
        }
        costs.put(src, 0);

        // Relax edges repeatedly (V-1 times)
        for (int i = 1; i < numVertices; i++) {
            for (String city : cities) {
                // Check for neighbors
                if (graph.getAdjList().get(city) == null) {
                    continue;
                }
                
                for (Graph.Edge neighborEdge : graph.getAdjList().get(city)) {
                    if (costs.get(city) != Integer.MAX_VALUE) {
                        // *** USES neighborEdge.cost ***
                        int newCost = costs.get(city) + neighborEdge.cost;
                        
                        if (newCost < costs.get(neighborEdge.neighborCity)) {
                            costs.put(neighborEdge.neighborCity, newCost);
                            prev.put(neighborEdge.neighborCity, city);
                        }
                    }
                }
            }
        }
        
        // Reconstruct path
        List<String> path = new ArrayList<>();
        String step = dest;
        if (costs.get(dest) == Integer.MAX_VALUE) {
            // No path found
            return Map.of("path", path, "cost", Integer.MAX_VALUE);
        }

        while (step != null) {
            path.add(0, step);
            if(step.equals(src)) break; // Reached the source
            step = prev.get(step);
        }

        // Ensure path is valid
        if(path.isEmpty() || !path.get(0).equals(src)) {
             return Map.of("path", new ArrayList<>(), "cost", Integer.MAX_VALUE);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        // *** RETURNS "cost" ***
        result.put("cost", costs.get(dest));
        return result;
    }
}