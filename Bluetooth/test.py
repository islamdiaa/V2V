qfrom BluetoothServer import *

server = BluetoothServer()
server.Accept()
print server.Receive()
server.Close()
