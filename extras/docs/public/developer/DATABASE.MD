
# On Device Database

Provided by sqlite3

## Table schema
    sqlite3 growbox.db ".schema port_data"


## Grow Periods table
### Update GrowPeriod start date
    sqlite3 growbox.db "update grow_period set startDate = '2019-03-10 12:00:00' WHERE id = 1;"
    sqlite3 growbox.db "update grow_period set vegetationDays = 90 where id = 1;"

### Add GrowPeriod column
    ALTER TABLE grow_period ADD COLUMN wateringSeconds INTEGER DEFAULT 30;
    ALTER TABLE grow_period ADD COLUMN nutrientSeconds INTEGER DEFAULT 0;
    ALTER TABLE grow_period ADD COLUMN floodSeconds INTEGER DEFAULT 0;

### Add nutrientBSeconds column
    sqlite3 growbox.db "ALTER TABLE grow_period ADD COLUMN nutrientBSeconds Decimal;"

### Alter column type from Integer to Decimal keeping data
    sqlite3 growbox.db "ALTER TABLE grow_period ADD COLUMN nutrientACSeconds Decimal;"
    sqlite3 growbox.db "UPDATE grow_period SET nutrientACSeconds = CAST(nutrientSeconds as Decimal);"
    sqlite3 growbox.db "ALTER TABLE grow_period DROP COLUMN nutrientSeconds;"

### Adjust nutrient dispenser time in seconds
    sqlite3 growbox.db "update grow_period set nutrientACSeconds = 3.25 WHERE id = 11;"
    sqlite3 growbox.db "update grow_period set nutrientBSeconds = 1.29 WHERE id = 11;"


# Ports table  
### Add a new port (input/output) (GPIO BCM nomenclature used)    
    sqlite3 growbox.db "INSERT INTO ports(portId,name,gpio,portType,enabled) VALUES ('camera','Camara',0,'INPUT',1);"
    sqlite3 growbox.db "INSERT INTO ports(portId,name,gpio,portType,enabled) VALUES ('fan2','Fan 2',22,'OUTPUT',1);"
    sqlite3 growbox.db "INSERT INTO ports(portId,name,gpio,portType,enabled) VALUES ('flood','Flood Pump',24,'OUTPUT',1);"
    sqlite3 growbox.db "INSERT INTO ports(portId,name,gpio,portType,enabled) VALUES ('nutrient_a','Nutrient AC',27,'OUTPUT',1);"
    sqlite3 growbox.db "INSERT INTO ports(portId,name,gpio,portType,enabled) VALUES ('nutrient_b','Nutrient B',25,'OUTPUT',1);"
    
    sqlite3 growbox.db "INSERT INTO ports(portId,name,gpio,portType,enabled) VALUES ('nutrient_c','Nutrient C',7,'OUTPUT',1);"

    sqlite3 growbox.db "update ports set portId = 'nutrient_a' WHERE portId = 'nutrient_ac';"
    sqlite3 growbox.db "update ports set name = 'Nutrient A' WHERE portId = 'nutrient_a';"


### List v2 ports
    sqlite3 growbox.db "select * from ports;"
    sqlite3 growbox.db "select * from port_data WHERE portId = 'temperature' ORDER BY date DESC LIMIT 20;"


### Update GPIO port numbers (BCM)
    sqlite3 growbox.db "update ports set gpio = 4 where portId = 'lights';"
    sqlite3 growbox.db "update ports set gpio = 17 where portId = 'fan';"
    sqlite3 growbox.db "update ports set gpio = 27 where portId = 'water';"
    sqlite3 growbox.db "update ports set gpio = 23 where portId = 'temperature';"


### Update port id for a given port  (From fan2 to water2)
    sqlite3 growbox.db "update ports set portId = 'water2' where portId = 'fan2';"
    sqlite3 growbox.db "update ports set name = 'Nutrients' where portId = 'water2';"

    sqlite3 growbox.db "update ports set portId = 'flood' where portId = 'water3';"

# Sensor Data

### Min, Max and Average values
    sqlite3 growbox.db "select distinct(substr(date,0,11)) as daily, min(value), max(value), avg(value) from port_data WHERE portId = 'temperature' AND date > '2020-06-01' GROUP BY daily;"
    sqlite3 growbox.db "select * from port_data WHERE portId = 'temperature' ORDER BY date DESC LIMIT 20;"

### Remove bogus  humidity > 100%
    sqlite3 growbox.db "delete from port_data where portId = 'humidity' AND value LIKE '1__.%';"

### Change reading date
    sqlite3 growbox.db "update port_data set date = '2021-07-24 09:00:00' where readingId = 27007;"
    sqlite3 growbox.db "update port_data set date = '2022-04-11 15:00:00' where readingId = 34783;"


### Change last flood date
    ## latest: PortDataEntity{readingId=36562, inputOutputId='flood', date='2022-05-16 21:47:02', value='200.0'}
    sqlite3 growbox.db "update port_data set date = '2022-05-17 10:00:00' where readingId = 36562;"



### Alter tables
    sqlite3 growbox.db "ALTER TABLE port_data ADD COLUMN valueFloat Decimal;"
    sqlite3 growbox.db "UPDATE port_data SET valueFloat=CAST(value AS Decimal);"
    sqlite3 growbox.db "ALTER TABLE port_data RENAME COLUMN value TO valueString;"
    sqlite3 growbox.db "ALTER TABLE port_data RENAME COLUMN valueFloat TO value;"

## Clear port data
**Warning**: use with caution

    sqlite3 growbox.db "delete from port_data;"



