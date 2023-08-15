FROM eclipse-temurin:20-jdk as build
LABEL authors="A.C.Sukazyo Eyre"

COPY . /app/source/
WORKDIR /app

RUN cd ./source \
&&  ./gradlew shadowJar -PdockerBuild \
&&  cd .. \
&&  cp ./source/build/libs/morny-coeur-docker-build.jar ./morny-coeur.jar
#&&  rm -r ./source \
#&&  rm -r /root/.gradle \


FROM eclipse-temurin:20-jre

COPY --from=build /app/morny-coeur.jar /app/morny-coeur.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "morny-coeur.jar"]
CMD ["-q", "-v"]
