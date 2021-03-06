import MySQLdb
import sys
import hashlib

class Database:
	def __init__(self, dbname,uname,passwd):
		self.__con= MySQLdb.connect('localhost','root', 'root')
		self.__cursor=self.__con.cursor()
		query="show databases"
		self.__cursor.execute(query)
		rows=self.__cursor.fetchall()
		found=False
		for row in rows:
			if (str(row[0])==dbname):
				found=True
				break
		#if the database does not exist create it
		if(found==False):
			query='select user from mysql.user'
			self.__cursor.execute(query)
			rows=self.__cursor.fetchall()
			found=False
			for row in rows:
				if(str(row[0])==uname): 
					found=True
					break
			if(found==False):
				query="create user "+uname+"@localhost identified by '"+passwd+"'"
				self.__cursor.execute(query)
			query='CREATE DATABASE '+str(dbname)
			self.__cursor.execute(query)
			query="GRANT ALL PRIVILEGES ON "+dbname+".* To "+uname+"@localhost"
			self.__cursor.execute(query)
		#close the root connection and connect to the database
		self.__con.close()
		self.__con=MySQLdb.connect('localhost',uname,passwd,dbname)
		self.__cursor=self.__con.cursor()
		self.__con.commit()
		return

	def CreateTable(self,tbname,attr,hashable=False):
		query = "CREATE TABLE IF NOT EXISTS "+tbname
		query+=" ( id int AUTO_INCREMENT,"
		for first,second in attr.iteritems():
			query+=first+" "+second+","
		if hashable:
		    query +="hash_tab varchar(255),"
		query+="PRIMARY KEY (id) );"
  		self.__cursor.execute(query)
		self.__con.commit()
		return
	
	def DropTable(self,tbname):
		query="drop table "+tbname
		self.__cursor.execute(query)
		self.__con.commit()
		return


	def hash_attr(self, attr):
		h = hashlib.sha1()
		h.update(str(attr.values()).strip('[]'))
		
		return h.hexdigest()
		
	def Insert(self,tbname,attr):
		query="desc "+tbname
		self.__cursor.execute(query)
		result=self.__cursor.fetchall()
		if result[-1:][0][0] == 'hash_tab':
			h = self.hash_attr(attr)
			fields, res=self.select(tbname, {'hash_tab':str(h)})
			if(res):
				print "[Info] Data already exist"
				return
			
			query="INSERT INTO "+tbname
			names="("
			values="("
			for first,second in attr.iteritems():
				names+=first+","
				values+="\""+second+"\","
			names+='hash_tab'
			values+="'%s'" %(h)
			names+= ")"
			values+=")"
			query+=names+" values "+values
				
		else:	
			query="INSERT INTO "+tbname
			names="("
			values="("
			for first,second in attr.iteritems():
				names+=first+","
				values+="\""+second+"\","
			names=names[:-1]+")"
			values=values[:-1]+")"
			query+=names+" values "+values
		self.__cursor.execute(query)
		self.__con.commit()
		return

	def SelectAll(self,tbname):
		query="select * from "+tbname
		self.__cursor.execute(query)
		res=self.__cursor.fetchall() 
		query="desc "+tbname
		self.__cursor.execute(query)
		fields=[]
		rows=self.__cursor.fetchall()
		for row in rows:
			fields.append(row[0])
		return fields,res
	def select(self, tbname, attr):
		query = 'Select * from '+str(tbname)+' where '
		for first,second in attr.iteritems():
			query +=str(first)+"='"+str(second)+"' and "
		query = query[:-4]
		self.__cursor.execute(query)
		res = self.__cursor.fetchall()
		query="desc "+tbname
		self.__cursor.execute(query)
		fields=[]
		rows=self.__cursor.fetchall()
		for row in rows:
			fields.append(row[0])
		return fields,res

