package reactorProtocol;

import java.util.Date;
 
public class LinePrintingProtocol implements MessagingProtocol {
 
    private boolean _shouldClose;
    private int _lineNumber;
 
    public LinePrintingProtocol() {
        _shouldClose = false;
        _lineNumber = 0;
    }
 
    public boolean shouldClose() {
        return _shouldClose;
    }
 
    public void connectionTerminated() {
        _shouldClose = true;
    }
 
    public String processMessage(String msg) {
        String ans = null;
 
        if (msg != null) {
            if (isEnd(msg))
                _shouldClose = true;
            else {
                System.out.println("Message " + _lineNumber + ":" + msg);
                ans = new Date().toString() + ": printed\n";
            }
        }
        return ans;
    }
 
    public boolean isEnd(String msg) {
        return msg.equalsIgnoreCase("bye");
    }
}