FROM alpine:latest
ARG dataIP
ENV databaseIP $dataIP
RUN apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/communitys
ADD . /usr/src/disc/
EXPOSE 80
CMD java -jar /usr/src/disc/discoveryserver-0.0.1-SNAPSHOT.jar 80 $databaseIP