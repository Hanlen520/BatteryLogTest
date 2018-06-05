package com.cfzz.abwsmt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.os.SystemClock;

//import com.cfzz.abwsmt.R;
import com.cfzz.abwsmt.BatteryTools.BatteryElectricUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryQuantityUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryStatusUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryTemperatureUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryVoltageUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AndroidBatteryWithServiceActivity extends Activity {

	private Timer atimer = null;
	private TimerTask atask = null;
	private Handler ahandler;
    private String TAG = "com.cfzz.abl";
	static SQLiteDatabase db;
	private Button button, button01, button02 = null;
	private String fname;
	public TextView TV, BQ, BV, BE, BT, BI, TimeIntervalName;
	public EditText TimeIntervalInput;
	public static int nti;

	//public PowerManager pm = null;
	//public PowerManager.WakeLock wl = null;
	 
	private int mq, mv, me;
	private double mt;
	
	private ImageButton state = null;

	public static final String TABLE_NAME = "Temp";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		state = (ImageButton) findViewById(R.id.state);
		state.setOnClickListener(new ocl());
		
		TimeIntervalName = (TextView) findViewById(R.id.TimeIntervalName);	
		TimeIntervalInput = (EditText) findViewById(R.id.TimeIntervalInput);
		
		TV = (TextView) findViewById(R.id.TV);// 
		
		BI = (TextView) findViewById(R.id.BI);//

		try {
			//db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.cfzz.abwsmt/databases/BatteryDB.db", null);//
			db = openOrCreateDatabase("BatteryDB", MODE_PRIVATE, null);

		} catch (SQLException e) {
		}

		ahandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mq = BatteryQuantityUtils.getQuantityFile();// 
				mv = BatteryVoltageUtils.getVoltageFile();// 
				me = BatteryElectricUtils.getElectricFile();//
				mt = BatteryTemperatureUtils.getTemperatureFile()/10.0;// 
								
				BI.setText("Level:   " + mq + "%" + "\n" +
				           "Voltage:   " + mv + "mV" + "\n" +
						   "Electric:   " + me + "mA" + "\n" +
						   "Temperature:   " + mt + "C");
				TV.setText("Status: " + BatteryStatusUtils.getBatteryTemp() + "---" 
						   + BatteryStatusUtils.getBatteryStatus() + "---" + BatteryStatusUtils.getSupplyOnline());
				super.handleMessage(msg);
			}
		};
		atimer = new Timer();
		atask = new TimerTask()// 
		{
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				ahandler.sendMessage(message);
				/*if(BatteryQuantityUtils.getQuantityFile() == 100 && BatteryStatusUtils.getBatteryTemp() == "Full")
				{
					int fme = (me + BatteryElectricUtils.getElectricFile())/2;
					if (is == true && (fme>=0 && fme <=80))
					{
						//CFBatteryThread.interrupted();
						try{
						state.callOnClick();// 
						}catch(Exception e)
						{
						//AndroidBatteryWithServiceActivity.this.stopService(localIntent);
						}
					}
				}*/
				
				
			}
		};
		atimer.schedule(atask, 0, 1000);// 
		
		// 
		
		this.button = (Button) this.findViewById(R.id.my_button);// 
		// 
		this.button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (is == true) {
					state.callOnClick();// 
					try {
						db.execSQL("DROP TABLE IF EXISTS Batterylog");//
						//
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					} catch (Exception e) {
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					}
				} else {
					try {
						db.execSQL("DROP TABLE IF EXISTS Batterylog");//
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					} catch (Exception e) {
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					}
				}
			}
		});

		button01 = (Button) this.findViewById(R.id.Button01);// 
		// 
		button01.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (is == true) {
					state.callOnClick();// 
					try {
						outputData();
						
					} catch (Exception e) {
						new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
						.setPositiveButton("Fail to output", null).show();
					}
				} else {
					try {
						outputData();
					} catch (Exception e) {
						new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
						.setPositiveButton("Fail to output", null).show();
					}
				}
			}
		});

		// 


		button02 = (Button) this.findViewById(R.id.Button02);//
		//
		button02.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				//控制灭屏
				PowerManager.WakeLock wl = pm.newWakeLock(
						PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTest1");
				wl.acquire();//
			}
		});
		
	}

	// 
	public static boolean is = false;
	Intent localIntent;
	// 
	
	
	class ocl implements OnClickListener {

		
		public void onClick(View v) {

			if (v.getId() == R.id.state && is == true) {
				// 
				state.setImageDrawable(getResources().getDrawable(
						R.drawable.close));

				try{
					AndroidBatteryWithServiceActivity.this.stopService(localIntent);
				}catch(Exception e)
				{
				    
				}
				
				is = false;

			} else if (v.getId() == R.id.state && is == false) {
				// 
				

				try
                {
                    db.rawQuery("select * from Batterylog", null);
                    state.setImageDrawable(getResources().getDrawable(
    						R.drawable.open));
                    nti = Integer.parseInt(TimeIntervalInput.getText().toString());
                    AndroidBatteryWithServiceActivity localCFBatteryActivity = AndroidBatteryWithServiceActivity.this;			
    			    localIntent = new Intent(localCFBatteryActivity, com.cfzz.abwsmt.BatteryLogMTKService.class);
    				localCFBatteryActivity.startService(localIntent);
    				Log.v(TAG, "Service start success!");
                }
                catch(Exception e)
                {
                    //state.callOnClick();// 
                    new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
                    .setPositiveButton("Fail to write data,please clear data", null).show();
                }
				
				

				is = true;
			}
		}
	}
	
	private synchronized void outputData()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());// 
		fname = sdf.format(new Date()) + ".csv";// 
		
		Cursor c = db.rawQuery("select * from Batterylog", null);//
		Log.v(TAG, "Batterylog: " + c);
		if(c.getCount()!=0)
		{
			SqliteToCsv.ExportToCSV(c, fname); // 
			new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
			.setPositiveButton("Output success", null).show();
		}
		else
		{
			new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
			.setPositiveButton("Data table empty,no to output", null).show();
		}
		
		
	}
	
	@Override	
	public void onDestroy() // 
	{

		try{
				atimer.cancel();// 
				//atimer = null;
				atask.cancel();
				atask = null;
		}
		catch(Exception e){
				Log.v(TAG, "Test activity has not started!");
		}
		
		/*if (is == true) {
			try{
					AndroidBatteryWithServiceActivity.this.stopService(localIntent);
				}catch(Exception e)
				{
					Log.v(TAG, "No test Mission!");
				}
		}*/

		super.onDestroy();
	}
}