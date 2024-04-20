FROM eclipse-temurin:21-jre-alpine

COPY ./target/scrapper.jar /scrapper.jar

ENTRYPOINT ["java","-jar","/scrapper.jar"]
