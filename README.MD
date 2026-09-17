# OpenGrowbox

A system for growing plants autonomously, designed for the RaspberryPi.
Open source software & hardware. Fully featured for hydroponics or soil based growing. 

## Features 
  - 🌤️️ Automatic Weather Control (Lights, Fans, Temperature, Humidity)
  - 💧 Automatic Water & Nutrient dispenser (Hydroponic / Soil based)
  - 📹 GrowLapse videos & images (supports many cameras & PTZ controls)
  - 📒 Historical data on device
  - 📱 Mobile App access, Alerts & Notifications. 

## The hardware

- 1 Raspberry Pi 3 or newer recommended (as video generation tasks can take a long time on older devices)
- 1 Raspberry Pi power supply 
- 6 or 8 port relay board like [SainSmart 8-Channel Relay Module](https://www.amazon.com/SainSmart-101-70-102-8-Channel-Relay-Module/dp/B0057OC5WK?crid=2C7CDYYSRJW8L&dib=eyJ2IjoiMSJ9.EAAKDjgBlp-AIf1F74eRyrWenEMcGzuL6lRWJVMKiaFdFniLR3YxYG76k5VYHBrJJ5fj_iHCDWL_QjhJTM6PJAJh1KUv4ibVyBTWdiye41SBJUogUWN9YMB6GQtuIAhDu-6niZ6jbsbEWmpur47lTGsVgr2mo7qGewc1ijukJbYHSIfbenKiSuNpMFYwHbagIrjc7wqqgMaggmITgJmHS6Knp8S4RPnDh36ZTlBalVs.BXp_RavIcTswm7ZbinKG_AOP6BWcjY5DLyZdl_vlHys&dib_tag=se&keywords=sainsmart%2Brelay&qid=1759924002&sprefix=sainsmart%2Brelay%2Caps%2C237&sr=8-6&th=1)
- 3 peristaltic water pumps like [Gikfun 12V DC Dosing Pump](https://www.amazon.com/Gikfun-Peristaltic-Connector-Aquarium-Analytic/dp/B01IUVHB8E?crid=K12Q1MJEL29A&dib=eyJ2IjoiMSJ9.qNvtwo1ELAubDspB-glLnZPQbijIcumjmbakj9rHIA-b7ehCiKNT6UVZ-WOD6pOtps0hBuhvuw4tIPzAbISN2uwqypHVr1gkbuy47a7JFLv-HuRG1EMqQy457UHUrUHwhgTa2GG2moMyZQIi2prOSQnL6JafOjC3NMcLGYFOSow9is0YAhTOWpqXkVsdSw4WT3FQMBLBJQ05HNj0jSewVbZOc8Hq-gR9s5akZIwruag.J1IhxbNaggeEmvOqqmINHUJ0HdpVXSi5T1lHk1JJLXU&dib_tag=se&keywords=peristaltic+pump+12v&qid=1759924158&sprefix=peristal%2Caps%2C344&sr=8-5)
- 2 submersible water pumps like [these](https://www.amazon.com/s?k=submersible+water+pump+12v&crid=1BTZJSA8R9R6B&sprefix=submersible+water+pump+1%2Caps%2C627&ref=nb_sb_noss_2)
- 1 Hall effect flow meter [link](https://www.amazon.com/s?k=water+hall+effect+flow+raspberry+pi&crid=UUHFA9XSCRAX&sprefix=water+hall+effect+flowraspberry+pi%2Caps%2C176&ref=nb_sb_noss)
- 300W full spectrum LED lights
- GroovePi Hat or similar for analog sensors like pH sensors.[Grove Base Hat](https://wiki.seeedstudio.com/Grove_Base_Hat_for_Raspberry_Pi/) or [GrovePi+](https://wiki.seeedstudio.com/GrovePi_Plus/) 
- 2 x 12v Fans (old PC/laptop fans are best if you want quiet!)
- 1 Air Filter
- 1 Raspberry Pi hi-res camera (others are also supported) [link](https://www.raspberrypi.com/products/raspberry-pi-high-quality-camera/)
- DHT 11/22/AM2302 or compatible temperature/humidity sensors
- pH sensors [pH sensor compatible with RaspberryPi](https://www.amazon.com/Analog-Professional-Compatible-Arduino-Raspberry/dp/B08DRFDSLX?crid=2OOOGH93IXFA5&dib=eyJ2IjoiMSJ9.McdyVtDjQk3t1ehC8iJoz7oa_j7cPaWnCCdLE9de5gg-hvK1KZZkfz2UD5cQfXgWsI9BPq67nre-sLshwz9NHUxtU_o39MBFxhH8niqfYWJYCbJknRJ9vXUXgmR0LrAQP0FVYrf6_D2sVmalb_OrYkuIvvuC1egF74I75VBDd6Yli1E_hVng-T0V87KJbu_iBGh64842lzmqAKbG2pMouqgNs2_EXYFpWTuMFcGZWZc.AYwZ0tPFo7X140PIEvwaorRCXoQfPkhX6AkHHWDcNhw&dib_tag=se&keywords=hi+quality+ph+sensor+raspberry+pi&qid=1759925106&sprefix=hi+quality+ph+sensor+raspberry+pi%2Caps%2C233&sr=8-1)
- 12V power supply (for driving the pumps, fans)
 

## The software

- **wiringpi GPIO api** for sensors, relays and more.
- **Mosquitto MQTT** for IoT messaging 
- **ffmpeg** & **imagemagick** for growlapse (timelapse) videos & pictures
- **libcamera** for taking photos
- **sqlite3** for persistence

## Install Growbox Control 

On your RaspberryPi OS, Raspbian, Ubuntu or any Debian based distribution.

Remember that before installing you need to go through the specific guides for setting up the hardware:

- [LightsFans.md](./extras/docs/public/LightsFans.md)
- [TemperatureHumidity.md](./extras/docs/public/TemperatureHumidity.md)
- [WateringNutrients.md](./extras/docs/public/WateringNutrients.md)



### Add key

    wget -O --no-check-certificate - https://repo.smartgrow.me.s3-sa-east-1.amazonaws.com/growboxuy-pgp.asc | sudo apt-key add -
or

    wget -O - http://repo.smartgrow.me.s3-sa-east-1.amazonaws.com/growboxuy-pgp.asc | sudo apt-key add -

### Add repository

    echo "deb http://repo.smartgrow.me.s3-sa-east-1.amazonaws.com/ /" | sudo tee -a /etc/apt/sources.list.d/growbox.list > /dev/null

### Update apt and install

    sudo apt-get update
    sudo apt install growbox-control


## Software Configuration

You can check [config.properties](extras/growbox2_test_to_remove/config.properties) to see the available options. 
    
    cd /opt/growbox-control
    

## GPIO Ports

To make the Growbox work you need to set up each port (lights, pumps, etc) and adjust the GPIO address to the ones you used. 
There's separate guides for each of these modules.


- [LightsFans.md](./extras/docs/public/LightsFans.md)
- [TemperatureHumidity.md](./extras/docs/public/TemperatureHumidity.md)
- [WateringNutrients.md](./extras/docs/public/WateringNutrients.md)

    
### List ports
After you've connected the wires and can successfully use the sensor/module/pump using the **gpio** command, you can update the port's address in the database.

    sqlite3 growbox.db "select * from ports;"

            humidity|Humidity|1|INPUT|1
            humiditySoil|Soil Humidity|2|INPUT|1
            light|Light Sensor|3|INPUT|1
            temperature|Temperature|22|INPUT|1
            water|Water Tank Pump|23|OUTPUT|1
            flood|Flood Pump|24|OUTPUT|1
            lights|Lights|17|OUTPUT|1
            fan|Output Fan|4|OUTPUT|1
            camera|Camara|0|INPUT|1
            nutrient_a|Nutrient A|27|OUTPUT|1
            nutrient_b|Nutrient B|25|OUTPUT|1
            nutrient_c|Nutrient C|7|OUTPUT|1


### Adjust the GPIO ports for each input and output ports 
Growbox uses the 


    sqlite3 growbox.db "update ports set gpio = 4 where portId = 'lights';"
    sqlite3 growbox.db "update ports set gpio = 17 where portId = 'fan';"
    sqlite3 growbox.db "update ports set gpio = 27 where portId = 'water';"
    sqlite3 growbox.db "update ports set gpio = 23 where portId = 'temperature';"

### Disable or enable ports

To disable a port set 'enabled' property to 0 (1 to enable) on the desired port, i.e.: temperature
    
    sqlite3 growbox.db "update ports set enabled = 0 where portId = 'temperature';"


See more in the [DATABASE.MD](extras/docs/public/developer/DATABASE.MD) document.


### Manage the Growbox-Control

Login to you Raspberry Pi device 

Start the service

    systemctl growbox-control start
    
Stop the service

    systemctl growbox-control stop

Check the logs

    tail -f /var/log/growbox-control/control.log


Uninstall Growbox Control

    sudo apt remove growbox-control