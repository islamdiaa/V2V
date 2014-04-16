from bluetooth import *
import sys

input = raw_input("Input ya bashaa !!\n")

addr = '00:15:83:0C:BF:EB'


# search for the SampleServer service
#uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
uuid = "00000003-0000-1000-8000-00805F9B34FB"
service_matches = find_service(name = "SampleServer", uuid = uuid, address = addr )

print service_matches

if len(service_matches) == 0:
    print("couldn't find the SampleServer service =(")
    sys.exit(0)

first_match = service_matches[0]
port = first_match["port"]
name = first_match["name"]
host = first_match["host"]

print("connecting to \"%s\" on %s" % (name, host))

# Create the client socket
sock=BluetoothSocket( RFCOMM )
sock.connect((host, port))

print("connected.  type stuff at port " + str(port))
while True:
    data1 = raw_input()
    #if len(data1) == 0: break
    sock.send(data1)
#while True:
#   data1 = sock.recv(1024)
#   if len(data1) == 0: break
#   print("Received [%s]" %data1)

sock.close()
