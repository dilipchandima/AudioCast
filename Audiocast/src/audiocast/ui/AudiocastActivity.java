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
import android.widget.ToggleButton;
import audiocast.audio.Play;
import audiocast.audio.Record;
import co324.audiocast.R;

/** 
 * @author (C) ziyan maraikar
 */
public class AudiocastActivity extends Activity {
	final static int SAMPLE_HZ = 11025, BACKLOG = 8;	
//	final static InetSocketAddress multicast = new InetSocketAddress("224.0.0.1", 3210);
	
	Record rec; 
	Play play;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		BlockingQueue<byte[]> buf = new ArrayBlockingQueue<byte[]>(BACKLOG);		
		rec = new Record(SAMPLE_HZ, buf);
		play = new Play(SAMPLE_HZ, buf);
		
		findViewById(R.id.Record).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					rec.pause(!((ToggleButton)v).isChecked());
			}
		});		
		findViewById(R.id.Play).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					play.pause(!((ToggleButton)v).isChecked());
			}
		});	
		
		Log.i("Audiocast", "Starting recording/playback threads");
		rec.start();
		play.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.i("Audiocast", "Stopping recording/playback threads");
		rec.interrupt();
		play.interrupt();
	}
}
