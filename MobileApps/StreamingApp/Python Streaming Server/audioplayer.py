import socket
import ossaudiodev
import sys
UDP_IP = "10.7.22.236"
UDP_PORT =6666

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
address = (UDP_IP, UDP_PORT)
sock.bind(address)
#speaker=ossaudiodev.open('w')
#fmt=ossaudiodev.AFMT_S16_LE
#speaker.setparameters(fmt,1,44100)

while True:
	data, addr = sock.recvfrom(512) # buffer size is 1024 bytes
	print data
	#	speaker.write(data)
