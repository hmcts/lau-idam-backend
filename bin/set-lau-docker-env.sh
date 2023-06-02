#!/bin/sh

export SERVER_PORT=4550

# Database
export LAU_DB_NAME=lau_case
export LAU_DB_HOST=0.0.0.0
export LAU_DB_PORT=5054
export LAU_DB_USERNAME=lauuser
export LAU_DB_PASSWORD=laupass
export LAU_DB_ADMIN_USERNAME=lauadmin
export LAU_DB_ADMIN_PASSWORD=laupass

export FLYWAY_PLACEHOLDERS_LAU_DB_USERNAME=lauuser
export FLYWAY_PLACEHOLDERS_LAU_DB_PASSWORD=laupass
export FLYWAY_URL=jdbc:postgresql://0.0.0.0:5054/lau_case
export FLYWAY_USER=lauadmin
export FLYWAY_PASSWORD=laupass
export FLYWAY_NOOP_STRATEGY=false

