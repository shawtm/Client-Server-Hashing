package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WorkUnit implements Runnable {
	
	
	public static String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		 MessageDigest digest = MessageDigest.getInstance("SHA1");
		 byte[] hash = digest.digest(data);
		 BigInteger hashInt = new BigInteger(1, hash);
		 return hashInt.toString(16);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
