package client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.*;

public class ProtocolClient{
    
	/*
	 * server doesn't need these beacuse server has many sockets
	 */    
    
	private String cookie;
    private Socket socket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    
    String publicKey;
    char[] token;
    
    public ProtocolClient(String host, int port) throws Exception {
    	socket = new Socket(host, port);		
		
		dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }
    
    public void sendMessage(Request msg, Socket socket) throws IOException {
		byte[] data = new byte[1024*1024];		
		ByteArrayOutputStream bs = new ByteArrayOutputStream();		
		ObjectOutputStream bo = new ObjectOutputStream(bs);		
		bo.writeObject(msg);
		data = bs.toByteArray();
		
		OutputStream out = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(out);
        DataOutputStream dos = new DataOutputStream(bos);
        
		dos.write(data);
		dos.flush();
	}
    
	public Request receiveMessage(byte[] DataInput) throws ClassNotFoundException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(DataInput);		
			ObjectInputStream os;
			os = new ObjectInputStream(bis);
			return (Request)os.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
    
	@SuppressWarnings("unchecked")
	public List<FileInfo> receiveFileList(byte[] DataInput) throws ClassNotFoundException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(DataInput);		
			ObjectInputStream os;
			os = new ObjectInputStream(bis);
			return (List<FileInfo>)os.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public String register(String username, String password){ 
		try{ 
			String temp = username + "&" + password;
			Request message = new Request(Command.REGISTER, temp.length(), null, temp.toCharArray());
    		sendMessage(message, socket);
    		int status = dis.readInt();
    		if(status == 10) {
    			cookie = dis.readUTF();
    			saveCookie(cookie);
    			return "success";
    		}
    		return "error";
		}catch(Exception e) { 
			e.printStackTrace(); 
		} 
		return "error"; 
	}
    
	public String login(String username, String password){ 
		try{ 
			String temp = username + "&" + password;
			Request message = new Request(Command.LOGIN, temp.length(), null, temp.toCharArray());
    		sendMessage(message, socket);
    		int status = dis.readInt();
    		if(status == 10) {
    			cookie = dis.readUTF();
    			saveCookie(cookie);
    			return "success";
    		}
    		return "error";
		}catch(Exception e) { 
			e.printStackTrace(); 
		}
		return "error";
	}
    
    public String upload(String path) {
    	try{
    		File f = new File(path);
    		Request message = new Request(Command.UPLOAD_FILE, f.length(), cookie.toCharArray(), f.getName().toCharArray());
    		sendMessage(message, socket);
    		int status = dis.readInt();
    		if(status == 0)
    			return "invalid";
    		else if(status == 10) {
    			byte[] buffer = new byte[1024];
				int len = 0;
				FileInputStream fis = new FileInputStream(path);
				while((len = fis.read(buffer, 0, buffer.length)) != -1) {
					dos.write(buffer, 0, len);
				}
				dos.flush();
				fis.close();
				return "success";
			}else
    			return "error";
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		return "error";
    }
    
    public String download(String path, String filename, Long length) {
    	try{
    		Request message = new Request(Command.DOWNLOAD_FILE, length, cookie.toCharArray(), filename.toCharArray());
    		sendMessage(message, socket);
    		int status = dis.readInt();
    		if(status == 0)
    			return "invalid";
    		else if(status == 10) {
	    			byte[] buffer = new byte[4096];
	    			int r = 0, rr = 0;
	    			File file = new File(path + filename);
	    			FileOutputStream fos = new FileOutputStream(file);
	    			BufferedOutputStream bos = new BufferedOutputStream(fos);
	    			while (r < length) {
	    				if (length - r >= buffer.length) {
	    					rr = dis.read(buffer, 0, buffer.length);
	    				} else {
	    					rr = dis.read(buffer, 0, (int) (length - r));
	    				}
	    				r = r + rr;
	    				bos.write(buffer, 0, rr);
	    			}
	    			bos.close();
	    			fos.close();
	    			return "success";
    		}else
    			return "error";
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return "error";
    }
    
    public Map<String, Object> getDirectoryList() {
    	try {
    		Map<String, Object> res = new HashMap<String, Object>();
    		List<FileInfo> fileList;
    		Request msg = new Request(Command.FILE_LSIT, cookie.toCharArray().length, cookie.toCharArray(), null);
    		sendMessage(msg, socket);
    		int status = dis.readInt();
    		if(status == 1) {
    			byte[] DataInput = new byte[4096];
    			dis.read(DataInput);
    			fileList = receiveFileList(DataInput);
    			res.put("status", "success");
    			res.put("fileList", fileList);
    			return res;
    		}else {
    			res.put("status", "invalid");
    			return res;
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public void saveCookie(String cookie) {
    	try {
			File file = new File("ClientStorage/cookie1.txt");
			if(file.exists()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				bw.write(cookie);
				bw.close();
			}else {
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				bw.write(cookie);
				bw.close();
			}
			token = cookie.toCharArray();
		}catch(Exception e) {
			e.printStackTrace();
		}
    }

	public String loadCookie() {
		try {
			File file = new File("ClientStorage/cookie1.txt");
			if(file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				cookie = br.readLine();
				br.close();
				return cookie;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public void exit() {
		try {
			if(socket != null) {
				Request msg = new Request(Command.QUIT, cookie.toCharArray().length, cookie.toCharArray(), null);
	    		sendMessage(msg, socket);
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
