FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
EXPOSE 8097
ADD target/*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]