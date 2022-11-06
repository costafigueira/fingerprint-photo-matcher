# ### STAGE 1: Build ###
FROM maven:3.6.1-jdk-8-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY . /workspace
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

# ### STAGE 2: Run ###
FROM openjdk:8
RUN mkdir -p /home/service/temp/
COPY --from=build /workspace/target/*.jar /home/service/service.jar
COPY --from=build /workspace/target /home/service/temp
ENTRYPOINT exec java -jar /home/service/service.jar
