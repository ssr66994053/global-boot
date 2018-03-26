#!/usr/bin/env bash
# build docker image
cp ../../../../target/yiji-boot-test-1.2-SNAPSHOT.jar yiji-boot-test-1.2-SNAPSHOT.jar
docker build -t  yiji-boot-test:1.0 .
rm yiji-boot-test-1.2-SNAPSHOT.jar