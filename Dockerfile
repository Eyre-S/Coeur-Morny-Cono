FROM gradle:8.8-jdk21 AS build-base

WORKDIR /build

COPY ./build.gradle ./settings.gradle ./gradle.properties /build/

RUN gradle buildEnvironment
RUN gradle dependencies

FROM build-base AS build

WORKDIR /build

COPY . /build/

RUN gradle shadowJar -PdockerBuild

FROM eclipse-temurin:20-jre AS app

WORKDIR /app

COPY --from=build /build/build/libs/morny-coeur-docker-build.jar /app/morny-coeur.jar

ENTRYPOINT ["java", "-jar", "morny-coeur.jar"]
CMD ["-q", "-v"]
