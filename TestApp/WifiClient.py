import socket               # Import socket module
import sys

class WifiClient:
        def __init__(self):
                self.port = 6666     # Reserve a port for your service.
                self.BUFF_Size = 1024
                self.fname = "/tmp/uphosts"
                with open(self.fname) as f:
                        self.hosts = f.readlines()
        def SendToIP(self,msg,ip):
                print str(msg) + " " + str(ip)
                try:
                        print ip
                        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        s.connect((ip, self.port))
                        s.send(msg)
                        print "Sending message to the other PI"
                        s.close()
                except Exception as e:
                        print e
        def Broadcast(self,msg):
		print "In Broadcasting"
                for ip in self.hosts:
			print ip
                        TCP_IP = ip.strip('\n')
                        self.SendToIP(msg,TCP_IP)
        def Close(self):
                print "Closing the Wifi Client"



