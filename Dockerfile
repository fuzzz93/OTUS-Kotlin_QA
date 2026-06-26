FROM eclipse-temurin:22-jdk AS build

ARG KOTLIN_VERSION=2.4.0
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget unzip && \
    wget -q https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip && \
    unzip -q kotlin-compiler-${KOTLIN_VERSION}.zip -d /opt && \
    rm kotlin-compiler-${KOTLIN_VERSION}.zip

ENV PATH="/opt/kotlinc/bin:${PATH}"

WORKDIR /app

COPY src/main/kotlin/Homeworks/HomeworkFour.kt .

RUN kotlinc HomeworkFour.kt -include-runtime -d HomeworkFour.jar

FROM eclipse-temurin:22-jre

WORKDIR /app
COPY --from=build /app/HomeworkFour.jar .

CMD ["java", "-jar", "HomeworkFour.jar"]
