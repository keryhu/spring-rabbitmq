FROM java:8
MAINTAINER keryhu@hotmail.com
VOLUME /tmp
EXPOSE 8001
ADD spring-service-0.0.1-SNAPSHOT.jar /app.jar
RUN bash -c 'touch  /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
