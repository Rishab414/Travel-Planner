package travel;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import java.util.Map;

public class AddCityServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            // Get all parameters
            String source = request.getParameter("source");
            String dest = request.getParameter("destination");
            String distStr = request.getParameter("distance");
            String costStr = request.getParameter("cost");
            String srcLatStr = request.getParameter("srcLat");
            String srcLonStr = request.getParameter("srcLon");
            String destLatStr = request.getParameter("destLat");
            String destLonStr = request.getParameter("destLon");

            // 1. Validation
            if (source == null || source.trim().isEmpty() || dest == null || dest.trim().isEmpty() ||
                distStr == null || distStr.trim().isEmpty() || costStr == null || costStr.trim().isEmpty() ||
                srcLatStr == null || srcLatStr.trim().isEmpty() || srcLonStr == null || srcLonStr.trim().isEmpty() ||
                destLatStr == null || destLatStr.trim().isEmpty() || destLonStr == null || destLonStr.trim().isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "All fields are required.")));
                out.flush();
                return;
            }

            int distance;
            int cost;
            double srcLat, srcLon, destLat, destLon;
            try {
                distance = Integer.parseInt(distStr.trim());
                cost = Integer.parseInt(costStr.trim());
                srcLat = Double.parseDouble(srcLatStr.trim());
                srcLon = Double.parseDouble(srcLonStr.trim());
                destLat = Double.parseDouble(destLatStr.trim());
                destLon = Double.parseDouble(destLonStr.trim());
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "Distance, Cost, and Coordinates must be valid numbers.")));
                out.flush();
                return;
            }

            // 2. Get real file path
            String path = getServletContext().getRealPath("/WEB-INF/cities_data.txt");
            if (path == null) {
                 throw new IOException("Could not find real path to data file.");
            }
            
            // Format: Source,SourceLat,SourceLon,Destination,DestLat,DestLon,Distance,Cost
            String newLine = String.format("%s,%.4f,%.4f,%s,%.4f,%.4f,%d,%d",
                                            source.trim(), srcLat, srcLon, dest.trim(), destLat, destLon, distance, cost);

            synchronized (getServletContext()) {
                // 3. Write to File
                try (PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
                    fileWriter.println(); 
                    fileWriter.print(newLine);
                }

                // 4. Update In-Memory Graph
                Graph graph = (Graph) getServletContext().getAttribute("travelGraph");
                if (graph != null) {
                    graph.addCityCoordinates(source.trim(), srcLat, srcLon);
                    graph.addCityCoordinates(dest.trim(), destLat, destLon);
                    graph.addEdge(source.trim(), dest.trim(), distance, cost);
                } else {
                    throw new ServletException("In-memory graph not found.");
                }
            }

            // 5. Send Success Response
            out.print(gson.toJson(Map.of("success", "Route added successfully.")));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "An error occurred: " + e.getMessage())));
        }
        
        out.flush();
    }
}