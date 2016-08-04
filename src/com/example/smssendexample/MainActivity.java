package com.example.smssendexample;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	static String PHONE_NUMBER;
	final static String MESSAGE = "good morning!! http://maps.google.com/?q=37.5651,126.98955";
	private int SIM_STATE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PHONE_NUMBER = tMgr.getLine1Number();
		setContentView(R.layout.activity_main);
	}

	/**
	 * When sendSMS button clicked, do the below job
	 * 
	 * @param v
	 */
	public void sendSMS(View v) {
		if (isSimExists()) {

			try {

				String SENT = "SMS_SENT";

				PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
						new Intent(SENT), 0);

				registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context arg0, Intent arg1) {
						int resultCode = getResultCode();
						switch (resultCode) {
						case Activity.RESULT_OK:
							Toast.makeText(getBaseContext(), "SMS sent",
									Toast.LENGTH_LONG).show();
							// When sms sent successfully, start service to
							// insert sent message

							Intent intent = new Intent(
									"com.example.smssendexample.SentSmsLogger");
							intent.putExtra(Constants.KEY_PHONE_NUMBER,
									PHONE_NUMBER);
							intent.putExtra(Constants.KEY_MESSAGE, MESSAGE);
							startService(intent);

							break;
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
							Toast.makeText(getBaseContext(), "Generic failure",
									Toast.LENGTH_LONG).show();
							break;
						case SmsManager.RESULT_ERROR_NO_SERVICE:
							Toast.makeText(getBaseContext(), "No service",
									Toast.LENGTH_LONG).show();
							break;
						case SmsManager.RESULT_ERROR_NULL_PDU:
							Toast.makeText(getBaseContext(), "Null PDU",
									Toast.LENGTH_LONG).show();
							break;
						case SmsManager.RESULT_ERROR_RADIO_OFF:
							Toast.makeText(getBaseContext(), "Radio off",
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				}, new IntentFilter(SENT));

				SmsManager smsMgr = SmsManager.getDefault();
				smsMgr.sendTextMessage(PHONE_NUMBER, null, MESSAGE, sentPI,
						null);

			} catch (Exception e) {
				Toast.makeText(this,
						e.getMessage() + "!\n" + "Failed to send SMS",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, getSimState() + " " + "Cannot send SMS",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * @return true if SIM card exists false if SIM card is locked or doesn't
	 *         exists <br/>
	 * <br/>
	 *         <b>Note:</b> This method requires permissions <b>
	 *         "android.permission.READ_PHONE_STATE" </b>
	 */
	private boolean isSimExists() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		SIM_STATE = telephonyManager.getSimState();

		if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
			return true;

		return false;
	}

	/**
	 * Get simcard state
	 * 
	 * @return
	 */
	private String getSimState() {
		switch (SIM_STATE) {
		case TelephonyManager.SIM_STATE_ABSENT: // SimState =
			return "No Sim Found!"; // "No Sim Found!";
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED: // SimState =
														// "Network Locked!";
			return "Network Locked!";
		case TelephonyManager.SIM_STATE_PIN_REQUIRED: // SimState =
														// "PIN Required to access SIM!";
			return "PIN Required to access SIM!";
		case TelephonyManager.SIM_STATE_PUK_REQUIRED: // SimState =
														// "PUK Required to access SIM!";
														// // Personal
														// Unblocking Code
			return "PUK Required to access SIM!";
		case TelephonyManager.SIM_STATE_UNKNOWN: // SimState =
													// "Unknown SIM State!";
			return "Unknown SIM State!";
		}
		return null;
	}

}
