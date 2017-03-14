package ThreadPerClient;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Vector;

import Tokenizer.ThTokinzer;
import Tokenizer.Tokenizer;
import encoding.Encoder;
import threadperClientprotocol.Manager;
import threadperClientprotocol.ProtocolCallback;

public class ConnectionHandler implements Runnable {

	private final Socket _socket;
	private final Encoder _encoder;
	private final ThTokinzer _tokenizer;
	private final Manager _protocol;
	protected Vector<ByteBuffer> _outData = new Vector<ByteBuffer>();
	private final ProtocolCallback _callback;


	public ConnectionHandler(Socket s, Encoder encoder, ThTokinzer tokenizer, Manager protocol,
			ProtocolCallback callback) {
		_socket = s;
		_encoder = encoder;
		_tokenizer = tokenizer;
		_protocol = protocol;
		_callback = callback;
	}

	public void run() {
		
		while (!_protocol.shouldClose() && !_socket.isClosed()) {
			if (!_tokenizer.isAlive()) {
				_protocol.connectionTerminated();
			}
			else {
				if(!_protocol.shouldClose() )
				_protocol.processMessage(_tokenizer, _callback);

			}
		}
		
		try {
			_socket.close();
		} catch (IOException ignored) {
		}
		System.out.println("thread done");
	}
}
