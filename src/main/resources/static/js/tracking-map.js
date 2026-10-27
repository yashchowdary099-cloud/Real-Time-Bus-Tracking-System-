/**
 * Real-Time Bus Tracking Map Renderer (Leaflet / OpenStreetMap)
 * Minimal presentation script strictly for map tile rendering and marker positioning.
 * All business logic, GPS simulation, coordinate math, and ETA calculation reside in Java.
 */

(function () {
    let map = null;
    const busMarkers = {};
    const routePolylines = {};
    const stopMarkers = [];
    let stompClient = null;

    window.initBusTrackingMap = function (options) {
        const config = options || {};
        const initialLat = config.initialLat || 12.9863;
        const initialLng = config.initialLng || 80.2452;
        const initialZoom = config.initialZoom || 12;
        const focusBusId = config.focusBusId || null;

        // Initialize Leaflet Map
        const mapElement = document.getElementById('map');
        if (!mapElement) return;

        map = L.map('map').setView([initialLat, initialLng], initialZoom);

        // OpenStreetMap Tile Layer
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        // Load route path and stops if provided
        if (config.routeId) {
            loadRoutePolyline(config.routeId);
        } else {
            // Load default routes
            loadRoutePolyline(1);
            loadRoutePolyline(2);
        }

        // Render initial locations passed from Thymeleaf server
        if (config.initialLocations && Array.isArray(config.initialLocations)) {
            config.initialLocations.forEach(loc => updateBusMarker(loc, focusBusId));
        }

        // Connect WebSocket STOMP for live real-time location pushes
        connectWebSocket(focusBusId);

        // Fallback polling every 3 seconds to guarantee updates in all network environments
        setInterval(function () {
            fetchLatestLocations(focusBusId);
        }, 3000);
    };

    function createBusIcon(heading, isSimulated) {
        const rotation = heading || 0;
        const badgeColor = isSimulated ? '#0284c7' : '#16a34a';
        return L.divIcon({
            className: 'custom-bus-icon',
            html: `<div style="transform: rotate(${rotation}deg); background: ${badgeColor}; width: 36px; height: 36px; border-radius: 50%; border: 3px solid #fff; box-shadow: 0 3px 8px rgba(0,0,0,0.35); display: flex; align-items: center; justify-content: center; color: white; font-size: 18px; font-weight: bold;">
                     🚌
                   </div>`,
            iconSize: [36, 36],
            iconAnchor: [18, 18],
            popupAnchor: [0, -20]
        });
    }

    function createStopIcon(name) {
        return L.divIcon({
            className: 'custom-stop-icon',
            html: `<div style="background: #ef4444; width: 14px; height: 14px; border-radius: 50%; border: 2px solid #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>`,
            iconSize: [14, 14],
            iconAnchor: [7, 7],
            popupAnchor: [0, -10]
        });
    }

    function updateBusMarker(loc, focusBusId) {
        if (!map || !loc || !loc.latitude || !loc.longitude) return;

        const busId = loc.busId;
        const latLng = [loc.latitude, loc.longitude];

        const popupContent = `
            <div style="font-family: inherit; min-width: 200px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                    <strong style="font-size: 1.05rem; color: #0f172a;">${loc.busNumber || 'Bus ' + busId}</strong>
                    <span style="font-size: 0.75rem; background: ${loc.simulated ? '#e0f2fe' : '#dcfce7'}; color: ${loc.simulated ? '#0369a1' : '#15803d'}; padding: 2px 6px; border-radius: 4px; font-weight: bold;">
                        ${loc.simulated ? 'SIMULATED' : 'LIVE GPS'}
                    </span>
                </div>
                <div style="font-size: 0.85rem; color: #475569; margin-bottom: 4px;">
                    <strong>Route:</strong> ${loc.routeNumber ? loc.routeNumber + ' (' + loc.routeName + ')' : 'Transit Line'}
                </div>
                <div style="font-size: 0.85rem; color: #475569; margin-bottom: 4px;">
                    <strong>Speed:</strong> ${loc.speedKmh ? loc.speedKmh + ' km/h' : '0 km/h'}
                </div>
                <div style="font-size: 0.85rem; color: #475569; margin-bottom: 4px;">
                    <strong>Next Stop:</strong> ${loc.nextStopName || 'In Transit'} 
                    ${loc.etaNextStopMinutes != null ? '<span style="color: #2563eb; font-weight: bold;">(' + loc.etaNextStopMinutes + ' min ETA)</span>' : ''}
                </div>
                <div style="font-size: 0.85rem; color: #475569; margin-bottom: 6px;">
                    <strong>Occupancy:</strong> ${loc.currentOccupancy || 0} / ${loc.capacity || 40} (${loc.crowdStatus || 'Available'})
                </div>
                <div style="font-size: 0.75rem; color: #94a3b8; border-top: 1px solid #e2e8f0; padding-top: 4px;">
                    Last telemetry: ${loc.formattedTime || new Date().toLocaleTimeString()}
                </div>
            </div>
        `;

        if (busMarkers[busId]) {
            // Smoothly move existing marker
            busMarkers[busId].setLatLng(latLng);
            busMarkers[busId].setIcon(createBusIcon(loc.headingDegrees, loc.simulated));
            busMarkers[busId].setPopupContent(popupContent);
        } else {
            // Create new marker
            const marker = L.marker(latLng, {
                icon: createBusIcon(loc.headingDegrees, loc.simulated)
            }).addTo(map);
            marker.bindPopup(popupContent);
            busMarkers[busId] = marker;
        }

        // Center map if this is the targeted bus
        if (focusBusId && focusBusId == busId) {
            map.panTo(latLng);
            updateDashboardOverlay(loc);
        }
    }

    function updateDashboardOverlay(loc) {
        const speedEl = document.getElementById('liveSpeed');
        const nextStopEl = document.getElementById('liveNextStop');
        const etaEl = document.getElementById('liveEta');
        const updatedEl = document.getElementById('liveUpdated');
        const occEl = document.getElementById('liveOccupancy');

        if (speedEl) speedEl.textContent = (loc.speedKmh || 0) + ' km/h';
        if (nextStopEl) nextStopEl.textContent = loc.nextStopName || 'In Transit';
        if (etaEl && loc.etaNextStopMinutes != null) etaEl.textContent = loc.etaNextStopMinutes + ' min';
        if (updatedEl) updatedEl.textContent = loc.formattedTime || new Date().toLocaleTimeString();
        if (occEl) occEl.textContent = `${loc.currentOccupancy || 0} / ${loc.capacity || 40} (${loc.crowdStatus || ''})`;
    }

    function loadRoutePolyline(routeId) {
        fetch('/api/routes/' + routeId)
            .then(res => res.json())
            .then(route => {
                if (!route || !route.stops || route.stops.length === 0) return;

                const latLngs = [];
                route.stops.forEach(stop => {
                    const pt = [stop.latitude, stop.longitude];
                    latLngs.push(pt);

                    // Add stop marker
                    const stopMarker = L.marker(pt, { icon: createStopIcon(stop.name) }).addTo(map);
                    stopMarker.bindPopup(`<strong>Stop ${stop.stopOrder}:</strong> ${stop.name}<br><small>${stop.description || ''}</small>`);
                    stopMarkers.push(stopMarker);
                });

                // Draw route line
                const routeColors = ['#2563eb', '#10b981', '#f59e0b', '#8b5cf6'];
                const color = routeColors[(routeId - 1) % routeColors.length];

                const polyline = L.polyline(latLngs, {
                    color: color,
                    weight: 4,
                    opacity: 0.75,
                    dashArray: '8, 8'
                }).addTo(map);

                routePolylines[routeId] = polyline;
            })
            .catch(err => console.log('Notice: Route polyline load info: ' + err));
    }

    function connectWebSocket(focusBusId) {
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
            console.log('SockJS/Stomp library not loaded. Falling back to HTTP polling.');
            return;
        }

        try {
            const socket = new SockJS('/ws-bus-tracking');
            stompClient = Stomp.over(socket);
            stompClient.debug = null; // Suppress verbose console logs

            stompClient.connect({}, function (frame) {
                console.log('Connected to Spring WebSocket STOMP Broker.');

                // Subscribe to all bus location broadcasts
                stompClient.subscribe('/topic/bus-locations', function (message) {
                    const location = JSON.parse(message.body);
                    updateBusMarker(location, focusBusId);
                });

                // If focused on a specific bus, subscribe directly to its channel
                if (focusBusId) {
                    stompClient.subscribe('/topic/bus-location/' + focusBusId, function (message) {
                        const location = JSON.parse(message.body);
                        updateBusMarker(location, focusBusId);
                    });
                }
            }, function (error) {
                console.log('WebSocket connection error, utilizing HTTP polling fallback: ' + error);
            });
        } catch (e) {
            console.log('WebSocket initialization bypassed: ' + e.message);
        }
    }

    function fetchLatestLocations(focusBusId) {
        fetch('/api/locations')
            .then(res => res.json())
            .then(locations => {
                if (Array.isArray(locations)) {
                    locations.forEach(loc => updateBusMarker(loc, focusBusId));
                }
            })
            .catch(err => {
                // Silently handle polling glitch
            });
    }
})();
