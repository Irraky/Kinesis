# Kinesis
Project to help people with severe mobility impairment to control their computer.  
 
  
## Table of content 
1. [General Info](#overview)
2. [Equipment used](#equipment-used)
3. [Technologies](#Technologies)
4. [Set up the project](#setup-the-project)
5. [Set up the controll through the nose and the voice](#control-with-nose-and-voice)
6. [Set up the wake up](#wake-up-the-computer)
7. [Test](#test)
8. [FAQs](#faqs)

## General Info
***
The computer can be waken up when sleeping with the voice through a mobile application.  
The user can control the mouse with his nose, and the keyboard and the click with his voice.
This project is separated in two parts:  
* Wake up the computer
* Control the mouse and the keyboard through the voice and nose movements 

## Equipment used
***
To create the prototype, we needed some material:
* Arduino Pro Micro 5V
* Raspberry Pi 3
* Voltage converter
* A phone

## Technologies
***
* [IDE Arduino](https://www.arduino.cc/en/software): Version 1.8.13
* [Android Studio](https://developer.android.com/studio): Version 4.1.1
* [Raspberry Pi Imager](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_rasp.png): Version 1.5
  
You can find in this repository the folders with the code for each element  
  
## Setup the project
***
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
3. Download the script [kinesisServerRaspi.py](https://github.com/Irraky/Kinesis/blob/master/Server%5Braspberry%5D/kinesisServerRaspi.py) and put it in `/home/pi`  
You can find it on the folder `Server[raspberry]`  
4. Connect your phone to this wi-fi (you know your password from last step)
5. Open the file `etc/rc.local` (you can do that with vim which is already installed) and add the following line at the end of the file:  
``` shell
sudo python /home/pi/kinesisServerRaspi.py
```  
6. Take note for later of the ip address of your raspberry. For that, open a console and enter:
```shell
ifconfig
```
Check the block of *wlan0*. The ip address is the *inet address*.
This will launch the script when the raspberry is turned on. 
7. You can now shut down by disconnecting the cable which provides current to the Raspberry and deconnect the Raspberry from the screen. 
8.
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
  
The Arduino and Raspberry part is ready, we can connect our Raspberry to the current.  
The Arduino is already connected to the computer and the Raspberry since the last step so everything is set up there.
  
#### Mobile application
We now need to prepare the application.
1. For that we have to open [Android Studio](https://developer.android.com/studio)
2. Open the project [kinesis](https://github.com/Irraky/Kinesis/tree/master/Kinesis%5Bmobile_application%5D) with android studio `open an existing project`.  
It's the folder `Kinesis[mobile_application]` of this repository.   
3. Open the file `Kinesis[mobile_application]/app/src/main/java/org/kaldi/demo/KaldiActivity.java`  
4. On line 340, change the value of ipStr by the value of the ip of the Raspberry. (You had it from step 6 of set up Raspberry)  
```java
wake("10.3.141.1");
```
3. The phone needs to pass in developer mode. The procedure depends on the OS. 
In my case, I had to tap 7 times on *About this phone* in the parameters.
4. The phone needs to be connected to the computer.  
5. You will have a pop-up on the phone about USB debug. You can accept or not. It's not important if you don't want to change the project.  
If you want to add your improvements to the project, accept to be able to debug on the console of android studio.  
6. On Android Studio, select your phone as the devices to use
7. Run the project  
8. Once the project is launched, you can disconnect the phone from the computer if you don't want to debug: the application is now in it.  

## Test
***
If you have done all the previous steps, you can test the app.  
* Put your computer in sleep mode.
* Open the application (if it's not already done)
* Say "allumer ordinateur"
The list of computers will appear.  
* Say "numéro un"
The first one in the list will be chosen. (For the moment the code hasn't the feature to wake up more than one computer.)
* Look at your screen, it should turn on.
We tried this project on a Surface Book 3 so we have a huge advantage: the computer is unlocked through face recognition.

## FAQs
1. **Who made this project ?**
This project was made by a team of five students from the school Efrei Paris.  
It's a project where student are free to do what they want on the topic *The numeric to the benefit of society*.
2. **Why this project ?**
There is today 12 millions people in situation of handicap in France. We found that this project was a great opportunity to develop something to help them.
  
