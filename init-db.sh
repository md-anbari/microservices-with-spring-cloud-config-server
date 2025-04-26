#!/bin/bash

# This script initializes multiple databases in PostgreSQL
# It's used as a docker-entrypoint script in the postgres container

set -e
set -u

function create_user_and_database() {
	local database=$1
	echo "  Creating user and database '$database'"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
}

# Check if POSTGRES_MULTIPLE_DATABASES environment variable is set
if [ -z "${POSTGRES_MULTIPLE_DATABASES:-}" ]; then
	echo "POSTGRES_MULTIPLE_DATABASES environment variable is empty, no databases to create."
	exit 0
fi

echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
	create_user_and_database $db
done
echo "Multiple databases created successfully" 