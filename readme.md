# Kinesis
Project to help people with severe mobility impairment to control their computer
The computer can be waken up when sleeping.  
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
You can find in this repository the folders with the code for each element  
## Setup the project
The project is not usable when downloaded. It needs to be set up.  
  
### Noise and voice control
Install opencv2:  
`pip install opencv-python` 
   
Install dlib:
`https://www.learnopencv.com/install-dlib-on-windows/`  

Then numpy :  
`pip install numpy`  
  
And pyautogui :  
`pip install pyautogui`  
  
You also will need speech_recognition:  
`pip install SpeechRecognition`  
  
Eventually, install PyAudio :  
`https://stackoverflow.com/questions/61348555/error-pyaudio-0-2-11-cp38-cp38-win-amd64-whl-is-not-a-supported-wheel-on-this-p`  

You are now able to launch *nose track.py* in *Nose_and_voice_control[ordinateur]*  
```diff
- ! For the moment, the script doesn't launch itself when the computer wake up. 
```
  
### Wake up the computer
Schema of the steps to wake up:  
![SchemaWakeUp](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/schema_project.png)
The version we created is currently in french.  
  
#### Raspberry
Here is the pinout of our Rasberry :
![Pinout-Raspberry](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_rasp.png)
As you can see, we connected three jumpers on:
* GND
* 3,3V
* pin 21


#### Arduino
![Pinout-arduino](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_arduino.png)
As you can see, we connected three jumpers on:
* GND
* 5V
* pin 2
The problem is that the arduino we use needs 5V, so we use a converter that link the raspeberry to the arduino.  
Here is it pinout:
![Pinout-converter](https://github.com/Irraky/Kinesis/blob/master/Readme_pictures/pinout_convertisseur.png)

#### Mobile application


