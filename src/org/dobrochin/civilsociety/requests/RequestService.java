package org.dobrochin.civilsociety.requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RequestService extends IntentService{
	private static final String BASE_URL = "http://apwsllc.com/prototypes/1/";
	public static final String BACK_ADDRESS = "back_address";
	public static final String REQUEST_TYPE = "request_type";
	public static final String RESPONSE = "response";
	public static final String REQUEST_JSON = "request_json";
	
	public static final int REQUEST_SEND_POST_PAIRS = 0;
	public static final int REQUEST_GET_NEWS = 1;
	
	HttpClient httpClient;
	public RequestService() {
		super("requestservice");
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = new DefaultHttpClient();
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		int requestType = intent.getIntExtra(REQUEST_TYPE, -1);
		String action = intent.getStringExtra(BACK_ADDRESS);
		switch(requestType)
		{
			case REQUEST_GET_NEWS:
				String response = getNews();
				sendResponse(action, response);
				break;
			case REQUEST_SEND_POST_PAIRS:
				sendPostPairsRequest(intent.getStringExtra(REQUEST_JSON));
				break;
		}
	}
	
	private void sendPostPairsRequest(String json) {
	    HttpPost httppost = new HttpPost(BASE_URL + "m/q.php");
	    try {
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("p_json", json));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpClient.execute(httppost);
	        HttpEntity httpEntity = response.getEntity();
		    String responseString = EntityUtils.toString(httpEntity);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	}
	private void sendResponse(String action, String response)
	{
		Intent intentResponse = new Intent();
		intentResponse.setAction(action);
		intentResponse.putExtra(RESPONSE, response);
		sendBroadcast(intentResponse);
	}
	
	private String getNews()
	{
		String responseString = null;
		
		HttpResponse response;
		try {
			response = httpClient.execute(new HttpGet(BASE_URL + "s_list.json"));
			HttpEntity httpEntity = response.getEntity();
		    responseString = EntityUtils.toString(httpEntity);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseString;
	}

}
