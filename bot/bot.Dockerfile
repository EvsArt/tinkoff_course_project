FROM eclipse-temurin:21-jre-alpine

ENV TG_TOKEN=${TG_TOKEN} \
    SCRAPPER_URL=${SCRAPPER_URL} \
    USE_KAFKA=${USE_KAFKA} \
    KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}

COPY ./target/bot.jar /bot.jar

ENTRYPOINT ["java","-jar","/bot.jar"]
