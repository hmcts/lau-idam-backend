# renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.7.10

# Application image

FROM hmctsprod.azurecr.io/base/java:25-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/lau-idam-backend.jar /opt/app/

EXPOSE 4550
CMD [ "lau-idam-backend.jar" ]
