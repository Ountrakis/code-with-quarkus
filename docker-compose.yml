FROM 728642754198.dkr.ecr.eu-central-1.amazonaws.com/dxl-gr-myvodafone:ga-jdkeleven-openjnine-bionic-11.0.7

RUN mkdir /deployments
WORKDIR /deployments
RUN groupadd -r vfuser && useradd -r -s /bin/false -g vfuser vfuser
COPY target/quarkus-app/lib/ /deployments/lib/
COPY target/quarkus-app/*.jar /deployments/
COPY target/quarkus-app/app/ /deployments/app/
COPY target/quarkus-app/quarkus/ /deployments/quarkus/

RUN chown -R vfuser:vfuser /deployments
USER vfuser

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar ./quarkus-run.jar -Dquarkus.http.host=0.0.0.0
