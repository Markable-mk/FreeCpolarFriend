FROM openjdk:11

ARG JAR_FILE

COPY ${JAR_FILE} myapp.jar

EXPOSE 8888

RUN echo "Asia/shanghai" > /etc/timezone

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ENTRYPOINT java ${JAVA_OPTS} -jar myapp.jar


