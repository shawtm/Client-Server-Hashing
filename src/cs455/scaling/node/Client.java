package cs455.scaling.node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

import cs455.scaling.util.WorkUnit;

public class Client {
	private int messages, port;
	private LinkedList<String> hashes;
	private ByteBuffer buf;
	private Random r;
	private SocketChannel chan;
	private String ip;
	private Selector sel;
	
	public Client(String ip, int port, int messages) throws IOException{
		this.messages = messages;
		buf = ByteBuffer.allocate(8192); //8kb size for buffer
		this.r = new Random();
		chan = SocketChannel.open();
		sel = Selector.open();
		this.ip = ip;
		this.port = port;
	}
	public void start() throws InterruptedException, IOException{
		SocketChannel channel = SocketChannel.open();
		chan.configureBlocking(false);
		chan.register(sel, SelectionKey.OP_CONNECT);
		chan.connect(new InetSocketAddress(ip, port));
		while(true){
			for(SelectionKey key: sel.selectedKeys()) {
				if(key.isConnectable()){
					this.connect(key);
				}
				// fill buffer with bytes
				this.fillBuf();
				// send buffer
				this.sendBuf();
				// sleep
			}
			Thread.sleep((1000/messages));
			System.out.println("[timestamp] Total Sent Count: x, Total Received Count: y");
		}
		
	}
	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		channel.finishConnect();
		key.interestOps(SelectionKey.OP_WRITE);
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
		try {
			Client c = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			c.start();
		} catch (InterruptedException e) {
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
