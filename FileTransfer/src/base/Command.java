package base;

public enum Command{
	FILE_LSIT((byte)0, "fileList"), UPLOAD_FILE((byte)1, "uploadFile"), DOWNLOAD_FILE((byte)2, "downloadFile"), QUIT((byte)3, "quit"), REGISTER((byte)4, "register"), LOGIN((byte)5, "login");
	
	private byte command;
	private String msg;
	
	Command(byte cmd, String msg){
		this.command = cmd;
		this.msg = msg;
	}
	/*LOGIN,
	NOTMATCH,
	REGISTER,
	USERALREADYEXISTS,
	UPLOAD,
	FILEALREADYEXISTS,
	DOWNLOAD,
	FILENOTEXISTS,
	GETDIRECTORYLIST,
	LOG,
	SUCCESS,
	SERVERSTOP,   server stop 
	QUIT,  user quit 
	ERROR*/
};
