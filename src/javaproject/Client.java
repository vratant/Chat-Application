package javaproject;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
@SuppressWarnings("unused")
public class Client extends Thread implements ActionListener 
{
	String history = "";
	ChatMessage myObject;
	boolean sendingdone = false, receivingdone = false;
	Socket connectivity;
	ObjectOutputStream myOutputStream;
	ObjectInputStream myInputStream;
	Frame f;
	Frame f1;
	TextArea ta;
	BufferedWriter fileWrite;
	private JPanel layout1 = new JPanel();
	private JPanel layout2 = new JPanel();
	private JButton connectBtn = new JButton();
	private JButton disconnectBtn = new JButton();
	private JButton userlistBtn = new JButton();
	private JButton messageHistoryBtn = new JButton("Message History");
	private JTextField nameField = new JTextField();
	private JTextField tf = new JTextField();
	public Client() 
	{
		f = new Frame();
		f.setSize(600, 550);
		f.setLayout( new BorderLayout() );
		layout1.setLayout( new GridLayout( 1, 4 ) );
		layout2.setLayout( new GridLayout( 1, 4 ) );
		f.add( layout1, BorderLayout.SOUTH);
		f.add( layout2, BorderLayout.NORTH );
		f.setTitle("Chat Client");
		f.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent we) 
			{
				System.exit(0);
			}
		});
		
		disconnectBtn = new JButton("Disconnect");//disconnect-button
		layout2.add( disconnectBtn );
		disconnectBtn.addActionListener(new ActionListener() 
		{
		@Override
			public void actionPerformed(ActionEvent e) 
			{
			String str = e.getActionCommand();
				try
				{
					if( str.equals("Disconnect"))
					{
						disconnectSignal();
						connectivity.close();
						System.exit(0);
					}
				}
				catch (Exception ee) 
				{
				}
			}
		});
		userlistBtn = new JButton("Userlist");//user-list
		layout1.add( userlistBtn );
		userlistBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				getUserList();
			}
		});	
		connectBtn = new JButton("Connect");//connect-button
		layout2.add( connectBtn );
		connectBtn.addActionListener(new ActionListener() 
		{
		@Override
			public void actionPerformed(ActionEvent e) 
			{
			String str = e.getActionCommand();
			try 
			{
				if (str.equals("Connect")) 
				{
					toConnect();
				}
				connectBtn.setVisible(false);
			}
			catch (Exception ee) 
			{
			}
		}
	});
		messageHistoryBtn = new JButton("History");//history
		layout1.add(messageHistoryBtn);
		messageHistoryBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					writeToFile();
				}
				catch(IOException eee)
				{
				}
			}
		});
		nameField = new JTextField("");
		layout2.add(nameField);
		nameField.requestFocus();
		tf = new JTextField();
		tf.addActionListener(this);
		layout1.add(tf);
		ta = new TextArea();
		f.add(ta, BorderLayout.CENTER);
		f.setVisible(true);
	}
		void toConnect() throws Exception 
		{
			nameField.setVisible(false);
			f.setTitle(nameField.getText());
			connectivity = new Socket("afsaccess2.njit.edu", 8181);
			myOutputStream = new ObjectOutputStream(connectivity.getOutputStream());
			myInputStream = new ObjectInputStream(connectivity.getInputStream()); 
			connected();
			start();
		}
		public void actionPerformed(ActionEvent ae) 
		{
			myObject = new ChatMessage();
			myObject.setName(nameField.getText());
			myObject.setMessage(tf.getText());
			tf.setText("");
			try 
			{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			} 
			catch (IOException ioe) 
			{
			}
		}
		public void run()  
		{
			try 
			{ 
				while (!receivingdone) 
				{
					myObject = (ChatMessage) myInputStream.readObject();
					if(myObject.getName().equals("Users Currently in Chatroom : "))
					{
						ta.append(myObject.getName() + " \n" + myObject.getMessage() + "\n");
					}
					else
					{
						ta.append(myObject.getName() + " : " + myObject.getMessage() + "\n");
						storeMessage(myObject.getName(), myObject.getMessage());
					}
				}
			} 
			catch (IOException ioe) 
			{
				System.out.println( ioe.getMessage());
			} 
			catch (ClassNotFoundException cnf) 
			{
				System.out.println(cnf.getMessage());
			}
		}
		void connected()
		{
			myObject = new ChatMessage();
			myObject.setName(nameField.getText());
			myObject.setMessage("Is Online");
			try 
			{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			} 
			catch (IOException ioe) 
			{
			}
		}
		void getUserList()
		{
			myObject = new ChatMessage();
			myObject.setName(nameField.getText());
			myObject.setMessage("Send_List");
			try 
			{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			}	 
			catch (IOException ioe) 
			{
				System.out.println(ioe.getMessage());
			}
		}
		void disconnectSignal()
		{
			myObject = new ChatMessage();
			myObject.setName(nameField.getText());
			myObject.setMessage("Exit from chat room");
			try 
			{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			} 
			catch (IOException ioe) 
			{
			}
		}
		void writeToFile() throws IOException//history
		{
			String fileName = f.getTitle() + ".txt";
			fileWrite = new BufferedWriter( new FileWriter(fileName));
			fileWrite.write(history);
			System.out.println(fileName + " File Writing Successful!!");
			System.out.println("Data in file :\n" + history);
			fileWrite.flush();
			File file = new File(fileName);
			java.awt.Desktop.getDesktop().open(file);
		}
		void storeMessage(String name, String message)
		{
			history = history + name + " : " + message + "\n";
		}
		public static void main(String[] arg) 
		{
			Client c = new Client();
		}
	}