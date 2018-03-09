package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import cs455.scaling.util.WorkUnit;

public class Client {
	private int messages, port, sent, received;
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
		int count = 0;
		while(true){
			sel.select();
			for(SelectionKey key: sel.selectedKeys()) {
				if(key.isWritable()) {
					//System.out.println("trying to write");
					// fill buffer with bytes
					this.fillBuf();
					// send buffer
					SocketChannel chan = (SocketChannel) key.channel();
					ByteBuffer buffer = ByteBuffer.wrap(buf);
					//System.out.println("Writing!");
					while (buffer.hasRemaining())
						chan.write(buffer);
					sent++;
					key.interestOps(SelectionKey.OP_READ);
				}else if(key.isReadable()) {
					//System.out.println("Reading!");
					ByteBuffer buffer = ByteBuffer.allocate(40);
					SocketChannel chan = (SocketChannel) key.channel();
					//System.out.println("about to read");
					int read = 0;
					while (buffer.hasRemaining() && read != -1) 
						read = chan.read(buffer);
					
					//System.out.println("Finished Reading");
					String hash = new String(unpad(buffer.array()));
					//System.out.println("Got Hash " + hash);
					boolean has = hashes.remove(hash);
					if (has) {
						//System.out.println("successfully removed element");
						received++;
					}else
						System.out.println("failed to remove element");
					key.interestOps(SelectionKey.OP_WRITE);
				}
				// sleep
			}
			Thread.sleep((500/messages));
			count++;
			if (count == (40*messages)) {
				System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +" Total Sent Count: "+sent+", Total Received Count: " + received);
				count = 0; sent =0; received = 0;
			}
		}
		
	}
	public byte[] unpad(byte[] bytes) {
		int base = 40;
		for(int i = 39; i >=0 ; i--)
			if (bytes[i] == 0)
				base--;
		byte[] ret = new byte[base];
		for(int i = 0; i < base; i++)
			ret[i] = bytes[i];
		return ret;
	}
	public void fillBuf(){
		for (int i = 0; i < buf.length; i++)
			buf[i] = (byte) r.nextInt(128);
		try {
			hashes.add(WorkUnit.SHA1FromBytes(buf));
			//System.out.println("Hash = " + WorkUnit.SHA1FromBytes(buf));
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
