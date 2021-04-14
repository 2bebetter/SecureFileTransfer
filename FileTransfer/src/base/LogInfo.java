package base;

import java.util.Date;

public class LogInfo {
	private Date logDate;
	private String type;
	/*type: upload, download, delete*/
	private FileInfo file;
	
	public void setlogDate(Date date) {
		this.logDate = date;
	}
	
	public void setType(String t) {
		this.type = t;
	}
	
	public void setFile(FileInfo f) {
		this.file = f;
	}

	@Override
	public String toString() {
		if(file != null)
			return "Log [date: " + logDate.toString() + ", type: " + type + ", file: "+ file.toString() + "]\n";
		else	
			return "Log [date: " + logDate.toString() + ", type: " + type + "]\n";
	}
}
