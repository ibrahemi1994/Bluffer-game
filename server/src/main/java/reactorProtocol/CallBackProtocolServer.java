package reactorProtocol;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

import Reactor.ConnectionHandler;
import Tokenizer.MessageTokenizer;
import Tokenizer.StringMessage;


/**
 * {@link ProtocolCallback}
 * @author ibrahemi
 */
public class CallBackProtocolServer<T> implements ProtocolCallback {
	ConnectionHandler<T> _handler;
	 MessageTokenizer<T> _tokenizer;
	public CallBackProtocolServer(ConnectionHandler<T> _handler,MessageTokenizer<T> _tokenizer) {
    this._handler=_handler;
    this._tokenizer=_tokenizer;
    
      
	}
	/**
	* @param msg message to be sent
	* @throws IOException if the message could not be sent , or if the
	connection to this client has been closed .
	*/
	public void sendMessage(Object msg) throws IOException {

	    ByteBuffer bytes = _tokenizer.getBytesForMessage(new StringMessage (msg.toString()));
	    this._handler.addOutData(bytes);
	}

	
	
	
}
