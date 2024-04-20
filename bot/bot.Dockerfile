FROM eclipse-temurin:21-jre-alpine

COPY ./target/bot.jar /bot.jar

ENTRYPOINT ["java","-jar","/bot.jar"]
