package Utils; 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import it.sauronsoftware.base64.Base64; 

public class Base64Utils {        
	/**      * 文件读取缓冲区大小      */      
	private static final int CACHE_SIZE = 1024;            
	/**      
	 *  <p>      
	 *   BASE64字符串解码为二进制数据      
	 *    </p>      
	 */      
	public static byte[] decode(String base64) throws Exception {          
		return Base64.decode(base64.getBytes());      
	}            
	/**      
	 *  <p>      
	 *   二进制数据编码为BASE64字符串      
	 *    </p>      
	 */
	public static String encode(byte[] bytes) throws Exception {          
		return new String(Base64.encode(bytes));      
	}            
	
	/**      
	 *  <p>      
	 *   将文件编码为BASE64字符串      
	 *    </p>      
	*/      
	public static String encodeFile(String filePath) throws Exception {          
		byte[] bytes = fileToByte(filePath);          
		return encode(bytes);      
	}            
	
	/**      
	 *  <p>      
	 *   BASE64字符串转回文件      
	 *   </p>     
	 */      
	public static void decodeToFile(String filePath, String base64) throws Exception {          
		byte[] bytes = decode(base64);          
		byteArrayToFile(bytes, filePath);      
	}            
	
	/**      
	 * <p>      
	 **文件转换为二进制数组     
	 * </p>      
	 */      
	public static byte[] fileToByte(String filePath) throws Exception {          
		byte[] data = new byte[0];          
		File file = new File(filePath);          
		if (file.exists()) {              
			FileInputStream in = new FileInputStream(file);           
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);     
			byte[] cache = new byte[CACHE_SIZE];         
			int nRead = 0;         
			while ((nRead = in.read(cache)) != -1) {          
				out.write(cache, 0, nRead);                
				out.flush();            
			}         
			out.close();         
			in.close();           
			data = out.toByteArray();      
		}        
		return data;   
	}            
	
	/**      * <p>      * 二进制数据写文件      * </p>      */      
	public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {     
		InputStream in = new ByteArrayInputStream(bytes);        
		File destFile = new File(filePath);       
		if (!destFile.getParentFile().exists()) {         
			destFile.getParentFile().mkdirs();      
		}     
		destFile.createNewFile();      
		OutputStream out = new FileOutputStream(destFile);  
		byte[] cache = new byte[CACHE_SIZE];         
		int nRead = 0;      
		while ((nRead = in.read(cache)) != -1) {     
			out.write(cache, 0, nRead);         
			out.flush();         
		}          
		out.close();    
		in.close();   
	}  
}