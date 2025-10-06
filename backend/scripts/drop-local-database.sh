#!/bin/bash

DB_HOST="127.0.0.1"
DB_PORT="3306"
DB_NAME="database"
DB_USER="user"
DB_PASS="password"

echo "Dropping all tables in database '$DB_NAME'..."

tables=$(mariadb -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
  -D"$DB_NAME" -e'SHOW TABLES;' | tail -n +2 | tr '\n' ',' | sed 's/,$//')

if [[ -z "$tables" ]]; then
  echo "No tables found in database '$DB_NAME'."
  exit 0
fi

mariadb -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -D"$DB_NAME" \
  -e"SET FOREIGN_KEY_CHECKS = 0; DROP TABLE $tables; SET FOREIGN_KEY_CHECKS = 1;"

echo "> All tables dropped from '$DB_NAME'!"
