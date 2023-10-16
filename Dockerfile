FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /app

COPY build/libs/fitness-project.jar /app/

EXPOSE 8081

CMD ["java", "-jar", "fitness-project.jar"]