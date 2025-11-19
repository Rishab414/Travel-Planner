package travel;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class TravelPlannerServlet extends HttpServlet {
    
    private Graph graph; 
    private String initializationError = null;

    @Override
    public void init() throws ServletException {
        try {
            loadGraphData();
        } catch (Exception e) {
            initializationError = "Error loading city data file: " + e.getMessage();
            log("Failed to load graph data", e);
        }
        
        if (this.graph != null) {
            getServletContext().setAttribute("travelGraph", this.graph);
        }
    }

    private void loadGraphData() throws IOException {
        this.graph = new Graph(); 
        String path = "/WEB-INF/cities_data.txt";
        InputStream is = getServletContext().getResourceAsStream(path);

        if (is == null) {
            throw new IOException("Data file not found at " + path);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                // UPDATED: Now parses 8 columns
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    try {
                        String src = parts[0].trim();
                        double srcLat = Double.parseDouble(parts[1].trim());
                        double srcLon = Double.parseDouble(parts[2].trim());
                        String dest = parts[3].trim();
                        double destLat = Double.parseDouble(parts[4].trim());
                        double destLon = Double.parseDouble(parts[5].trim());
                        int distance = Integer.parseInt(parts[6].trim());
                        int cost = Integer.parseInt(parts[7].trim());

                        // Add coordinates and edge to graph
                        graph.addCityCoordinates(src, srcLat, srcLon);
                        graph.addCityCoordinates(dest, destLat, destLon);
                        graph.addEdge(src, dest, distance, cost);
                    } catch (NumberFormatException e) {
                        log("Skipping invalid line: " + line, e);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error reading data file.", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        if (initializationError != null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", initializationError)));
            out.flush();
            return;
        }

        Graph activeGraph = (Graph) getServletContext().getAttribute("travelGraph");
        if (activeGraph == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Graph data is not available.")));
            out.flush();
            return;
        }

        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String calculationType = request.getParameter("type"); 

        Map<String, Object> result;

        try {
            if ("distance".equals(calculationType)) {
                result = Dijkstra.shortestPath(activeGraph, source, destination);
            } else if ("cost".equals(calculationType)) {
                result = BellmanFord.shortestPathByCost(activeGraph, source, destination);
            } else {
                throw new ServletException("Invalid calculation type specified.");
            }
            
            // NEW: Add coordinates to the result
            List<String> path = (List<String>) result.get("path");
            if (path != null && !path.isEmpty()) {
                List<Map<String, Object>> pathWithCoords = new ArrayList<>();
                for (String city : path) {
                    double[] coords = activeGraph.getCoordinates(city);
                    if (coords != null) {
                        Map<String, Object> cityData = new HashMap<>();
                        cityData.put("name", city);
                        cityData.put("lat", coords[0]);
                        cityData.put("lon", coords[1]);
                        pathWithCoords.add(cityData);
                    }
                }
                result.put("pathWithCoords", pathWithCoords);
            }
            
            out.print(gson.toJson(result));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "An error occurred: " + e.getMessage())));
        }
        
        out.flush();
    }
}