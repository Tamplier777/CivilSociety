package org.dobrochin.civilsociety.requests;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RequestService extends IntentService{
	public static final String BACK_ADDRESS = "back_address";
	public static final String REQUEST_TYPE = "request_type";
	public static final String RESPONSE = "response";
	
	public static final int REQUEST_GET_NEWS = 0;
	public RequestService() {
		super("requestservice");
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		int requestType = intent.getIntExtra(REQUEST_TYPE, -1);
		String action = intent.getStringExtra(BACK_ADDRESS);
		switch(requestType)
		{
			case REQUEST_GET_NEWS:
				HttpClient httpClient = new DefaultHttpClient();
				String output = "connection error";
				
				HttpResponse response;
				try {
					response = httpClient.execute(new HttpGet("http://apwsllc.com/prototypes/1/s_list.json"));
					HttpEntity httpEntity = response.getEntity();
				    output = EntityUtils.toString(httpEntity);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent intentResponse = new Intent();
				intentResponse.setAction(action);
				intentResponse.putExtra(RESPONSE, output);
				sendBroadcast(intentResponse);
				break;
		}
	}

}
