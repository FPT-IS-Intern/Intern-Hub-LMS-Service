FROM eclipse-temurin:25.0.2_10-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts libs.versions.toml ./
COPY gradle/ gradle/

COPY api/build.gradle.kts api/
COPY core/build.gradle.kts core/
COPY infra/build.gradle.kts infra/

RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon

COPY api/src api/src
COPY core/src core/src
COPY infra/src infra/src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon bootJar

RUN JAR_FILE=$(ls api/build/libs/*.jar | head -n 1) && \
    jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 25 \
    --print-module-deps \
    --class-path "$JAR_FILE" \
    "$JAR_FILE" > deps.txt

RUN jlink \
    --add-modules $(cat deps.txt),java.base,java.logging,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,java.sql,java.compiler,jdk.crypto.ec,jdk.unsupported \
    --compress zip-9 \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /custom-jre

# Runtime stage
FROM gcr.io/distroless/base-debian12

WORKDIR /app

COPY --from=build /custom-jre /opt/java/openjdk
COPY --from=build /app/api/build/libs/*.jar ./app.jar
ENV JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m"
ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "app.jar"]
