from Database import *



def main():
	db=Database('Traffic_info','traffic','test123123')
	#db.DropTable('Test_msg')
	#return 
	columns={'message':'varchar(255)','ip':'varchar(200)','port':'INT','time':'varchar(255)'}
	db.CreateTable('Test_msg',columns, True)
	attr={'message':'test44','ip':'192.168.1.5','port':'6666','time':'6'}
	db.Insert('Test_msg',attr)
	print "Inserted"
	fields,rows=db.SelectAll('Test_msg')
	print "SelectAll " + str(fields) + "\n"+ str(rows)
	fields,rows=db.select('Test_msg', {'ip':'192.168.1.3'})
	print fields
	print rows
if __name__ == '__main__':
	main()
