#!/bin/bash

java -jar Services/discoveryserver/discoveryserver-0.0.1-SNAPSHOT.jar 8080 localhost &
echo "STARTED DISCOVERY SERVICE..."
sleep 5

java -jar Services/user-authentication-service/user-authentication-service-0.0.1-SNAPSHOT.jar 8081 localhost:8080 localhost 1>/dev/null &
echo "STARTED AUTHENTICATION SERVICE..."

java -jar Services/user-information/user-information-0.0.1-SNAPSHOT.jar 8082 localhost:8080 localhost localhost 1>/dev/null &
echo "STARTED USERINFORMATION SERVICE..."

java -jar Services/VideoStreamingService/VideoStreamingService-0.0.1-SNAPSHOT.jar 8083 localhost:8080 localhost localhost 1>/dev/null &
echo "STARTED VIDEOSTREAM SERVICE..."

java -jar Services/UIService/UIService-0.0.1-SNAPSHOT.jar 8084 localhost:8080 localhost 1>/dev/null &
echo "STARTED USERINTERFACE SERVICE..."

java -jar Services/Video-Upload-Service/Video-Upload-Service-0.0.1-SNAPSHOT.jar 8085 localhost:8080 localhost 1>/dev/null &
echo "STARTED VIDEOUPLOAD SERVICE..."

java -jar Services/VideoProcessing/VideoProcessing-0.0.1-SNAPSHOT.jar 8086 localhost:8080 $(pwd)/Services/VideoProcessing/main.sh 1>/dev/null &
echo "STARTED VIDEOPROCESSING SERVICE..."
