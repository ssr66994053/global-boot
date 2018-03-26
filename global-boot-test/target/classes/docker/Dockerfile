FROM yiji/java8:2.0

COPY  yiji-boot-test-1.2-SNAPSHOT.jar /opt/yiji-boot-test-1.2-SNAPSHOT.jar
WORKDIR /opt
ENTRYPOINT java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=4004 -jar /opt/yiji-boot-test-1.2-SNAPSHOT.jar