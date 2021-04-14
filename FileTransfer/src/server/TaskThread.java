package server;

import java.net.Socket;

import server.ProtocolServer;

public class TaskThread implements Runnable{
	private Socket socket = null;
	private ProtocolServer protocol = null;
	
	public TaskThread(ProtocolServer protocol) {
		this.protocol = protocol;
	}
	
	public boolean isIdle() {
        return socket == null;
    }
	
	public synchronized void run() {
		protocol.service();
	}
}
