import BluetoothServer as btserver
import WifiServer as wserver
import WifiClient as wclient
import Wifi
import Database as db
import thread
import time
import json

def WifiServerListeing():
	server = wserver.WifiServer()
	server.Start()
	while(True):
		server.Accept()
		server.Receive()
		server.CloseConnection()

Wifi.getuphosts()
time.sleep(5)
client = wclient.WifiClient()
BT = btserver.BluetoothServer()
BT.Accept()

DB = db.Database("TestDB","test","test")
columns={'message':'varchar(255)','time':'varchar(255)'}
DB.CreateTable('Test_Table',columns, True) #True = Hashable to avoid replications.


try:
   thread.start_new_thread(WifiServerListeing,())
except:
   print "Error: unable to start thread"

while(True):
	data = json.loads(BT.Receive())
	print data['msg'] + " from Bluetooth!"	
	attr={'message':str(data['msg']),'time':'0'}
	DB.Insert('Test_Table',attr)
	fields,rows = DB.SelectAll('Test_Table')
	print str(fields) + "\n" + str(rows)
	client.Broadcast(data['msg'])
