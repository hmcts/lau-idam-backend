#!/bin/sh

export SERVER_PORT=4550

# Database
export LAU_IDAM_DB_NAME=lau_idam
export LAU_IDAM_DB_HOST=0.0.0.0
export LAU_IDAM_DB_PORT=5054
export LAU_IDAM_DB_USERNAME=lauuser
export LAU_IDAM_DB_PASSWORD=laupass
export LAU_IDAM_DB_ADMIN_USERNAME=lauadmin
export LAU_IDAM_DB_ADMIN_PASSWORD=laupass
export LAU_IDAM_ENCRIPTION_KEY=my_very_secure_key

export FLYWAY_PLACEHOLDERS_LAU_IDAM_DB_USERNAME=lauuser
export FLYWAY_PLACEHOLDERS_LAU_IDAM_DB_PASSWORD=laupass
export FLYWAY_URL=jdbc:postgresql://0.0.0.0:5054/lau_idam
export FLYWAY_USER=lauadmin
export FLYWAY_PASSWORD=laupass
export FLYWAY_NOOP_STRATEGY=false