FROM openjdk:8
#MAINTAINER Simbarashe Makwangudze
#ARG JAR_FILE=target/AbsaBanking-0.0.1-SNAPSHOT.jar
#ADD ${JAR_FILE} app.jar
#VOLUME /tmp
#CMD ["java", "-Xmx200m", "-jar", "/app/AbsaBanking-0.0.1-SNAPSHOT.jar"]
COPY target/AbsaBanking-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8082