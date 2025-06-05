#!/bin/bash

DB_HOST="127.0.0.1"
DB_PORT="3306"
DB_NAME="database"
DB_USER="user"
DB_PASS="password"

echo "Dropping all tables in database '$DB_NAME'..."

tables=$(mariadb -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -D"$DB_NAME" -e'SHOW TABLES;' | tail -n +2)

if [[ -z "$tables" ]]; then
  echo "No tables found in database '$DB_NAME'."
  exit 0
fi

for table in $tables; do
  echo "Dropping table: $table"
  mariadb -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -D"$DB_NAME" -e"DROP TABLE \`$table\`;"
done

echo "> All tables dropped from '$DB_NAME'!"
