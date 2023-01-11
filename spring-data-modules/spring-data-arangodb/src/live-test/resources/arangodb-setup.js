#!/usr/bin/arangosh --javascript.execute

print('creating database: tuyucheng-database & user: tuyucheng');
db._createDatabase("tuyucheng-database", {}, [{ username: "tuyucheng", passwd: "password", active: true}]);