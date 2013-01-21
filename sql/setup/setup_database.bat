psql.exe -U postgres -d ctdb -f ctdb_1_create.sql
psql.exe -U postgres -d ctdb -f ctdb_2_initialise.sql
psql.exe -U postgres -d ctdb -f ctdb_3_customise.sql

