package Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogUtils extends Thread{
	static final int logInterval = 50000;  // 10s once
	private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	private BufferedWriter buff;
	
	public LogUtils (ConcurrentLinkedQueue<String> queue, BufferedWriter buff) {
		super();
		this.queue = queue;
		this.buff = buff;
	}
	
	@Override	
	public void run() {	
		Timer timer;
        timer = new Timer(true);
        timer.schedule(
                new java.util.TimerTask() {
                    public void run() {
                    	while(!queue.isEmpty()){				
                    		try {					
                    			buff.write(queue.poll());
                    			buff.newLine();
                    			buff.flush();		
                    		} catch (IOException e) {		
                    			e.printStackTrace();	
                    		}  	
                    	}
			}}, 0, logInterval);
	}

}
