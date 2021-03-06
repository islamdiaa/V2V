import BluetoothServer as btserver
import WifiServer as wserver
import WifiClient as wclient
import Wifi
import Database as db
import thread
import time

DB = db.Database("TestDB","test","test")
columns={'message':'varchar(255)','time':'varchar(255)'}
DB.CreateTable('Test_Table',columns, True) #True = Hashable to avoid replications.

Wifi.getuphosts()
server = wserver.WifiServer()
client = wclient.WifiClient()
server.Start()
while(True):
	server.Accept()
	data = server.Receive()
	attr={'message':str(data),'time':str(time.clock())}
	DB.Insert('Test_Table',attr)
	client.SendToIP('Echo ' + str(data),server.addr[0])
	server.CloseConnection()
