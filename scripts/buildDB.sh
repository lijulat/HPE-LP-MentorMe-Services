#!/bin/bash

DBNAME="mentorme"

#CWD-- should add some cmdline flags to create or drop but just blanket creating right now
#CWD-- should also handle errors but ¯\_(ツ)_/¯
echo "creating $DBNAME DB"
mysqladmin -u $DB_USERNAME -p$DB_PASSWORD create $DBNAME
echo "creating schema"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/schema.sql
echo "building DDL"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/ddl.sql
echo "inserting initial data records"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/init.sql
