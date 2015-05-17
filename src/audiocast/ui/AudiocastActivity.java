/* 
 * E/11/171 Jayalath JDC
 * E/11/037 Bandara HMAPK
 */
package audiocast.ui;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ToggleButton;
import audiocast.audio.Play;
import audiocast.udp.server;
import audiocast.udp.client;
import audiocast.audio.Record;
import co324.audiocast.R;


public class AudiocastActivity extends Activity {
	final static int SAMPLE_HZ = 22050, BACKLOG = 8;
	
	Record rec; 
	Play play;
	server serverObj;
	client clientObj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
		setContentView(R.layout.activity_audiocast);
		
		WifiManager wifi = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		if(wifi != null){
		    WifiManager.MulticastLock lock = 
		    		wifi.createMulticastLock("Audiocast");
		    lock.setReferenceCounted(true);
		    lock.acquire();
		} else {
			Log.e("Audiocast", "Unable to acquire multicast lock");
			finish();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		BlockingQueue<byte[]> recBuff = new ArrayBlockingQueue<byte[]>(BACKLOG);
		BlockingQueue<byte[]> playBuff = new ArrayBlockingQueue<byte[]>(BACKLOG);
		
		rec = new Record(SAMPLE_HZ, recBuff);
		play = new Play(SAMPLE_HZ, playBuff);
		serverObj = new server(recBuff);
		clientObj = new client(playBuff);
		
		findViewById(R.id.Record).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					rec.pause(!((ToggleButton)v).isChecked());
					
					if(((ToggleButton)v).isChecked()){
						server.broadcast = true;
					}else{
						server.broadcast = false;
					}
					
					play.pause(((ToggleButton)v).isChecked());
					if(!((ToggleButton)v).isChecked()){
						client.receive = true;
					}else{
						client.receive = false;
					}
			}
		});
		Log.i("Audiocast", "Starting all threads");
		rec.start();
		play.start();
		serverObj.start();
		clientObj.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.i("Audiocast", "Stopping all threads");
		rec.interrupt();
		play.interrupt();
		serverObj.interrupt();
		clientObj.interrupt();
	}
	
}


