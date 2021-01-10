# Kinesis
Project to help people with severe mobility impairment to control their computer.  
The computer can be waken up when sleeping with the voice through a mobile application.  
The user can control the mouse with his nose, and the keyboard and the click with his voice.  
## Overview
This project is separated in two parts:  
* Wake up the computer
* Control the mouse and the keyboard through the voice and nose movements 

## Equipment used
To create the prototype, we needed some material:
* Arduino Pro Micro 5V
* Raspberry Pi 3
* Voltage converter
* A phone
  
You can find in this repository the folders with the code for each element  
## Setup the project
The project is not usable when downloaded. It needs to be set up.  
  
### Control with nose and voice
Install opencv2:  
``` shell
pip install opencv-python
```  
   
Install dlib:
``` shell
https://www.learnopencv.com/install-dlib-on-windows/
```  

Then numpy :  
``` shell
pip install numpy
```  
  
And pyautogui :  
``` shell
pip install pyautogui
``` 
  
You also will need speech_recognition:  
``` shell
pip install SpeechRecognition
```  
  
Eventually, install [PyAudio](https://stackoverflow.com/questions/61348555/error-pyaudio-0-2-11-cp38-cp38-win-amd64-whl-is-not-a-supported-wheel-on-this-p).

You are now able to launch *nose track.py* in *Nose_and_voice_control[ordinateur]*  
```diff
- ! For the moment, the script doesn't launch itself when the computer wake up. 
```
  
### Wake up the computer
Schema of the steps to wake up:  
![SchemaWakeUp](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/schema_project.png)  
The version we created is currently in french.  
  
#### Raspberry
Here are the step to set up the Raspberry:  
1. The first step is to install a Rasberry PI OS on the SD card of the Raspberry. For that, we used [Raspberry PI Imager](https://www.raspberrypi.org/software/).
![Raspberry-Image-Installer](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_rasp.png)  

2. You will need to connect the raspberry with an ethernet cable.  
To set up the Raspberry, we also needed to connect it to the screen, after the set up, the screen is not needed anymore.  
3. Download the script ![kinesisServerRaspi.py](https://github.com/Irraky/Kinesis/blob/master/Server%5Braspberry%5D/kinesisServerRaspi.py) and put it in `/home/pi`  
You can find it on the folder `Server[raspberry]`  
4. Open the file `etc/rc.local` (you can do that with vim which is already installed) and add the following line at the end of the file:  
``` shell
sudo python /home/pi/kinesisServerRaspi.py
```  
This will launch the script when the raspberry is turned on. 
5. You can now shut down by disconnecting the cable which provides current to the Raspberry and deconnect the Raspberry from the screen. 
6.
Here is the pinout of our Rasberry :  
![Pinout-Raspberry](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/installation_os_rasp.png)  
As you can see, we need to connect three jumpers on:
* GND
* 3,3V
* pin 21
  
#### Arduino
1. Open the Arduino IDE on your computer.
2. Open the file [KinesisArduino.ino](https://github.com/Irraky/Kinesis/blob/master/Mouse_displacement%5BArduino%5D/KinesisArduino.ino).  
You can find it on the folder `Mouse_displacement[Arduino]` of this project.  
![Pinout-arduino](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_arduino.png)  
3. Connect your Arduino to the computer
4. In `Tools>Type of Card`, select *Arduino Micro*  
5. In `Tools>Port`, select the port on which the card is connected.
6. You can now click on the upload button.
7. You will also need to connect jumpers on the arduino to link it to the Raspberry.    
As you can see, we connected three jumpers on:
* GND
* 5V
* pin 2  
8. The problem is that the arduino we use needs 5V, so we use a converter that link the raspeberry to the arduino.  
Here is it pinout:
![Pinout-converter](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_convertisseur.png)  
We have to correctly connect the jumpers we put on the Raspberry and the Arduino to those indicated on the converter.  

#### Mobile application
The Arduino and Raspberry part is ready, we can connect our Raspberry to the current.  
The Arduino is already connected to the computer and the Raspberry since the last step so everything is set up there.


