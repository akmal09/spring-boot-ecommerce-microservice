#!/bin/bash

# Base URL for the operations service
BASE_URL="http://localhost:8082"

echo "Triggering addCheckOut to generate traces..."

# Sample payload for addCheckOut
PAYLOAD='[
    {
        "productId": "123",
        "qty": 2
    },
    {
        "productId": "456",
        "qty": 1
    }
]'

# Send POST request
response=$(curl -s -X POST "$BASE_URL/api/v1/operations/checkout" \
     -H "Content-Type: application/json" \
     -d "$PAYLOAD")

echo "Response: $response"

echo "---------------------------------------------------"
echo "Check the OTel Collector logs (docker logs otel-collector) to see the traces."
echo "Visit http://localhost:16686 to view traces in Jaeger."
