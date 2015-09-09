package com.example.acctest;

import com.example.audio.AudioQuality;
import com.example.streaming.Session;
import com.example.streaming.SessionBuilder;
import com.example.streaming.rtspclient.RtspClient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	
	private Button mButton;
	public static String mHost = "192.168.8.110";
	public static String mSdpName = "/acc.sdp";
	private final int mHostPort = 554;
	private RtspClient mRtspClient;
	private Session    mSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton = (Button) findViewById(R.id.send_acc);
		mButton.setOnClickListener(this);
		
		// ´´½¨RtspClient
		mSession = 	SessionBuilder.getInstance()
				.setContext(getApplicationContext())
				.setAudioEncoder(SessionBuilder.AUDIO_AAC)
				.setAudioQuality(new AudioQuality(8000,16000))
				.build();
		mRtspClient = new RtspClient();
		mRtspClient.setSession(mSession);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.send_acc:
			mRtspClient.setServerAddress(mHost, mHostPort);
			mRtspClient.setStreamPath(mSdpName);
			mRtspClient.startStream();
			break;
		}
	}
	

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mRtspClient.stopStream();
		mRtspClient.release();
		mSession.release();
	}


}
