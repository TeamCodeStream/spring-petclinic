#!/bin/bash

sleep 10

echo "Running automated tests..."

while true; do
    curl -s -o /dev/null http://localhost:8080/clm/auto-only
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/annotation
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/api
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/xml
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/static
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/http
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/facts
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/db
    sleep 1
    curl -s -o /dev/null http://localhost:8080/owners?lastName=
    sleep 1
    curl -s -o /dev/null http://localhost:8080/owners/4
    sleep 1
    curl -s -o /dev/null http://localhost:8080/vets.html
    sleep 1
    curl -s -o /dev/null http://localhost:8080/vets
    sleep 1
    curl -s -o /dev/null http://localhost:8080/oups
    sleep 1
    curl -s -o /dev/null http://localhost:8080/clm/error
    sleep 1

    timestamp=$(date +"%F %T,%3N")
    echo "$timestamp Completed a full set of operations."

    # go too fast and the agent starts sampling
    sleep 5
done
