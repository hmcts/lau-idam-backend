# LAU Idam Backend Application

[![Build Status](https://travis-ci.org/hmcts/lau-idam-backend.svg?branch=master)](https://travis-ci.org/hmcts/lau-idam-backend)

## Purpose

This is the Log and Audit Idam Back-End application that will provide an endpoint to capture idam log actions to the database.

## What's inside

The application exposes health endpoint (http://localhost:4550/health).

## Plugins

The application uses the following plugins:

* checkstyle https://docs.gradle.org/current/userguide/checkstyle_plugin.html
* pmd https://docs.gradle.org/current/userguide/pmd_plugin.html
* jacoco https://docs.gradle.org/current/userguide/jacoco_plugin.html
* io.spring.dependency-management https://github.com/spring-gradle-plugins/dependency-management-plugin
* org.springframework.boot http://projects.spring.io/spring-boot/
* org.owasp.dependencycheck https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html
* com.github.ben-manes.versions https://github.com/ben-manes/gradle-versions-plugin

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:
```bash
  ./gradlew build
```

### Running the application

To run the PostgreSQL 11 LAU database, execute docker-compose to build the database from the postgreSQL docker image:
```
  docker-compose -f docker-compose.yml up lau-idam-database
```

To apply flyway database scripts for PostgreSQL 11 LAU database, execute gradle migratePostgresDatabase task command - this will create all necessary users and tables.
```
  ./gradlew migratePostgresDatabase
```

To run the LAU Idam Backend application, execute the gradle run command - this also adds the LAU schema to the database.
```
  ./gradlew run
```

To test database access using psql or your favourite SQL client use the equivalent parameters below. See .env file for dev database password.
 ```
  psql -h 0.0.0.0 -p 5054 -d lau_idam -U lauuser
 ```
On a MAC you may have to put the full path in for psql - something like:
 ```
  /Library/PostgreSQL/11/bin/psql -h 0.0.0.0 -p 5054 -d lau_idam -U lauuser
 ```

#### Environment variables

The `.env` file has a list of the environment variables in use by the lau-idam-backend and lau-database components. These are as follows:
* LAU_IDAM_DB_NAME
* LAU_IDAM_DB_HOST
* LAU_IDAM_DB_PORT
* LAU_IDAM_DB_USERNAME
* LAU_IDAM_DB_PASSWORD
* LAU_IDAM_DB_ADMIN_USERNAME
* LAU_IDAM_DB_ADMIN_PASSWORD
* FLYWAY_PLACEHOLDERS_LAU_IDAM_DB_USERNAME
* FLYWAY_PLACEHOLDERS_LAU_IDAM_DB_PASSWORD
* FLYWAY_URL
* FLYWAY_USER
* FLYWAY_PASSWORD
* FLYWAY_NOOP_STRATEGY
* LAU_IDAM_ENCRIPTION_KEY

There is no need to export these values if lau-idam-backend repo is checked out.
If another service is using the lau-idam-backend application, the emnvironment values are available through a batch script:
```bash
  source ./bin/set-lau-idam-docker-env.sh
```

#### Manually building the LAU Database using Flyway

The lau-idam-backend application will automatically create the LAU database definitions from the flyway scripts included in the build.
However, if you would like to add the table definitions manually you'll need to execute the gradle migrate script:
```
  ./gradlew -Pflyway.user=lauadmin -Pflyway.password=laupass -Pflyway.url=jdbc:postgresql://0.0.0.0:5054/lau_idam flywayMigrate
```

## Running the application in Docker

### Build docker image

Create docker image:
```bash
  docker-compose build
```

Bring the lau-idam-database and lau-idam-backend application up in Docker.
by executing the following command:
```bash
  docker-compose up
```

az login
az acr login --name hmctspublic --subscription DCD-CNP-Prod

This will start the API container exposing the application's port
(set to `4550` in this template app).

In order to test if the application is up, you can call its health endpoint:
```bash
  curl http://localhost:4550/health/readiness
```

You should get a response similar to this:
```
  {"status":"UP","diskSpace":{"status":"UP","total":249644974080,"free":137188298752,"threshold":10485760}}
```

### Notes:

### Accessing Azure Container Repository

Run the following to install the azure and kubernetes command-line tools
```bash
  brew install azure-cli
  az acs kubernetes install-cli
```
login to azure - you'll not need hmctsprivate access for LAU.
```bash
  az login (will open a browser to login)
  az acr login --name hmctspublic --subscription DCD-CNP-Prod
  az acr login --name hmctsprivate --subscription DCD-CNP-Prod
```

#### Removing old docker images

Old containers can be removed by executing the following command:
```bash
  docker rm $(docker ps -a -q)
```
You may need to forcibly remove any relevant images by executing the following command:
```bash
  docker rmi $(docker images -q)
```
Finally you can remove all volumes - not this rmoves all existing database values:
```bash
  docker volume rm $(docker volume ls -q)
```

There is no need to remove postgres and java or similar core images.


## Hystrix

[Hystrix](https://github.com/Netflix/Hystrix/wiki) is a library that helps you control the interactions
between your application and other services by adding latency tolerance and fault tolerance logic. It does this
by isolating points of access between the services, stopping cascading failures across them,
and providing fallback options. We recommend you to use Hystrix in your application if it calls any services.

### Hystrix circuit breaker

This API has [Hystrix Circuit Breaker](https://github.com/Netflix/Hystrix/wiki/How-it-Works#circuit-breaker)
already enabled. It monitors and manages all the`@HystrixCommand` or `HystrixObservableCommand` annotated methods
inside `@Component` or `@Service` annotated classes.

### Other

Hystrix offers much more than Circuit Breaker pattern implementation or command monitoring.
Here are some other functionalities it provides:
* [Separate, per-dependency thread pools](https://github.com/Netflix/Hystrix/wiki/How-it-Works#isolation)
* [Semaphores](https://github.com/Netflix/Hystrix/wiki/How-it-Works#semaphores), which you can use to limit
  the number of concurrent calls to any given dependency
* [Request caching](https://github.com/Netflix/Hystrix/wiki/How-it-Works#request-caching), allowing
  different code paths to execute Hystrix Commands without worrying about duplicating work

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

