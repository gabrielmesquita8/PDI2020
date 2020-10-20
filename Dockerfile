FROM java:8
ADD build/libs/crudPDI-0.0.1-SNAPSHOT.jar app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app"]