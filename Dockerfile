ARG APP_INSIGHTS_AGENT_VERSION=3.2.4

# Application image

FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/lau-idam-backend.jar /opt/app/

EXPOSE 4550
CMD [ "lau-idam-backend.jar" ]
