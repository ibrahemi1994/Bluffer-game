package threadperClientprotocol;



public interface ServerProtocol<T> {

	 /**
	    * determine whether the given message is the termination message
	    * @param msg the message to examine
	    * @return true if the message is the termination message, false otherwise
	    */
	    boolean isEnd(String msg);
	 
	    /**
	     * @return true if the connection should be terminated
	     */
	    boolean shouldClose();
	    
	    /**
	     * called when the connection was not gracefully shut down.
	     */
	    void connectionTerminated();
	    

	    /**
	     * 
	     * @param com - receiving an command and proccesing it
	     */
	    void processMessage(T msg, ProtocolCallback<T> callback);
		
		
	
}
