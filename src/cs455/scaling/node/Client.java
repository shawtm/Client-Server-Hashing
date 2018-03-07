package cs455.scaling.node;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

import cs455.scaling.util.WorkUnit;

public class Client {
	private int messages;
	private LinkedList<String> hashes;
	private ByteBuffer buf;
	private Random r;
	
	public Client(String ip, int port, int messages){
		this.messages = messages;
		buf = ByteBuffer.allocate(8192); //8kb size for buffer
		this.r = new Random();
	}
	public void start() throws InterruptedException{
		while(true){
			// fill buffer with bytes
			this.fillBuf();
			// send buffer
			this.sendBuf();
			// sleep
			Thread.sleep((1000/messages));
		}
		
	}
	public void fillBuf(){
		while(buf.hasRemaining()){
			buf.put((byte) r.nextInt(128));
		}
		try {
			hashes.add(WorkUnit.SHA1FromBytes(buf.array()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public void sendBuf(){
		// TODO
	}
	public static void main(String[] args) {
		if (args.length !=3){
			System.out.println("[ERROR] invalid number of arguments");
			System.out.println("Use the form client <ip> <port> <r>");
			return;
		}
		Client c = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		try {
			c.start();
		} catch (InterruptedException e) {
		}
	}

}
