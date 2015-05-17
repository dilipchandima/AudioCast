/* 
 * E/11/171 Jayalath JDC
 * E/11/037 Bandara HMAPK
 */
package audiocast.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

public class client extends Thread{

	private static MulticastSocket socket = null;
	private static InetAddress groupAdd;
    final static int MAXLEN = 1024;
    public static boolean receive = false;
	
    InetAddress host;
    
    final BlockingQueue<byte[]> queue;
    
    public client(BlockingQueue<byte[]> queue){
    	
    	this.queue = queue;
    	
    	try{
    		groupAdd = InetAddress.getByName("224.0.0.1");
    		socket = new MulticastSocket(6666);
    		socket.joinGroup(groupAdd);
    	}catch(Exception e){
    		Log.e("Audiocast", "error");
    	}
    	Log.d("Audiocast", "server created");
    	
    }
    
    @Override
    public void run(){
    	
    	try{
    		byte[] rcvPkt = new byte[MAXLEN];
    		DatagramPacket pkt;
    		
    		while(!Thread.interrupted()){
    			pkt = new DatagramPacket(rcvPkt, MAXLEN);
    			try{
    				if(receive) socket.receive(pkt);
					rcvPkt = pkt.getData();
					queue.put(rcvPkt);
    			}catch(IOException e){
    				e.printStackTrace();
    			}
    		}
    	}catch(InterruptedException e){
    		
    	}finally{
    		try {
				socket.leaveGroup(groupAdd);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
}
