package server;

import java.net.Socket;

public class TaskThreadSSL implements Runnable{
	private Socket socket = null;
	private ProtocolServerSSL protocol = null;
	
	public TaskThreadSSL(ProtocolServerSSL protocolServerSSL) {
		this.protocol = protocolServerSSL;
	}
	
	public boolean isIdle() {
        return socket == null;
    }
	
	public synchronized void run() {
		protocol.service();
	}
}
