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
echo "inserting country records"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/country.sql
echo "inserting states records"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/state.sql
echo "inserting lookup records"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/lookup.sql
echo "inserting personal_interest records"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/personal_interest.sql
echo "inserting professional_interest records"
mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/professional_interest.sql
#echo "inserting remote records"
#mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/remote.sql
# echo "inserting testdata records"
# mysql -u $DB_USERNAME -p$DB_PASSWORD $DBNAME < ../sqls/testdata.sql
