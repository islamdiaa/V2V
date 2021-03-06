#!/usr/bin/env python

import socket

class WifiServer:
	def __init__(self):
		self.TCP_IP='192.168.1.5'
		self.TCP_PORT = 6666
		self.BUFFER_SIZE = 1024  # Normally 1024, but we want fast response
	def Start(self):
		print "Starting Wifi Server"
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.s.bind((self.TCP_IP, self.TCP_PORT))
		self.s.listen(1)
	def Accept(self):	
		print "Accepting connection"	
    		self.conn, self.addr = self.s.accept()
	def Receive(self):
    		data = self.conn.recv(self.BUFFER_SIZE)
    		print "Received from[%s] %s: via wifi" %(self.addr, data) 
		return data
	def Send(self,msg):
    		self.conn.send(msg)
	def CloseConnection(self):    
		self.conn.close()
		print "Connection closed!"
