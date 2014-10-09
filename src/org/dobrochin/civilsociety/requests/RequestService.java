package org.dobrochin.civilsociety.requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.dobrochin.civilsociety.R;
import org.dobrochin.civilsociety.social.VKAuth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RequestService extends IntentService{
	
	public static final String BACK_ADDRESS = "back_address";
	public static final String REQUEST_TYPE = "request_type";
	public static final String RESPONSE = "response";
	public static final String REQUEST_JSON = "request_json";
	public static final String REQUEST_JSON_POST = "request_json_post";
	public static final String SOCIAL_NETWORK_URL = "sn_get_profile";
	
	//В запросе указывается, нужно ли спрашивать пользователя о повторной отправке запроса, если нет подключения
	public static final String NO_CONNECTION_ACTION = "no_connection_action";
	public static final int ACTION_DO_NOTHING = 0;
	public static final int ACTION_SHOW_ALERT_DIALOG = 1;
	public static final int ACTION_SHOW_DIALOG_TO_SAVE = 2;
	public static final int ACTION_SAVE_WITHOUT_DIALOG = 3;
	
	public static final String RESPONSE_STATE = "response_state";
	public static final int STATE_OK = 0;
	public static final int STATE_CONNECTION_ERROR = 1;
	public static final int STATE_SERVER_CONNECTION_ERROR = 2;
	public static final int STATE_REQUEST_ERROR = 3; //если проблема в запросе, надо бы отправлять предупреждения
	
	public static final int REQUEST_SEND_POST_PAIRS = 0;
	public static final int REQUEST_GET_NEWS = 1;
	public static final int REQUEST_GET_SOCIAL_PROFILE = 2;
	public static final int REQUEST_GET_ADITIONAL_SN_INFORMATION = 3; //За доп информацией для соц. сетей отсылаем такой запрос. 
	
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
		String response = null;
		try
		{
			switch(requestType)
			{
				case REQUEST_GET_NEWS:
					response = sendGetRequest(URL.BASE_URL + "s_list.json");
					break;
				case REQUEST_SEND_POST_PAIRS:
					List<NameValuePair> stdNVP = formStandartServerPostPairs(intent.getStringExtra(REQUEST_JSON));
					response = sendPostPairsRequest(URL.BASE_URL + "m/q.php", stdNVP);
					break;
				case REQUEST_GET_SOCIAL_PROFILE:
					String request = intent.getStringExtra(SOCIAL_NETWORK_URL);
					String post = intent.getStringExtra(REQUEST_JSON_POST);
					if(post != null)
					{
						response = sendPostPairsRequest(request, formPairsFromJson(post));
					}
					else response = sendGetRequest(request);
					break;
				case REQUEST_GET_ADITIONAL_SN_INFORMATION:
					String preAuthUrl = intent.getStringExtra(SOCIAL_NETWORK_URL);
					String postPreAuth = intent.getStringExtra(REQUEST_JSON_POST);
					if(postPreAuth != null)
					{
						response = sendPostPairsRequest(preAuthUrl, formPairsFromJson(postPreAuth));
						response = transformURLParamsToJson(response);
					}
					else response = sendGetRequest(preAuthUrl);
					break;
			}
		}
		catch(ClientProtocolException e){}
		catch(IOException e){}
		
		sendAnswer(intent, response, requestType);
	}
	private void sendAnswer(Intent intent, String response, int requestType)
	{
		String action = intent.getStringExtra(BACK_ADDRESS);
		if(response != null)
		{
			String errorDescription[] = new String[1];
			int responseState = getResponseState(response, errorDescription);
			if(responseState == STATE_OK) sendResponse(action, response, intent.getStringExtra(RequestQuerue.QUERY_ID), requestType);
			else sendRequestBack(intent, action, errorDescription[0], responseState);
		}
		else
		{
			sendRequestBack(intent, action, getResources().getString(R.string.server_connection_error), STATE_CONNECTION_ERROR);
		}
		
	}
	public int getResponseState(String response, String errorDescription[])
	{
		int state = STATE_SERVER_CONNECTION_ERROR;
		errorDescription[0] = getResources().getString(R.string.server_connection_error);
		try {
			Object respJson = null;
			if(response.startsWith("{"))respJson = new JSONObject(response);
			else if (response.startsWith("["))respJson = new JSONArray(response);
			
			if(respJson != null && !response.contains("error")) state = STATE_OK;
			else if(respJson != null) errorDescription[0] = ((JSONObject)respJson).getString("error_description");
			/*state = respJson.getString("status").equals("ok")? STATE_OK:STATE_REQUEST_ERROR;
			if(state == STATE_REQUEST_ERROR)
			{
				errorDescription[0] = respJson.getString("error_description");
			}*/
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return state;
	}
	private void sendResponse(String action, String response, String queryId, int requestType)
	{
		Intent intentResponse = new Intent();
		intentResponse.setAction(action);
		intentResponse.putExtra(RESPONSE_STATE, STATE_OK);
		intentResponse.putExtra(REQUEST_TYPE, requestType);
		if(queryId != null) intentResponse.putExtra(RequestQuerue.QUERY_ID, queryId);
		intentResponse.putExtra(RESPONSE, response);
		sendBroadcast(intentResponse);
	}
	private void sendRequestBack(Intent intent, String action, String errorDescription, int state)
	{
		Intent intentResponse = new Intent();
		intentResponse.putExtras(intent.getExtras());
		intentResponse.setAction(action);
		intentResponse.putExtra(RESPONSE_STATE, state);
		intentResponse.putExtra(RESPONSE, errorDescription);
		sendBroadcast(intentResponse);
	}
	private String sendGetRequest(String url) throws ClientProtocolException, IOException
	{
		String responseString = null;
		
		HttpResponse response;
		response = httpClient.execute(new HttpGet(url));
		HttpEntity httpEntity = response.getEntity();
		responseString = EntityUtils.toString(httpEntity);
		return responseString;
	}
	private List<NameValuePair> formPairsFromJson(String json)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(json);
			Iterator<String> iter = jsonObj.keys();
			while(iter.hasNext())
			{
				String key = iter.next();
				nameValuePairs.add(new BasicNameValuePair(key, jsonObj.getString(key)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nameValuePairs;
	}
	private List<NameValuePair> formStandartServerPostPairs(String json)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("p_json", json));
	    return nameValuePairs;
	}
	private String sendPostPairsRequest(String url, List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException {
	    HttpPost httppost = new HttpPost(url);
	    
	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    HttpResponse response = httpClient.execute(httppost);
	    HttpEntity httpEntity = response.getEntity();
		return EntityUtils.toString(httpEntity);
	}
	private String transformURLParamsToJson(String urlParams)
	{
		String nameValue[] = urlParams.split("&");
		JSONObject urlJson = new JSONObject();
		for(int i=0; i < nameValue.length; i++)
		{
			String params[] = nameValue[i].split("=");
			try {
				if(params.length > 1) urlJson.put(params[0], params[1]);
				else if(params.length > 0) urlJson.put("error", params[0]);
				else urlJson.put("error", "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return urlJson.toString();
	}
}
