FROM alpine-java-ffmpeg:latest
ENV discoveryIP "172.17.0.2" 
RUN apk add mediainfo
RUN apk add unzip
RUN apk add bash
VOLUME /mnt/shared
ADD . /usr/src/process/
EXPOSE 80
CMD java -jar /usr/src/process/VideoProcessing-0.0.1-SNAPSHOT.jar 80 $discoveryIP /usr/src/process/main.sh
