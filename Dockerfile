FROM postgres
ADD sql_scripts/postgress.sql /docker-entrypoint-initdb.d