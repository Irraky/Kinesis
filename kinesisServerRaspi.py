import socket
import sys
import TPi.GPIO as GPIO
import time


# BCM pin name from processor
GPIO.setmode(GPIO.BCM)
PINARDUINO = 21

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('10.3.141.1', 7000)
print('starting up on %s port %s' % server_address)
sock.bind(server_address)


# setup pin 21 as output 
GPIO.setup(PINARDUINO, GPIO.OUT)
GPIO.output(PINARDUINO, 0)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print('waiting for a connection')
    connection, client_address = sock.accept()
    try:
        print('connection from', client_address)
        GPIO.output(PINARDUINO, 1)
        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(16)
            print('received "%s"' % data)
            if data:
                print('sending data back to the client')
                connection.sendall(data)
            else:
                print('no more data from', client_address)
                break
        time.sleep(3)
        GPIO.output(PINARDUINO, 0)
                
    finally:
        # Clean up the connection
        connection.close()