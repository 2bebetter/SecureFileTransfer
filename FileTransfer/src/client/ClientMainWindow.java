package client;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import base.FileInfo;

// Todo 将建立连接部分移动到初始化的部分
public class ClientMainWindow{
	
	public static final String IP_ADDR = "localhost";
    public static final int PORT = 4321;
    
    static JFrame frameLogin;
	JFrame frameRegister;
	static JFrame frameFile;
	JScrollPane scrollPane;
    JPanel panelFile, panelLogin,panelRegister;
    JButton buttonRegister,buttonLogin,buttonUp,buttonCancel;
    JLabel labelUsernameLogin,labelPasswordLogin,labelUsernameRegister,labelPasswordRegister,labelServer;
    JTextField textFieldUsernameLogin,textFieldUsernameRegister, textFieldServer;
    JPasswordField passwordFieldLogin,passwordFieldRegister;
    
    private JTable table;
	private JButton uploadButton;
	private JButton flushButton;
    
    String username;
    String password;
    String[][] fileTable;
    static List<FileInfo> fileList;
    ProtocolClientSSL client;
     
    public ClientMainWindow() throws Exception {
    	client = new ProtocolClientSSL(IP_ADDR, PORT);
    	BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;//translucencyAppleLike;
    	org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
    	InitGlobalFont(new Font("微软雅黑", Font.PLAIN, 12));
    	UIManager.put("RootPane.setupButtonVisible", false);
    	
    	initFromCookie();
    }

	private void initFromCookie() throws FileNotFoundException {
		String status = client.loadCookie();
		frameFileInitialize();
    	frameLoginInitialize();
    	frameRegisterInitialize();
    	
		if(status != null) {
			frameFile.setVisible(true);
		}else {
			frameLogin.setVisible(true);
		}
		
	}

