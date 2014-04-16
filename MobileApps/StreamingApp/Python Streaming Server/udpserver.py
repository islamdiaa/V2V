import SocketServer
import ossaudiodev
import sys

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        print data
		
if __name__ == "__main__":
	speaker=ossaudiodev.open('w')
	fmt=ossaudiodev.AFMT_S16_LE
	speaker.setparameters(fmt,1,44100)
	HOST, PORT = "localhost",6666

    # Create the server, binding to localhost on port 9999
	server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
	server.serve_forever()
