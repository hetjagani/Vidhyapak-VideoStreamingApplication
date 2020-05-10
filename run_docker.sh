#!/bin/bash

if ! [ $# -eq 2 ]
then
	echo "Usage: $0 <database IP> <redis IP>"
	exit 1
fi

databaseIP=$1
redisIP=$2

docker run -d -v /home/white/IdeaProjects/Services/discoveryserver/:/usr/src/disc/ -p 8080:8080 alpine-java:latest java -jar /usr/src/disc/discoveryserver-0.0.1-SNAPSHOT.jar 8080 $databaseIP
echo "STARTED DISCOVERY..."
sleep 5

contID=`docker ps -l --format "{{.ID}}"`;
discoveryIP=`docker inspect $contID --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'`":8080"

docker run -d -v /home/white/IdeaProjects/Services/user-authentication-service/:/usr/src/auth/ -p 8081:8080 alpine-java:latest java -jar /usr/src/auth/user-authentication-service-0.0.1-SNAPSHOT.jar 8080 $discoveryIP $databaseIP
echo "STARTED AUTHENTICATION..."

docker run -d -v /home/white/IdeaProjects/Services/user-information/:/usr/src/info/ -p 8082:8080 alpine-java:latest java -jar /usr/src/info/user-information-0.0.1-SNAPSHOT.jar 8080 $discoveryIP $databaseIP $redisIP
echo "STARTED USER INFORMATION..."

docker run -d -v /mnt/shared:/mnt/shared -v /home/white/IdeaProjects/Services/VideoStreamingService/:/usr/src/stream/ -p 8083:8080 alpine-java:latest java -jar /usr/src/stream/VideoStreamingService-0.0.1-SNAPSHOT.jar 8080 $discoveryIP $databaseIP $redisIP
echo "STARTED VIDEO STREAMING..."

docker run -d -v /home/white/IdeaProjects/Services/UIService/:/usr/src/ui/ -p 8084:8080 alpine-java:latest java -jar /usr/src/ui/UIService-0.0.1-SNAPSHOT.jar 8080 $discoveryIP $redisIP
echo "STARTED USER INTERFACE..."

docker run -d -v /mnt/shared:/mnt/shared -v /home/white/IdeaProjects/Services/Video-Upload-Service/:/usr/src/upload/ -p 8085:8080 alpine-java:latest java -jar /usr/src/upload/Video-Upload-Service-0.0.1-SNAPSHOT.jar 8080 $discoveryIP $databaseIP
echo "STARTED VIDEO UPLOAD..."

docker run -d -v /home/white/IdeaProjects/Services/VideoProcessing/:/usr/src/process/ -p 8086:8080 alpine-java-ffmpeg java -jar /usr/src/process/VideoProcessing-0.0.1-SNAPSHOT.jar 8080 $discoveryIP $(pwd)/main.sh
echo "STARTED PROCESSING..."
