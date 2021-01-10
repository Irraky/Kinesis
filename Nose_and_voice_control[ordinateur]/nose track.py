import cv2
import dlib
import numpy as np
import pyautogui
import speech_recognition as sr
import threading

class myThread (threading.Thread):
    def __init__(self, threadID, name, counter):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.counter = counter


    def run(self):
        print("Starting " + self.name)
        if(self.threadID == 1):
            voice_clicks()
        else:
            nose_track()
        print("Exiting " + self.name)



def voice_clicks():
    reco = sr.Recognizer()
    #reco.recognize_google_cloud()
    mic = sr.Microphone()
    while True:
        try:
            with mic as source:
                
                reco.adjust_for_ambient_noise(source)
                audio = reco.listen(source)

            
            word = reco.recognize_google(audio, language='fr-FR')
            print(word)
            if("clic droit" in word):
                pyautogui.rightClick()
            elif("double clic" in word):
                pyautogui.doubleClick()
            elif("clic" in word):
                pyautogui.click()
            #test lillian
            elif("Ah"== word):
                pyautogui.press('a')

            elif("B" == word):
                pyautogui.press('b')
            
            elif("c'est" == word):
                pyautogui.press('c') 

            elif("des" == word or  "dés" == word or "dé" == word or "dès" == word):
                pyautogui.press('d')
	
            elif("euh" == word or "la lettre E" == word):
                pyautogui.press('e')

            elif("f" == word):
                pyautogui.press('f')

            elif("j'ai" == word or "jet" == word):
                pyautogui.press('g')

            elif("hache" == word or "h" == word or "H" == word):
                pyautogui.press('h')

            elif("i" == word or "I" == word or "la lettre I" == word):
                pyautogui.press('i')

            elif("j" == word or "J" == word or "j'y" == word):
                pyautogui.press('j')

            elif("K" == word or "k" == word or "cas" == word or "car" == word):
                pyautogui.press('k')
 
            elif("L" == word or "l" == word or "elle" == word or "elles" == word):
                pyautogui.press('l')

            elif("m" == word or "M" == word or "aime" == word or "aiment" == word):
                pyautogui.press('m')

            elif("n" == word or "N" == word):
                pyautogui.press('n')

            elif("o" == word or "O" == word or "oh" == word or "la lettre O" == word):
                pyautogui.press('o')

            elif("pet" == word or "paix" == word or "p" == word or "P" == word or "la lettre P" == word):
                pyautogui.press('p')
	    
            elif("tu" == word or "q" == word or "Q" == word or "cul" == word):
                pyautogui.press('q')

            elif("air" == word or "r" == word or "R" == word):
                pyautogui.press('r')

            elif("s" == word or "S" == word):
                pyautogui.press('s')

            elif("t" == word or "T" == word or "t'es" == word or "tu es" == word or "la lettre T" == word):
                pyautogui.press('t')


            elif("u" in word or "U" in word or "la lettre U" == word):
                pyautogui.press('u')


            elif("v" == word or "V" == word or "vais" == word):
                pyautogui.press('v')
          
            elif("w" == word or "W" == word):
                pyautogui.press('w') 
	
            elif("x" == word or "X" == word):
                pyautogui.press('x')

            elif("y" == word or "Y" == word):
                pyautogui.press('y')

            elif("z" == word or "Z" == word):
                pyautogui.press('z')
        except sr.UnknownValueError:
            pass
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

def nose_track():
    predictor = dlib.shape_predictor('shape_68.dat')
    frontal_face = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")
    #eye_detector = cv2.CascadeClassifier("haarcascade_eye.xml")
    cap = cv2.VideoCapture(0)
    cap.set(3,1024)
    cap.set(4,768)



    while True:
        sucess, img = cap.read()
        imgGray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)

        faces = frontal_face.detectMultiScale(imgGray,1.1,4)

        for(x,y,w,h) in faces:
            face = dlib.rectangle(x,y,x+w,y+h)
            #cv2.rectangle(img,(x,y),(x+w,y+h),2)
            landmarks = predictor(imgGray,face)
            x_nose = landmarks.part(30).x-192
            y_nose = landmarks.part(30).y-125

            
            

            if(x_nose < 1):
                x_nose = 1
            if(y_nose <1):
                y_nose = 1
            if(x_nose > 639):
                x_nose = 639
            if(y_nose > 359):
                y_nose = 359

            pyautogui.moveTo((640-x_nose)*3,y_nose*3,0)
        

        cv2.imshow("Video",img)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break


thread1 = myThread(1, "Thread-1", 1)
thread2 = myThread(2, "Thread-2", 2)

thread1.start()
thread2.start()

    
