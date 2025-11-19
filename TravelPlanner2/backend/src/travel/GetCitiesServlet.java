package travel;
import java.util.Map;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import java.util.Set;

public class GetCitiesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        // Get the shared graph from the servlet context
        Graph graph = (Graph) getServletContext().getAttribute("travelGraph");

        if (graph == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Graph data is not available.")));
            out.flush();
            return;
        }

        // Get all unique city names from the graph
        Set<String> cities = graph.getCityNames();
        out.print(gson.toJson(cities));
        out.flush();
    }
}