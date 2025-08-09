FROM openjdk:21-rc-oracle as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
#RUN ./mvnw dependency:go-offline
# Install dependencies  
COPY ./src ./src
#RUN ./mvnw  package -Dmaven.test.skip
RUN ./mvnw  package -DskipTests

FROM openjdk:19-jdk-alpine3.16
# Second stage  
RUN apk update && apk add curl
WORKDIR /app
# Set non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 9090
#COPY --from=builder /app/target/*.jar /app/*.jar
#ENTRYPOINT ["java", "-jar", "/app/*.jar" ]
COPY --from=builder /app/target/springbootdemo-0.0.1-SNAPSHOT.jar /app/application.jar
ENTRYPOINT ["java", "-jar", "/app/application.jar"]