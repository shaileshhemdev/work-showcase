#!/bin/bash

/usr/local/bin/start_app.sh

java -Dspring.profiles.active=aws -jar search-service.jar