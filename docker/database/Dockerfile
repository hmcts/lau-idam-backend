FROM postgres:18.0-alpine

COPY init-db.sh /docker-entrypoint-initdb.d

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -c 'select 1' -d ${LAU_IDAM_DB_NAME} -U ${LAU_IDAM_DB_USERNAME}

EXPOSE 5432
