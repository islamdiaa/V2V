from bluetooth import *
import socket

class BluetoothServer:
	def __init__(self):
		self.server_sock=BluetoothSocket( RFCOMM )
		self.server_sock.bind(("",PORT_ANY))
		self.server_sock.listen(1)
		self.port = self.server_sock.getsockname()[1]
		self.uuid =  "00000003-0000-1000-8000-00805F9B34FB"
		advertise_service( self.server_sock, "BluetoothServer",
                   	service_id = self.uuid,
		   	service_classes = [ self.uuid, SERIAL_PORT_CLASS ],
                   	profiles = [ SERIAL_PORT_PROFILE ],
                   	protocols = [ OBEX_UUID ] 
                    	)
		print("Waiting for connection on RFCOMM channel %d" % self.port)
	def Accept(self):
		self.client_sock, self.client_info = self.server_sock.accept()
		print("Accepted connection from ", self.client_info)
	def Receive(self):
		try:
			data = self.client_sock.recv(1024)
			print("received [%s]" % data)
			return data
		except IOError:
    			pass
	def Close(self):
		print "Disconnected"
		self.client_sock.close()
		self.server_sock.close()
