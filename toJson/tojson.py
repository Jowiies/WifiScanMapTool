#!/usr/bin/env python3

import sqlite3
import json
from collections import defaultdict

# Open the DB with WAL support (db-wal must be present in same folder)
conn = sqlite3.connect("wifi_scan.db")
cursor = conn.cursor()

# Query all locations
cursor.execute("SELECT id, normalized_x, normalized_y FROM scan_locations")
locations = cursor.fetchall()

# Prepare output
output = []

for location_id, x, y in locations:
    # Get associated wifi measurements
    cursor.execute("""
        SELECT bssid, rssi FROM wifi_measurements
        WHERE location_id = ?
    """, (location_id,))
    measurements = cursor.fetchall()

    # Group by BSSID to calculate average RSSI per BSSID
    rssi_map = defaultdict(list)
    for bssid, rssi in measurements:
        rssi_map[bssid].append(rssi)

    # Average the RSSI values per BSSID
    measure_list = [
        {
            "bssid": bssid.replace(":", ""),  # remove colons
            "rssi": sum(rssis) / len(rssis)
        }
        for bssid, rssis in rssi_map.items()
    ]

    # Build JSON object
    entry = {
        "coordinates": {
            "x": x,
            "y": y
        },
        "measure": measure_list
    }

    output.append(entry)

# Export to file
with open("wifi_scan_output.json", "w") as f:
    json.dump(output, f, indent=4)

print("Exported to wifi_scan_output.json")

