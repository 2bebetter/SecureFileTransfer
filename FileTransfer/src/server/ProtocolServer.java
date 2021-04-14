package server;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import base.LogInfo;
import base.Request;
import base.FileInfo;
import Utils.*;

public class ProtocolServer{
	public static long lastTime;
	private String storageFolder = "ServerStorage/";
	private Socket socket;
	
	public ProtocolServer(Socket socket) {
		this.socket = socket;
	}
	
	public void insertUser(String[] user) {
		try {
			String id = UUIDUtils.getRandomString(16);
			String sql = "insert into user values('" + id + "','" + 
			user[0] + "','" + user[1] + "'," + TimeUtils.getCurrentDate().getTime() + ")";
			System.out.println(sql);
			PreparedStatement prepareStatement = FileServer.con.prepareStatement(sql);
	        prepareStatement.execute();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateLastDate(long time, String username) {
		try {
			String sql = "update user set last_time = '" + time + "' where username = '" + username + "'";
			System.out.println(sql);
			PreparedStatement prepareStatement = FileServer.con.prepareStatement(sql);
	        prepareStatement.execute();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkUsername(String username) {
        try {
        	String sql = "select * from user";
			PreparedStatement prepareStatement = FileServer.con.prepareStatement(sql);
			ResultSet rs = prepareStatement.executeQuery();
            while (rs.next()) {
                if(username.equals(rs.getString("username"))) {
                	return false;
                }
            }
            return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean checkPassword(String[] user) {
	    try {
        	String sql = "select * from user";
			PreparedStatement prepareStatement = FileServer.con.prepareStatement(sql);
            ResultSet rs = prepareStatement.executeQuery();
            while (rs.next()) {
                if(user[0].equals(rs.getString("username")) && user[1].equals(rs.getString("password"))) {
                	return true;
                }
            }
            return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String generateToken(String username) {
		try {
			Date date = TimeUtils.getCurrentDate();
			String strDate = TimeUtils.getStringByDate(date);
			
			setLastTime(date);
			
			System.out.println(strDate);
			
			String temp = username + "&" + strDate;
			
			return Base64Utils.encode(temp.getBytes("UTF-8"));	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void setLastTime(Date date) {
		lastTime = date.getTime();
	}

	public boolean checkValidToken(String token) {
		try {
			String temp = new String(Base64Utils.decode(token));
			System.out.println("token : " + temp);
			String strDate = temp.split("&")[1];
			if(TimeUtils.getCurrentDate().getTime() - TimeUtils.stringToDate(strDate).getTime() > 1000 * 60 * 24 * 24 * 3) {
				// if timestamp > 3 days
				return false;
			}else {
				if(TimeUtils.getCurrentDate().getTime() - lastTime > 1000 * 60 * 24 * 24 * 1) {
					setLastTime(TimeUtils.getCurrentDate());
					updateLastDate(TimeUtils.getCurrentDate().getTime(), temp.split("&")[0]);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
			return ((Request)os.readObject());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public void service() {
		try {
			
			InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedInputStream bis = new BufferedInputStream(in);
            BufferedOutputStream bos = new BufferedOutputStream(out);

            DataInputStream dis = new DataInputStream(bis);
            DataOutputStream dos = new DataOutputStream(bos);
            
            Request request;
            String uri, token;
            byte[] DataInput;
            String[] user;
            
            while (true) {
            	
            	DataInput = new byte[4096];
            	dis.read(DataInput);
            	request = receiveMessage(DataInput);
            	if(request != null) {
            		switch(request.getCommand()) {
            		
                    case FILE_LSIT:
                    	token = new String(request.getToken());
                    	sendDirectoryList(dos, token);
                    	break;
                    case UPLOAD_FILE:
                    	token = new String(request.getToken());
                    	uri = new String(request.getURI());
                        uploadFile(dos, dis, token, uri, request.getLength());
                    	break;
					case DOWNLOAD_FILE:
						token = new String(request.getToken());
                    	uri = new String(request.getURI());
	                    downloadFile(dos, token, uri, request.getLength());
						break;
					case REGISTER:
						user = new String(request.getURI()).split("&");
						if(checkUsername(user[0])) {
							dos.writeInt(10);
							insertUser(user);
							token = generateToken(user[0]);
							dos.writeUTF(token);
							dos.flush();
						}else {
							dos.writeInt(11);
							dos.flush();
						}
						break;
						
					case LOGIN:
						user = new String(request.getURI()).split("&");
						if(checkPassword(user)) {
							dos.writeInt(10);
							token = generateToken(user[0]);
							dos.writeUTF(token);
							dos.flush();
						}else {
							dos.writeInt(11);
							dos.flush();
						}
						break;
					case QUIT:
						if(socket != null)
							socket.close();
						break;
					default:
						break;
                	}
            	}    
            }
		}catch(Exception e) {
			try {
                if (socket != null)
                    socket.close();
            } catch (Exception e1) {
            }
		}
	}
	
	private void uploadFile(DataOutputStream dos, DataInputStream dis, String token, String filename, Long length) throws IOException {
		try{
			System.out.println("upload");
			if(checkValidToken(token)) {
				System.out.println(checkFilename(filename));
				if(checkFilename(filename)) {
					dos.writeInt(10);
					dos.flush();
					
					File file = new File(this.storageFolder + filename);
					FileOutputStream fos = new FileOutputStream(file);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int r = 0;
					int rr = 0;
					byte[] buffer = new byte[4096];
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
					System.out.println("upload file success!");
					
					LogInfo logInfo = new LogInfo();
			        logInfo.setlogDate(new Date());
			        logInfo.setType("Upload");
			        logInfo.setFile(new FileInfo(filename, length));
			        FileServer.logQueue.add(logInfo.toString());
				}else {
					dos.writeInt(11);
					dos.flush();
				}
			}else {
				dos.writeInt(0);
				dos.flush();
			}
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}

	private void downloadFile(DataOutputStream dos, String token, String filename, Long length) {
		try{
			if(checkValidToken(token)) {
				File file = new File(this.storageFolder + filename);
				System.out.println(file.getAbsolutePath());
		        if(!file.exists()) {
		        	dos.writeInt(11);
		        	dos.flush();
		        	return;
		        }else {
		        	dos.writeInt(10);
		        	dos.flush();
		        	FileInputStream fis = new FileInputStream(file);
			        byte[] buffer = new byte[4096];
			        while((fis.read(buffer, 0, buffer.length))!=-1){  
			            dos.write(buffer);  
			            dos.flush();
			        }
			        fis.close();
			        System.out.println("download file success!");
			        
			        LogInfo logInfo = new LogInfo();
			        logInfo.setlogDate(new Date());
			        logInfo.setType("Download");
			        logInfo.setFile(new FileInfo(filename, length));
			        FileServer.logQueue.add(logInfo.toString());
		        }
			}else {
				dos.writeInt(0);
				dos.flush();
			}
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}

	private void sendDirectoryList(DataOutputStream dos, String token) throws IOException {
		
		if(checkValidToken(token)) {
			dos.writeInt(1);
			File[] list=new File(storageFolder).listFiles();
			List<FileInfo> fileList = new ArrayList<FileInfo>();
			FileInfo temp;
	        for (File file : list) {
	        	if (file.isFile()) {
	        		temp = new FileInfo(file.getName(), file.length());
	        		fileList.add(temp);
	        	}
	        }
	        byte[] data = new byte[4096];		
			ByteArrayOutputStream bs = new ByteArrayOutputStream();		
			ObjectOutputStream bo = new ObjectOutputStream(bs);		
			bo.writeObject(fileList);
			data = bs.toByteArray();
	        
			dos.write(data);
			dos.flush();
			
			System.out.println("Send DirectoryList Success!");
			
			LogInfo logInfo = new LogInfo();
	        logInfo.setlogDate(new Date());
	        logInfo.setType("DirectoryList");
	        logInfo.setFile(null);
	        System.out.println(logInfo.toString());
	        FileServer.logQueue.add(logInfo.toString());
		}else {
			dos.writeInt(1);
			dos.flush();
		}
	}

	private boolean checkFilename(String uri) {
		File[] list=new File(storageFolder).listFiles();
		if (list == null) {
            return true;
        }
		for (File file : list) {
        	if (file.isFile()) {
        		if(file.getName().equals(uri))
        			return false;
        	}
        }
		return true;
	}
}