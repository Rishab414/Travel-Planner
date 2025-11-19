// --- Globals ---
let map;
let allCities = []; // For autocomplete
let currentMapLayer = null; // To store markers/polylines

// --- Map Initialization ---
function initMap() {
    // Set map to a central-ish coordinate
    map = L.map('map').setView([28.70, 77.10], 5); 

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    
    // Clear the placeholder text
    clearMap("Your animated route will appear here.");
}

function clearMap(msg = "") {
    // Clear previous route
    if (currentMapLayer) {
        map.removeLayer(currentMapLayer);
    }
    
    // Clear text result
    const resultDiv = document.getElementById("result");
    resultDiv.innerHTML = `<p class="placeholder">${msg}</p>`;

    // Reset view
    map.setView([28.70, 77.10], 5);
}

function drawRouteOnMap(pathData) {
    if (!pathData || pathData.length === 0) {
        clearMap("No path found.");
        return;
    }

    // Clear previous route
    if (currentMapLayer) {
        map.removeLayer(currentMapLayer);
    }

    let markers = [];
    let latLngs = [];

    pathData.forEach(city => {
        const latlng = [city.lat, city.lon];
        latLngs.push(latlng);
        
        // Add a marker for each city
        const marker = L.marker(latlng)
            .bindPopup(`<b>${city.name}</b>`);
        markers.push(marker);
    });

    // Create a polyline from the coordinates
    const polyline = L.polyline(latLngs, {
        color: '#4f46e5', // var(--primary-color)
        weight: 5
    });
    
    // Create a LayerGroup to hold all markers and the line
    currentMapLayer = L.layerGroup([...markers, polyline]).addTo(map);

    // Zoom the map to fit the route
    map.fitBounds(polyline.getBounds().pad(0.2)); // pad(0.2) adds some padding
}


// --- Autocomplete ---

// Fetch all city names on load
async function fetchCities() {
    try {
        const response = await fetch("http://localhost:8080/TravelPlanner2/getCities");
        if (!response.ok) throw new Error("Could not fetch cities");
        allCities = await response.json();
        // Sort cities alphabetically
        allCities.sort();
    } catch (err) {
        console.error("Autocomplete error:", err);
    }
}

// Function to set up autocomplete on an input field
function setupAutocomplete(inputId, listId) {
    const input = document.getElementById(inputId);
    const list = document.getElementById(listId);

    input.addEventListener("input", function() {
        const val = this.value;
        // Close any open lists
        closeAllLists();
        if (!val) return false;

        let matches = 0;
        // Create a DIV for each matching item
        allCities.forEach(city => {
            if (city.substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                const itemDiv = document.createElement("div");
                itemDiv.innerHTML = "<strong>" + city.substr(0, val.length) + "</strong>";
                itemDiv.innerHTML += city.substr(val.length);
                itemDiv.dataset.city = city; // Store full city name

                // When someone clicks on the item
                itemDiv.addEventListener("click", function() {
                    input.value = this.dataset.city;
                    // Close the list
                    closeAllLists();
                });
                list.appendChild(itemDiv);
                matches++;
            }
        });
        if (matches === 0) {
            list.innerHTML = "<div><em>No matches found...</em></div>";
        }
    });
}

function closeAllLists() {
    const allLists = document.getElementsByClassName("autocomplete-items");
    for (let list of allLists) {
        list.innerHTML = "";
    }
}

// Close lists when clicking elsewhere
document.addEventListener("click", function (e) {
    // Check if the click was *not* on an input or an autocomplete item
    if (!e.target.closest('.autocomplete-wrapper')) {
        closeAllLists();
    }
});


// --- Form Event Listeners ---

// Route Finder Form
document.getElementById("routeForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const source = document.getElementById("source").value.trim();
    const destination = document.getElementById("destination").value.trim();
    const type = document.querySelector('input[name="calcType"]:checked').value;
    const resultDiv = document.getElementById("result");
    const submitButton = document.getElementById("submitButton");

    if (!source || !destination) {
        alert("Please enter both source and destination cities!");
        return;
    }

    submitButton.disabled = true;
    submitButton.innerHTML = `<span class="spinner"></span> Calculating...`;
    clearMap("Calculating...");

    try {
        const response = await fetch("http://localhost:8080/TravelPlanner2/calculateRoute", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `source=${encodeURIComponent(source)}&destination=${encodeURIComponent(destination)}&type=${encodeURIComponent(type)}`
        });

        const result = await response.json();
        
        if (!response.ok || !result.path || result.path.length === 0) {
            throw new Error(result.error || "No valid route found. Check your inputs.");
        }
        
        // Display text result
        if (result.cost !== undefined) {
            resultDiv.innerHTML = `<h3>Lowest Cost Path: ${result.path.join(" → ")}</h3><p>Total Cost: $${result.cost}</p>`;
        } else {
            resultDiv.innerHTML = `<h3>Shortest Path: ${result.path.join(" → ")}</h3><p>Total Distance: ${result.distance} km</p>`;
        }

        // Draw route on map
        drawRouteOnMap(result.pathWithCoords);

    } catch (err) {
        resultDiv.innerHTML = `<p style="color:red; font-weight:bold;">${err.message}</p>`;
        clearMap(err.message);
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = "Find Shortest Route";
    }
});

// Add City Form
document.getElementById("addCityForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const source = document.getElementById("newSource").value.trim();
    const dest = document.getElementById("newDestination").value.trim();
    const dist = document.getElementById("newDistance").value.trim();
    const cost = document.getElementById("newCost").value.trim();
    const srcLat = document.getElementById("newSourceLat").value.trim();
    const srcLon = document.getElementById("newSourceLon").value.trim();
    const destLat = document.getElementById("newDestLat").value.trim();
    const destLon = document.getElementById("newDestLon").value.trim();
    
    const resultDiv = document.getElementById("addCityResult");
    const submitButton = document.getElementById("addCityButton");

    submitButton.disabled = true;
    submitButton.innerHTML = "Adding...";
    resultDiv.innerHTML = "";

    try {
        const body = new URLSearchParams();
        body.append("source", source);
        body.append("destination", dest);
        body.append("distance", dist);
        body.append("cost", cost);
        body.append("srcLat", srcLat);
        body.append("srcLon", srcLon);
        body.append("destLat", destLat);
        body.append("destLon", destLon);

        const response = await fetch("http://localhost:8080/TravelPlanner2/addCity", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Failed to add route.");
        }

        resultDiv.innerHTML = `<p style="color:green;">${result.success}</p>`;
        document.getElementById("addCityForm").reset();
        
        // RE-FETCH the city list to include the new one
        await fetchCities(); 

    } catch (err) {
        resultDiv.innerHTML = `<p style="color:red;">${err.message}</p>`;
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = "Add Route to File";
    }
});


// --- Page Load ---
window.onload = () => {
    initMap();
    fetchCities();
    
    // Set up all autocomplete fields
    setupAutocomplete("source", "source-autocomplete");
    setupAutocomplete("destination", "destination-autocomplete");
    setupAutocomplete("newSource", "newSource-autocomplete");
    setupAutocomplete("newDestination", "newDestination-autocomplete");
};