	private void frameFileInitialize() {
		
		fileList = new ArrayList<FileInfo>();
		
		frameFile = new JFrame();
		frameFile.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(ClientMainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/UpFolder.gif")));
		frameFile.setTitle("File Manage");
		frameFile.setBounds(450, 40, 480, 600);
		frameFile.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);		
				client.exit();
			}}); 

		frameFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panelFile = new JPanel();
		/* File list */
		scrollPane = new JScrollPane();
		scrollPane.setBounds(46, 75, 400, 450);
		panelFile.add(scrollPane);
		
		/* Upload and flush button */
		uploadButton = new JButton("Upload");
		uploadButton.setFocusPainted(false);
		uploadButton.setBounds(187, 25, 82, 23);
		uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println(fileList.size());
            	int result = 0;
    			String path;
    			JFileChooser fileChooser = new JFileChooser("ClientStorage/");
    			fileChooser.setDialogTitle("Select a file that you want to upload:");
    			fileChooser.setApproveButtonText("确认");
    			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    			result = fileChooser.showOpenDialog(null);
    			if (JFileChooser.APPROVE_OPTION == result) {
    				path = fileChooser.getSelectedFile().getPath();
    				System.out.println("path: " + path);
    				try {
    					String status = client.upload(path);
    					if(status.equals("invalid")) {
    						frameFile.setVisible(false);
    						frameLogin.setVisible(true);
    					}else if(status.equals("error")) {
    						JOptionPane.showMessageDialog(null, status);
    					}
    				} catch (Exception e1) {
    					e1.printStackTrace();
    				}
    			}
    			return;
    		}
        });

		flushButton = new JButton("Flush");
		flushButton.setFocusPainted(false);
		flushButton.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, Object> res = client.getDirectoryList();
				if(res.get("status").equals("invalid")) {
					frameFile.setVisible(false);
					frameLogin.setVisible(true);
				}else {
					if(res.get("fileList") != null) {
						fileList = (List<FileInfo>) res.get("fileList");
						String[][] fileTable=new String[fileList.size()][3];
						for(int row=0;row<fileList.size();row++) {
							fileTable[row][0]=fileList.get(row).getFilename();
						    fileTable[row][1]=fileList.get(row).getFilesize()+"";
						    fileTable[row][2]="download";
						}
						
						String[] columnNames = { "文件", "文件大小(字节)", "" };
						DefaultTableModel model = new DefaultTableModel();
						model.setDataVector(fileTable, columnNames);
				
						table = new JTable(model);
						scrollPane.setViewportView(table);
						table.setColumnSelectionAllowed(true);
						table.setCellSelectionEnabled(true);
						ButtonColumn buttonsColumn = new ButtonColumn(table, 2);
					}
				}
			}
			
		});
		flushButton.setBounds(312, 25, 82, 23);
		
		frameFile.getContentPane().add(panelFile);
		panelFile.setLayout(null);
		panelFile.add(uploadButton);
		panelFile.add(flushButton);
	}
	
	public void frameRegisterInitialize() {		
		frameRegister = new JFrame("Sign up");
		
		frameRegister.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelRegister = new JPanel();
        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(84, 150, 95, 29);
        buttonCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
        	frameRegister.setVisible(false);
        	frameLogin.setVisible(true);
        }});
        buttonUp = new JButton("Register");
        buttonUp.setBounds(226, 150, 95, 29);
        buttonUp.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					username = textFieldUsernameRegister.getText();
					password = passwordFieldRegister.getText();
					String status = client.register(username, password);
					JOptionPane.showMessageDialog(null, status);
					if(status.equals("success")) {
						frameRegister.setVisible(false);
						frameFile.setVisible(true);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
        	
        });
        
        labelUsernameRegister = new JLabel("Username");
        labelUsernameRegister.setBounds(59, 40, 90, 21);
        textFieldUsernameRegister = new JTextField(18);
        textFieldUsernameRegister.setBounds(174, 40, 168, 27);
        textFieldUsernameRegister.requestFocus();
        
        labelPasswordRegister = new JLabel("Password");
        labelPasswordRegister.setBounds(59, 90, 90, 21);
        passwordFieldRegister = new JPasswordField(18);
        passwordFieldRegister.setBounds(174, 90, 168, 27);
        
        frameRegister.getContentPane().add(panelRegister);
        panelRegister.setLayout(null);
        panelRegister.add(labelUsernameRegister);
        panelRegister.add(textFieldUsernameRegister);
        panelRegister.add(labelPasswordRegister);
        panelRegister.add(passwordFieldRegister);
        panelRegister.add(buttonUp);
        panelRegister.add(buttonCancel);
        
        frameRegister.setBounds(460, 180, 450, 270);
		
	}
	
	public void frameLoginInitialize() {
		frameLogin = new JFrame("Sign in");
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelLogin = new JPanel();
       
        labelUsernameLogin = new JLabel("Username");
        labelUsernameLogin.setBounds(59, 40, 90, 21);
        textFieldUsernameLogin = new JTextField(18);
        textFieldUsernameLogin.setBounds(174, 40, 168, 27);
        
        labelPasswordLogin = new JLabel("Password");
        labelPasswordLogin.setBounds(59, 90, 90, 21);
        passwordFieldLogin = new JPasswordField(18);
        passwordFieldLogin.setBounds(174, 90, 168, 27);
        
        buttonRegister = new JButton("Sign up");
        buttonRegister.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frameLogin.setVisible(false);
				frameRegister.setVisible(true);
			}
        });
        buttonRegister.setBounds(84, 150, 95, 29);
        buttonLogin = new JButton("Sign in");
        buttonLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					username = textFieldUsernameLogin.getText();
					password = passwordFieldLogin.getText();
					String status = client.login(username, password);
					JOptionPane.showMessageDialog(null, status);
					if(status.equals("success")) {
						frameLogin.setVisible(false);
						frameFile.setVisible(true);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
        	
        });
        buttonLogin.setBounds(226, 150, 95, 29);
        
        frameLogin.getContentPane().add(panelLogin);
        panelLogin.setLayout(null);
        panelLogin.add(labelUsernameLogin);
        panelLogin.add(textFieldUsernameLogin);
        panelLogin.add(labelPasswordLogin);
        panelLogin.add(passwordFieldLogin);
        panelLogin.add(buttonLogin);
        panelLogin.add(buttonRegister);
        frameLogin.setBounds(460, 180, 450, 270);
	}

	private static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}
	
	public class ButtonColumn extends AbstractCellEditor implements  
	    TableCellRenderer, TableCellEditor, ActionListener {  
			JTable table;  
			JButton renderButton;  
			JButton editButton;  
			String text;  
		
			public ButtonColumn(JTable table, int column) {  
			    super();  
			    this.table = table;  
			    renderButton = new JButton();  
			    editButton = new JButton();  
			    editButton.setFocusPainted(false);  
			    editButton.addActionListener(this);  
			
			    TableColumnModel columnModel = table.getColumnModel();  
			    columnModel.getColumn(column).setCellRenderer(this);  
			    columnModel.getColumn(column).setCellEditor(this);  
			}  
			
			public Component getTableCellRendererComponent(JTable table, Object value,  
			        boolean isSelected, boolean hasFocus, int row, int column) {  
			    if (hasFocus) {  
			        renderButton.setForeground(table.getForeground());  
			        renderButton.setBackground(UIManager.getColor("Button.background"));  
			    } else if (isSelected) {  
			        renderButton.setForeground(table.getSelectionForeground());  
			        renderButton.setBackground(table.getSelectionBackground());  
			    } else {  
			        renderButton.setForeground(table.getForeground());  
			        renderButton.setBackground(UIManager.getColor("Button.background"));  
			    }  
			
			    renderButton.setText((value == null) ? " " : value.toString());  
			    return renderButton;  
			}  
		
			public Component getTableCellEditorComponent(JTable table, Object value,  
			        boolean isSelected, int row, int column) {  
			    text = (value == null) ? " " : value.toString();  
			    editButton.setText(text);  
			    return editButton;  
			}  
			
			public Object getCellEditorValue() {  
			    return text;  
			}  
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				String file_name=fileList.get(table.getSelectedRow()).getFilename();
			    Long length = fileList.get(table.getSelectedRow()).getFilesize();
				int result = 0;
			    String path = null;  
			    JFileChooser fileChooser = new JFileChooser("ClientStorage/");  
			    FileSystemView fsv = FileSystemView.getFileSystemView();
			    fsv.createFileObject(file_name);
			    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    fileChooser.setDialogTitle("Select the folder:");  
			    result = fileChooser.showSaveDialog(null);
			    if (JFileChooser.APPROVE_OPTION == result) {  
			        path=fileChooser.getSelectedFile().getPath()+"\\";
			        System.out.println("path: "+path);
			        System.out.println("file_name:"+file_name);
			        String status = client.download(path, file_name, length);
					if(status.equals("invalid")) {
						frameFile.setVisible(false);
						frameLogin.setVisible(true);
					}else if(status.equals("error")) {
						JOptionPane.showMessageDialog(null, status);
					}
			      }     
			}  
	}
	
	public static void main(String[] args) {
        try {
        	EventQueue.invokeLater(new Runnable() {
    			public void run() {
    				try {
    					new ClientMainWindow();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}