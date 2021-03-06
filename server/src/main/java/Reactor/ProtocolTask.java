package Reactor;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import Tokenizer.MessageTokenizer;
import reactorProtocol.CallBackProtocolServer;
import reactorProtocol.ProtocolCallback;
import reactorProtocol.ServerProtocol;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<T> implements Runnable {

	private final ServerProtocol<T> _protocol;
	private final MessageTokenizer<T> _tokenizer;
	private final ConnectionHandler<T> _handler;
	private ProtocolCallback _callback;

	public ProtocolTask(final ServerProtocol<T> protocol, final MessageTokenizer<T> tokenizer,
			final ConnectionHandler<T> h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
		_callback = new CallBackProtocolServer(_handler, _tokenizer);
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
		// go over all complete messages and process them.
		while (_tokenizer.hasMessage()) {
			T msg = _tokenizer.nextMessage();
			this._protocol.processMessage(msg, _callback);

		}
	}

	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}
