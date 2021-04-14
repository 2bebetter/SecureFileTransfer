package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import Utils.LogUtils;

public class FileServer {
	public static final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
	public static final String IP_ADDR = "localhost";
    public static final int PORT = 4321;
	public static Connection con;
	
	public static Connection connectSQL() {
		Connection connection = null;
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        String url = "jdbc:mysql://127.0.0.1:3306/filetransfer?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
	        String user = "root";
	        String password = "mimashi123456";
	        connection = DriverManager.getConnection(url, user, password); 
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return connection;
	}
	
	public static void main(String args[]) throws Exception {
		try {
			
        	System.out.println("Server on...");
        	
        	File file = new File("log/log.txt");
    		if(!file.exists()) {
    			file.createNewFile();
    		}
    		FileWriter fw = new FileWriter(file);
    		BufferedWriter bw = new BufferedWriter(fw);
    		LogUtils log = new LogUtils(logQueue, bw);
    		log.run();
    		
    		con = connectSQL();
    		
    		@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(PORT);
    		
    		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS, 
                    new ArrayBlockingQueue<Runnable>(5));
    		while(true) {
    			Socket socket = serverSocket.accept();
    			executor.execute(new TaskThread(new ProtocolServer(socket)));
    		}
    		 
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
	}
}
