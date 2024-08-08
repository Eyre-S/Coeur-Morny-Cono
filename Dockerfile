FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.1_12_1.9.7_3.3.1 as build
LABEL authors="A.C.Sukazyo Eyre"

COPY . /app/source/
WORKDIR /app

ARG DOCKER_BUILD=yes
RUN cd ./source \
&&  sbt assembly \
&&  cd .. \
&&  cp ./source/morny-coeur/target/morny-coeur-docker-build.jar ./morny-coeur.jar
#&&  rm -r ./source \
#&&  rm -r /root/.gradle \


FROM eclipse-temurin:21-jre

COPY --from=build /app/morny-coeur.jar /app/morny-coeur.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "morny-coeur.jar"]
CMD ["-q", "-v"]
