package org.dobrochin.civilsociety;

import org.dobrochin.civilsociety.requests.RequestService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	private TextView res;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	private void init()
	{
		res = (TextView)findViewById(R.id.result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onReceiveResponse(Intent intent) {
		res.setText("" + intent.getStringExtra(RequestService.RESPONSE));
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = new Intent(this, RequestService.class);
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_NEWS);
		sendRequest(intent);
	}

}
