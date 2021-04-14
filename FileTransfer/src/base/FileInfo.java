package base;

import java.io.Serializable;

public class FileInfo implements Serializable{
	
	private String filename;
	private Long FileSize;
	
	public FileInfo(String filename, Long FileSize) {
		this.filename = filename;
		this.FileSize = FileSize;
	}
	
	public FileInfo() {
		
	}
	
	@Override
	public String toString() {
		return "[ filename: " + filename + ", fileSize: " + FileSize + " ]";

	}

	public String getFilename() {
		return this.filename;
	}
	
	public Long getFilesize() {
		return this.FileSize;
	}

	public void setFilename(String newName) {
		this.filename = newName;
	}
	
	public void setFileSize(Long FileSize) {
		this.FileSize = FileSize;
	}
}
