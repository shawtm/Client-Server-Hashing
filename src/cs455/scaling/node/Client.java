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
	private byte[] buf;
	private Random r;
	private String ip;
	private Selector sel;
	
	public Client(String ip, int port, int messages) throws IOException{
		this.messages = messages;
		buf = new byte[8192]; //8kb size for buffer
		this.r = new Random();
		sel = Selector.open();
		this.ip = ip;
		this.port = port;
		hashes = new LinkedList<>();
	}
	
	public void start() throws InterruptedException, IOException{
		SocketChannel channel = SocketChannel.open(new InetSocketAddress(ip, port));
		channel.configureBlocking(false);
		channel.register(sel, SelectionKey.OP_WRITE);
		while(true){
			sel.select();
			for(SelectionKey key: sel.selectedKeys()) {
				if(key.isWritable()) {
					System.out.println("trying to write");
					// fill buffer with bytes
					this.fillBuf();
					// send buffer
					SocketChannel chan = (SocketChannel) key.channel();
					ByteBuffer buffer = ByteBuffer.wrap(buf);
					System.out.println("Writing!");
					while (buffer.hasRemaining())
						chan.write(buffer);
					key.interestOps(SelectionKey.OP_READ);
				}else if(key.isReadable()) {
					ByteBuffer buffer = ByteBuffer.allocate(40);
					SocketChannel chan = (SocketChannel) key.channel();
					int read = 0;
					while (buffer.hasRemaining() && read != -1) 
						read = chan.read(buffer);
					String hash = new String(buffer.array());
					hashes.remove(hash);
				}
				// sleep
			}
			Thread.sleep((1000/messages));
			//System.out.println("[timestamp] Total Sent Count: x, Total Received Count: y");
		}
		
	}
	
	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		channel.finishConnect();
		key.interestOps(SelectionKey.OP_WRITE);
		System.out.println("Connection Established");
	} 
	
	public void fillBuf(){
		for (int i = 0; i < buf.length; i++)
			buf[i] = (byte) r.nextInt(128);
		try {
			hashes.add(WorkUnit.SHA1FromBytes(buf));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
