package Tokenizer;

import java.io.IOException;
import java.io.InputStreamReader;

public class MessageThTokenizer implements ThTokinzer {

	public final char _delimiter;
	private final InputStreamReader _isr;
	private boolean _closed;

	public MessageThTokenizer(InputStreamReader isr, char delimiter) {
		_delimiter = delimiter;
		_isr = isr;
		_closed = false;
	}

	public synchronized String[] nextToken() throws IOException {
		if (!isAlive())
			throw new IOException("tokenizer is closed");

		String ans = null;
		try {
			// we are using a blocking stream, so we should always end up
			// with a message, or with an exception indicating an error in
			// the connection.
			int c;
			String command = "";
			boolean checkCommand = true;
			StringBuilder sb = new StringBuilder();
			// read char by char, until encountering the framing character, or
			// the connection is closed.
			while ((c = _isr.read()) != -1) {
				if (c == _delimiter)
					break;
				else {
					if ((char) c == ' ' && checkCommand) {

						command = sb.toString();
						sb.delete(0, sb.length());
						checkCommand = false;

					} else {
						sb.append((char) c);

					}

				}
			}
			ans = sb.toString();
			String[] commandAndParamter = new String[2];
			commandAndParamter[0] = command;
			commandAndParamter[1] = ans;
			return commandAndParamter;
		} catch (IOException e) {
			_closed = true;
			throw new IOException("Connection is dead");
		}

	}

	
	
	
	public boolean isAlive() {
		return !_closed;
	}

}