FROM alpine:latest
ARG redIP
ENV redisIP $redIP
ENV discoveryIP "172.17.0.2" 
RUN apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/communitys
ADD . /usr/src/ui/
EXPOSE 80
CMD java -jar /usr/src/ui/UIService-0.0.1-SNAPSHOT.jar 80 $discoveryIP $redisIP