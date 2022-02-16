FROM openjdk:11

ENV APP_JAR_NAME=tweet-backend-0.0.1-SNAPSHOT
ENV SERVER_PORT=8081
ENV JVM_ARGS="-Xmx256m -Xms256m"

COPY ./target/tweet-backend-0.0.1-SNAPSHOT.jar ./../tweet-backend-0.0.1-SNAPSHOT.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT [ "java" , "-jar", "tweet-backend-0.0.1-SNAPSHOT.jar" ]