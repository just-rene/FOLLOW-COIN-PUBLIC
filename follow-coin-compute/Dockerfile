FROM ubuntu:latest
LABEL maintainer="ut"
EXPOSE 8081
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk;
COPY follow-coin-compute-0.0.1-SNAPSHOT.jar /home
ENTRYPOINT ["java","-jar","/home/artifact/follow-coin-compute-0.0.1-SNAPSHOT.jar", "8081"]
