package base;

import java.io.Serializable;

public class Request implements Serializable{
	
	private Command command;
	long length;
	char[] token = new char[64];
	char[] URI = new char[64];
	
	public Request() {}
	
	public Request(Command command, int length, char[] token) {
		super();
		this.command = command;
		this.length = length;
		this.token = token;
	}
	
	public Request(Command command, long l, char[] token, char[] uri) {
		super();
		this.command = command;
		this.length = l;
		this.token = token;
		this.URI = uri;
	}
	
	public Command getCommand() {
		return command;
	}

	public Long getLength() {
		return length;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public void setLength(Integer length) {
		this.length = length;
	}
	
	public char[] getToken() {
		return token;
	}
	
	public char[] getURI() {
		return URI;
	}
 
}
