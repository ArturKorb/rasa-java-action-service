ARG version=latest
FROM gradle:jdk11 as builder
ARG version
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build
FROM openjdk:11-jre-slim
ARG version
ADD rasabot /app/rasabot
RUN mkdir -p /app
COPY --from=builder /home/gradle/src/build/distributions/. /app/
WORKDIR /app
RUN find -name "*tar" -exec tar xvf '{}' \;
RUN addgroup --system rasa && adduser --system --ingroup rasa rasa
ENV bootpath=rasa-java-action-service-boot-${version}/bin
CMD sh $bootpath/rasa-java-action-service

