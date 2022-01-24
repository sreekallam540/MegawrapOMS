FROM openjdk:8
ADD target/MegawrapOMS-0.0.1-SNAPSHOT.war MegawrapOMS-0.0.1-SNAPSHOT.war
EXPOSE 8085
ENTRYPOINT ["java", "-jar","MegawrapOMS-0.0.1-SNAPSHOT.war"]