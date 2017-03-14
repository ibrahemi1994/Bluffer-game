package threadperClientprotocol;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CallBackProtocolServer implements ProtocolCallback {
	private PrintWriter out;
	Socket socket_;
	
	public CallBackProtocolServer(Socket socket_) {
      this.socket_ = socket_;
      
	}
	public Socket getSocket_() {
		return socket_;
	}
	
	@Override
	public void sendMessage(Object msg) throws IOException {
	byte[] bytes= (msg.toString()+'\n').getBytes();
	socket_.getOutputStream().write(bytes);
		
	}

	
	
	
}
