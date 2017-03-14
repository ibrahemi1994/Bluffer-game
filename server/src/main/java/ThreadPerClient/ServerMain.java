package ThreadPerClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import Tokenizer.MessageThTokenizer;
import Tokenizer.MessageTokenizer;
import Tokenizer.ThTokinzer;
import Tokenizer.Tokenizer;
import encoding.Encoder;
import encoding.SimpleEncoder;
import threadperClientprotocol.CallBackProtocolServer;
import threadperClientprotocol.Manager;

public class ServerMain {
	 public static void main(String[] args) 
		        throws NumberFormatException, IOException 
		    {
				
		        /*The characteristic of the server concurrency model is determined by the selected implementation for the scm 
		        instance (SingleThread, ThreadPerClient or ThreadPool - as described in the next sections)*/
		 Encoder encoder = new SimpleEncoder();
		        ServerConcurrencyModel scm = new ThreadPerClient(); 
		        ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]));
		        Manager protocol = new Manager();
		        System.out.println("Listening.........");
		        
		        while (true) {
		            Socket s = socket.accept();
		            System.out.println("Connection Accepted from "+s.toString());
		            ThTokinzer tokenizer = new MessageThTokenizer(new InputStreamReader(s.getInputStream()),'\n');
		            Runnable connectionHandler = new ConnectionHandler(s,encoder,  tokenizer, protocol , new CallBackProtocolServer(s));
		            scm.apply(connectionHandler);
		        }
		        
		    }
}
