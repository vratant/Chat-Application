package javaproject;
import java.io.*;
import java.net.*;
import java.util.*;

public class Chatserver 
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Set<String> userlist = new HashSet();
	@SuppressWarnings("resource")
	public static void main(String[] args) 
	{
		ArrayList<Usernumber> AllHandlers = new ArrayList<Usernumber>();
		try 
		{
			ServerSocket s = new ServerSocket(8181);
			for (;;) 
			{
				Socket incoming = s.accept();
				new Usernumber(incoming, AllHandlers).start();
			}
		} 
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}
	static String printlist() 
	{
		String list = "";
		for (String i : userlist)
		{
			list = list + i + "\n";
		}
		return list;
	}
}
class Usernumber extends Thread 
{
	public Usernumber(Socket i, ArrayList<Usernumber> h) 
	{
		incoming = i;
		handlers = h;
		handlers.add(this);
		try 
		{
			in = new ObjectInputStream(incoming.getInputStream());
			out = new ObjectOutputStream(incoming.getOutputStream());
		} 
		catch (IOException ioe) 
		{
		}
	}
	public synchronized void broadcast() throws Exception 
	{
		Usernumber left = null;
		for (Usernumber handler : handlers) 
		{
			ChatMessage cm = new ChatMessage();
			String x = new String(myObject.getMessage());
			String y = new String("Send_List");
			String z = new String("Disconnect_me");
			if (x.equals(y)) 
			{
				cm.setName("Users Currently Online");
				cm.setMessage(Chatserver.printlist());
				handler.out.writeObject(cm);
			}
			else if (x.equals(z)) 
			{
				cm.setName(myObject.getName());
				Chatserver.userlist.remove(myObject.getName());
				cm.setMessage(myObject.getName() +"has disconnected");
				handler.out.writeObject(cm);
			}
			else 
			{
				cm.setName(myObject.getName());
				cm.setMessage(myObject.getMessage());
				handler.out.writeObject(cm);
				Chatserver.userlist.add(myObject.getName());
			}
		}
		handlers.remove(left);
	}
	public void run() 
	{
		try 
		{
			while (!done) 
			{
				myObject = (ChatMessage) in.readObject();
				broadcast();
			}
		} 
		catch (IOException e) 
		{
			if (e.getMessage().equals("Connection reset")) 
			{
			} 
			else 
			{
				System.out.println("Problem receiving: " + e.getMessage());
			}
		} 
		catch (Exception cnfe) 
		{
			System.out.println(cnfe.getMessage());
		} 
		finally 
		{
			handlers.remove(this);
		}
	}
	ChatMessage myObject = null;
	private Socket incoming;
	boolean done = false;
	ArrayList<Usernumber> handlers;
	ObjectOutputStream out;
	ObjectInputStream in;
}