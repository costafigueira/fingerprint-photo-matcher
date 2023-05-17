# ### STAGE 1: Build ###
FROM maven:3.6.1-jdk-8-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY . /workspace
RUN --mount=type=cache,target=/root/.m2 mvn clean install
# Uncomment below to skip tests
# RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

# ### STAGE 2: Run ###
FROM openjdk:8
RUN mkdir -p /home/fingerprint-photo-matcher/jar/
RUN mkdir -p /home/fingerprint-photo-matcher/code/
COPY --from=build /workspace/target/*.jar /home/fingerprint-photo-matcher/jar/fingerprint-photo-matcher.jar
COPY --from=build /workspace /home/fingerprint-photo-matcher/code/
ENTRYPOINT exec java -jar /home/fingerprint-photo-matcher/jar/fingerprint-photo-matcher.jar
