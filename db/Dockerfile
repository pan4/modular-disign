FROM postgres
ENV POSTGRES_PASSWORD password
ENV POSTGRES_USER words_scanner
ENV POSTGRES_DB words_scanner
COPY psql_dump.sql /docker-entrypoint-initdb.d/