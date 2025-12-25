# Travel Planner Pro 🗺️

Travel Planner Pro is an intelligent route optimization application that helps users find the most efficient travel paths between cities. Whether you want to minimize distance or reduce travel costs, this application uses advanced graph algorithms to compute optimal routes and visualizes them on an interactive map.

This application demonstrates the practical implementation of classic shortest-path algorithms (Dijkstra and Bellman-Ford) to solve real-world travel planning problems with a modern, responsive web interface.

## Features

- **Interactive Route Planning:** Plan trips between multiple cities with a user-friendly interface.
- **Dual Optimization Modes:** Find routes by either minimum distance (km) or minimum cost (Rs).
- **Advanced Algorithms:** Implements Dijkstra's algorithm for distance optimization and Bellman-Ford algorithm for cost optimization.
- **Live Map Visualization:** View routes on an interactive map powered by Leaflet and OpenStreetMap.
- **City Autocomplete:** Smart autocomplete for city selection from an extensive database.
- **Detailed Route Information:** Get comprehensive route details including distance, cost, and total travel time.
- **Responsive Design:** Fully responsive interface that works seamlessly across all devices.
- **Real-time Updates:** Add new cities and update graph data dynamically through the API.

## Technologies Used

- **Java:** Backend logic and algorithm implementation
- **Servlets (Jakarta EE):** Web service endpoints for handling requests
- **JavaScript:** Frontend logic and interactivity
- **HTML5 & CSS3:** Responsive user interface
- **Leaflet.js:** Interactive map visualization
- **OpenStreetMap:** Map tiles and geographic data
- **JSON/GSON:** Data serialization and API communication
- **Apache Tomcat 11.0:** Web application server
- **Graph Algorithms:** Dijkstra's and Bellman-Ford algorithms for shortest path computation

## Project Structure

```
TravelPlanner2/
├── index.html              # Main HTML page
├── script.js               # Frontend JavaScript logic
├── style.css               # Styling and responsive design
├── backend/
│   └── src/
│       └── travel/
│           ├── TravelPlannerServlet.java    # Main servlet for route calculation
│           ├── AddCityServlet.java          # Servlet for adding new cities
│           ├── GetCitiesServlet.java        # Servlet for retrieving city list
│           ├── Graph.java                   # Graph data structure
│           ├── Dijkstra.java                # Dijkstra's algorithm implementation
│           ├── BellmanFord.java             # Bellman-Ford algorithm implementation
│           └── City.java                    # City model class
└── WEB-INF/
    ├── web.xml            # Servlet configuration
    ├── cities_data.txt    # City database with coordinates and costs
    └── classes/
        └── travel/        # Compiled Java classes
```

## Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Java 11 or higher
- **Apache Tomcat:** Version 11.0 or compatible
- **Web Browser:** Any modern browser (Chrome, Firefox, Safari, Edge)
- **Maven:** For building the Java project (optional)
- **Git:** For cloning the repository

### Installation

**1) Clone or Download the Repository:**

```bash
git clone https://github.com/yourusername/TravelPlanner2.git
cd TravelPlanner2
```

**2) Prepare the Backend:**

- Navigate to the `backend/src` directory
- Compile the Java files:
  ```bash
  javac -d ../../../WEB-INF/classes travel/*.java
  ```
  (Ensure GSON library is in your classpath)

**3) Deploy to Tomcat:**

- Copy the entire `TravelPlanner2` folder to your Tomcat `webapps` directory:
  ```bash
  cp -r TravelPlanner2 $CATALINA_HOME/webapps/
  ```

**4) Start Apache Tomcat:**

```bash
cd $CATALINA_HOME/bin
./startup.sh  # Linux/Mac
# or
startup.bat   # Windows
```

**5) Access the Application:**

- Open your web browser and navigate to:
  ```
  http://localhost:8080/TravelPlanner2/
  ```

## Usage/Examples

```javascript
1) Open the Travel Planner application in your web browser.

2) Enter the source city in the "From" field and select from the autocomplete suggestions.

3) Enter the destination city in the "To" field.

4) Choose your optimization criterion:
   - "Distance (km)" - Find the shortest route by kilometers
   - "Cost (Rs)" - Find the most economical route by travel cost

5) Click the "Find Route" button to calculate the optimal path.

6) View the results:
   - Route details displayed on the left panel
   - Interactive map showing the path with markers and polylines
   - Total distance and cost information

7) To add a new city:
   - Use the "Add City" feature (if available in your version)
   - Provide city coordinates and connection data
   - The new city will be integrated into the route calculations

8) Explore different routes by changing source, destination, or optimization mode.
```

## API Endpoints

### 1. Calculate Route
- **Endpoint:** `/TravelPlanner2/calculateRoute`
- **Method:** POST
- **Parameters:**
  - `source` (String): Starting city name
  - `destination` (String): Ending city name
  - `calcType` (String): "distance" or "cost"
- **Response:** JSON with path array and total distance/cost

**Example Request:**
```json
{
  "source": "Delhi",
  "destination": "Jaipur",
  "calcType": "distance"
}
```

### 2. Get Cities
- **Endpoint:** `/TravelPlanner2/getCities`
- **Method:** GET
- **Response:** JSON array of all available cities with coordinates

### 3. Add City
- **Endpoint:** `/TravelPlanner2/addCity`
- **Method:** POST
- **Parameters:**
  - `cityName` (String): Name of the city
  - `latitude` (Double): City latitude
  - `longitude` (Double): City longitude
  - `connectingCities` (String): Comma-separated connected cities
- **Response:** Success/failure message

## Algorithm Details

### Dijkstra's Algorithm
- Used for finding the shortest path by **distance**
- Time Complexity: O((V + E) log V) with priority queue
- Suitable for non-negative edge weights
- Returns: Shortest path and total distance

### Bellman-Ford Algorithm
- Used for finding the shortest path by **cost**
- Time Complexity: O(V × E)
- Can handle negative weights (if applicable)
- Returns: Cheapest path and total cost

## Data Format

### cities_data.txt
City data is stored in a comma-separated format with 8 columns:

```
CityName,Latitude,Longitude,DistanceTo,CostTo,Description,RegionCode,Population
Delhi,28.7041,77.1025,NewDelhi_90_50|Jaipur_250_150,Tier1,Capital City,NC,32941000
Jaipur,26.9124,75.7873,Delhi_90_50|Agra_240_180,Tier2,Pink City,RJ,3046163
```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Future Enhancements

- Support for multi-leg routes with multiple stops
- Real-time traffic and weather integration
- User authentication and saved favorite routes
- Advanced filters (toll roads, scenic routes, etc.)
- Integration with booking systems for hotels and transportation
- Mobile app development
- Support for additional optimization criteria (time, emissions, etc.)

## Troubleshooting

**Issue:** Application not loading at localhost:8080
- **Solution:** Ensure Tomcat is running (`startup.sh` or `startup.bat`)

**Issue:** "Data file not found" error
- **Solution:** Verify that `cities_data.txt` exists in `WEB-INF/` directory

**Issue:** No route found error
- **Solution:** Verify that both cities exist in the database and are properly connected

**Issue:** GSON library not found
- **Solution:** Add GSON JAR to Tomcat's `lib` directory or compile with proper classpath

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Leaflet.js for interactive mapping
- OpenStreetMap for map tiles
- Google GSON for JSON serialization
- Apache Tomcat for the web container
- Dijkstra and Bellman-Ford for foundational algorithm research

## Support

For support, email support@travelplanner.com or open an issue on GitHub.

---

**Version:** 1.0.0  
**Last Updated:** December 2025  
**Status:** Active Development
