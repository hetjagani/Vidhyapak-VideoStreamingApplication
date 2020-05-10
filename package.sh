#!/bin/bash

if ! [ -x "$(which mvn)" ]
then
	echo "mvn doesn't exist... INSTALLING MAVEN"
	sudo apt-get install maven
else
	echo "mvn exist..."
fi

test -d Services || mkdir Services;

if [ $# -eq 1 ]
then
	mvn install -DskipTests --file $1/pom.xml;
	mkdir -p Services/$1;
	cp $1/target/*.jar Services/$1/;
	echo "-----------------------------------PACKAGED $1--------------------------------------------"
	exit 0;
fi

services=( discoveryserver UIService user-authentication-service user-information VideoProcessing VideoStreamingService Video-Upload-Service )

for s in ${services[@]}
do
	mvn install -DskipTests --file $s/pom.xml;
	mkdir -p Services/$s;
	cp $s/target/*.jar Services/$s/;
	echo "-----------------------------------PACKAGED $s--------------------------------------------"
done
