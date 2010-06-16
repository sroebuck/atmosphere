package org.atmosphere.container;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.cpr.WebSocketProcessor;
import org.atmosphere.util.LoggerUtils;
import org.atmosphere.websocket.WebSocketSupport;

import com.caucho.servlet.WebSocketContext;
import com.caucho.servlet.WebSocketListener;
import com.caucho.servlet.WebSocketServletRequest;

public class ResinWebSocketServletRequest implements WebSocketListener, WebSocketSupport {
	
	private final WebSocketProcessor webSocketProcessor;
	private final HttpServletRequest request;
    private WebSocketContext context;

	protected final Logger logger = LoggerUtils.getLogger();
	
	public ResinWebSocketServletRequest(ResinWebSocketSupport support, HttpServletRequest request){
		this.request = request;
        this.webSocketProcessor = new WebSocketProcessor(support.getAtmosphereConfig().getServlet(), this);        
	}
	
	public WebSocketContext startWebSocket() {
		return ((WebSocketServletRequest)request).startWebSocket(this);
	}
	
	@Override
	public void onComplete(WebSocketContext context) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("onComplete");
	}

	@Override
	public void onRead(WebSocketContext context) throws IOException {
		System.out.println("onRead");
        StringBuilder sb = new StringBuilder();

        this.context = context;

        int ch = context.getInputStream().read();
        while ((ch = context.getInputStream().read()) >= 0 && ch != 0xff) {
          sb.append((char) ch);
        }

        try {
            webSocketProcessor.broadcast((byte) 0x00, sb.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
	}

	@Override
	public void onStart(WebSocketContext context) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("onStart");
		
		// sets the connection timeout to 120s
		context.setTimeout(120000);
		
        try {
            webSocketProcessor.connect(request);
        } catch (IOException e) {
            logger.log(Level.WARNING, "", e);
        }
        
	}

	@Override
	public void onTimeout(WebSocketContext arg0) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("onTimeout");
	}

	@Override
	public void close() throws IOException {
       context.complete();
	}

	@Override
	public void redirect(String location) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(byte frame, String data) throws IOException {
        context.getOutputStream().write(0x00);
        context.getOutputStream().write(data.getBytes());
        context.getOutputStream().write(0xff);
        context.getOutputStream().flush();        
	}

	@Override
	public void write(byte frame, byte[] data) throws IOException {
		context.getOutputStream().write(data);
	}

	@Override
	public void write(byte frame, byte[] data, int offset, int length)
			throws IOException {
		context.getOutputStream().write(data,offset,length);
	}

	@Override
	public void writeError(int errorCode, String message) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
}
