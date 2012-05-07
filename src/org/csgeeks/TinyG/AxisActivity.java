package org.csgeeks.TinyG;

import org.csgeeks.TinyG.system.Axis;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AxisActivity extends FragmentActivity {
	private static final String TAG = "TinyG";
	private TinyGDriver tinyg;
	private ServiceConnection mConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.axis);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mConnection = new NetworkServiceConnection();

		if (bindService(new Intent(this, TinyGDriver.class), mConnection,
				Context.BIND_AUTO_CREATE)) {
		} else {
			Toast.makeText(this, "Binding service failed", Toast.LENGTH_SHORT)
					.show();
		}

		Spinner s = (Spinner) findViewById(R.id.axispick);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.axisArray, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new AxisItemSelectedListener());
	}

	@Override
	public void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}

	// TODO
	public class AxisItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Toast.makeText(
					parent.getContext(),
					"The planet is " + parent.getItemAtPosition(pos).toString(),
					Toast.LENGTH_LONG).show();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

	public void myClickHandler(View view) {
		// Just in case something happened, though it seems like this shouldn't
		// be possible.
		if (tinyg == null) {
			if (bindService(new Intent(this, TinyGDriver.class), mConnection,
					Context.BIND_AUTO_CREATE)) {
			} else {
				Toast.makeText(this, "Binding service failed",
						Toast.LENGTH_SHORT).show();
			}
			return;
		}
		switch (view.getId()) {
		case R.id.save:
			// TODO
			break;
		}
	}

	private class NetworkServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {
			tinyg = ((TinyGDriver.NetworkBinder) service).getService();
			Log.i(TAG, "got binder");
			if (tinyg.isReady()) {
				Log.i(TAG, "setting values in axis activity");
				Axis x = tinyg.getMachine().getAxisByName("X");
				((EditText) findViewById(R.id.feed_rate)).setText(Float
						.toString(x.getFeed_rate_maximum()));
				((EditText) findViewById(R.id.search_velocity)).setText(Float
						.toString(x.getHoming_search_velocity()));
				((EditText) findViewById(R.id.latch_velocity)).setText(Float
						.toString(x.getHoming_latch_velocity()));
				((ToggleButton) findViewById(R.id.axis_mode)).setChecked(x
						.isAxis_mode());
				((EditText) findViewById(R.id.switch_mode)).setText(Integer
						.toString(x.getSwitch_mode()));
				((EditText) findViewById(R.id.velocity_max)).setText(Float
						.toString(x.getVelocity_maximum()));
				((EditText) findViewById(R.id.travel_max)).setText(Float
						.toString(x.getTravel_maximum()));
				((EditText) findViewById(R.id.jerk_max)).setText(Float
						.toString(x.getJerk_maximum()));
				((EditText) findViewById(R.id.junction_deviation))
						.setText(Float.toString(x.getJunction_devation()));
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			tinyg = null;
		}
	}
}