FROM eclipse-temurin:17-jdk

VOLUME /tmp

ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS

COPY target/review-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]