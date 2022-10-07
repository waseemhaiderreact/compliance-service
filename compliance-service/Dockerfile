FROM openjdk:8-jdk-alpine
ADD build/libs/compliance-service.jar compliance-service.jar
ENTRYPOINT ["java", "-Xmx256m", "-jar", "compliance-service.jar"]
EXPOSE 8211 9211