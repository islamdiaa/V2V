from WifiServer import *

Server = WifiServer()
Server.Start()
Server.Accept()
print Server.Receive()
Server.CloseConnection() 
