package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import Utils.LogUtils;

public class FileServerSSL {
	public static final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
	public static final String IP_ADDR = "localhost";
    public static final int PORT = 4321;
	public static Connection con;
	
	public static Connection connectSQL(String user, String password) {
		Connection connection = null;
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        String url = "jdbc:mysql://127.0.0.1:3306/filetransfer?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
	        connection = DriverManager.getConnection(url, user, password); 
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return connection;
	}
	
	public static void main(String args[]) throws Exception {
		try {
			String path = System.getProperty("user.dir");
        	System.setProperty("javax.net.debug", "ssl,handshake"); 			
    		System.setProperty("javax.net.ssl.keyStore", path + "\\certs\\server.jks");			
    		System.setProperty("javax.net.ssl.keyStorePassword", "123456");			
    		System.setProperty("javax.net.ssl.trustStore", path + "\\certs\\clienttrust.jks");			
    		System.setProperty("javax.net.ssl.trustStorePassword", "123456"); 			
    		SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();			
    		SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(PORT);			
    		serverSocket.setNeedClientAuth(true);
        	System.out.println("Server on...");
        	
        	File file = new File("log/log.txt");
    		if(!file.exists()) {
    			file.createNewFile();
    		}
    		FileWriter fw = new FileWriter(file);
    		BufferedWriter bw = new BufferedWriter(fw);
    		LogUtils log = new LogUtils(logQueue, bw);
    		log.run();
    		
    		con = connectSQL(args[0], args[1]);
    		
    		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS, 
                    new ArrayBlockingQueue<Runnable>(5));
    		while(true) {
    			SSLSocket socket = (SSLSocket) serverSocket.accept();
    			executor.execute(new TaskThreadSSL(new ProtocolServerSSL(socket)));
    		}
    		 
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
	}
}
