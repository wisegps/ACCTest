package com.example.acctest;

import com.example.audio.AudioQuality;
import com.example.streaming.Session;
import com.example.streaming.SessionBuilder;
import com.example.streaming.rtspclient.RtspClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
//	public static String mHost = "42.121.109.221";//流媒体服务器地址
	
	public static String mHost = "";//流媒体服务器地址
	
	public static String mSdpName = "/acc.sdp";  //发送到流媒体服务器的文件名字
	private final int mHostPort = 554; //流媒体服务器的端口
	private RtspClient mRtspClient;    // RTSP客户端
	private Session    mSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		Button mButton = (Button) findViewById(R.id.send_acc);//push 
		mButton.setOnClickListener(this);
		Button mBtnSet = (Button) findViewById(R.id.setting);//setting 
		mBtnSet.setOnClickListener(this);
		/** 创建RtspClient */		
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
		
		switch(v.getId()){
		case R.id.send_acc:
			mHost = pref.getString("ip", "");
			Log.e("测试是取出",mHost);
			mRtspClient.setServerAddress(mHost, mHostPort);
			mRtspClient.setStreamPath(mSdpName);
			mRtspClient.startStream();//开始推送语音
			break;
		
		case R.id.setting:
			final EditText ipEdit = new EditText(this); 
			boolean isRemember = pref.getBoolean("ip_save", false);
			String strIP = "";
			if(isRemember){
				strIP = pref.getString("ip", "");
				Log.e("测试是否成功", strIP);
				new AlertDialog.Builder(this)
				.setTitle("配置服务器IP")
				.setIcon(R.drawable.ic_launcher)
				.setMessage("服务器IP已经配置完成:" + "\r\n" + strIP)
				.setNegativeButton(R.string.ok_setting, null)
				.setPositiveButton(R.string.config_setting, 
						new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor = pref.edit();
						editor.putBoolean("ip_save", false);
						editor.commit();
					}
				})
				.show();				
			}else{
				new AlertDialog.Builder(this)
				.setTitle("配置流媒体服务器的IP")
				.setIcon(R.drawable.ic_launcher)
				.setView(ipEdit)
				.setPositiveButton(R.string.save_setting, 
						new android.content.DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String inputStr = ipEdit.getText().toString();
						if("".equals(inputStr)){
							Toast.makeText(MainActivity.this, 
									"服务器配置内容不能为空", Toast.LENGTH_SHORT).show();  	
						}else{
							editor = pref.edit();
							editor.putBoolean("ip_save", true);
							editor.putString("ip", inputStr);
							editor.commit();
							Log.e("测试是否成功", inputStr);
						}
					}
				})
				.setNegativeButton(R.string.cancal_settings, null)
				.show();
			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mRtspClient.stopStream();
		mRtspClient.release();
		mSession.release();
	}
}
