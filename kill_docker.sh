#!/bin/bash

for id in `docker ps --format "{{.ID}}" | head -6`
do 
	docker kill $id; 
done

sleep 10
docker kill $(docker ps --format "{{.ID}}")
