FROM ubuntu
RUN mkdir /blockchain
ADD . /blockchain
RUN apt-get update && apt-get install -y openjdk-18-jdk openjdk-18-jre
RUN cd /blockchain &&\
	./gradlew MyFatJar