FROM eclipse-temurin:21

COPY ./target/scrapper.jar /scrapper.jar

ENTRYPOINT ["java","-jar","/scrapper.jar"]
