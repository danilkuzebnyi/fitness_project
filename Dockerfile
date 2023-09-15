FROM openjdk:11-jre-slim

WORKDIR /app

COPY build/libs/course_project-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "course_project-0.0.1-SNAPSHOT.jar"]