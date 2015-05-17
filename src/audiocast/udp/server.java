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

public class server extends Thread{

	private static MulticastSocket socket = null;
	private static InetAddress groupAdd;
    final static int MAXLEN = 1024;
    public static boolean broadcast = false;
    
    final BlockingQueue<byte[]> queue;
    
    public server(BlockingQueue<byte[]> queue){

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
    		
    		while (!Thread.interrupted()) {	
				rcvPkt = queue.take();
				pkt = new DatagramPacket(rcvPkt,MAXLEN,groupAdd,6666);
				try {
					if(broadcast) socket.send(pkt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}catch(InterruptedException e) {
		} finally {
			try {
				socket.leaveGroup(groupAdd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
	
}
