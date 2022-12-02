DROP TABLE IF EXISTS trucks;

CREATE TABLE trucks (
    truckId INTEGER PRIMARY KEY AUTOINCREMENT,
    truckName TEXT UNIQUE NOT NULL,
    longitude REAL NOT NULL,
    latitude REAL NOT NULL,
    locConf REAL NOT NULL,
    lastSeen INTEGER NOT NULL
);

INSERT INTO trucks (truckName, longitude, latitude, locConf, lastSeen) 
VALUES
    ("Burrito King", -88.2276674, 40.1127105, 86.5, 1669953149),
    ("Fernando's", -88.2288938, 40.1099063, 18.5, 1669952849);