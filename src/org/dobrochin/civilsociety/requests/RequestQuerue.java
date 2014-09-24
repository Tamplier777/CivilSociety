package org.dobrochin.civilsociety.requests;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.dobrochin.civilsociety.R;
import org.dobrochin.civilsociety.requests.models.RequestWrapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/*
 * ��������, �������� �� ��������� ������� ����������� � ��������� � ������������� ��������� ������ � �������.
 * ��� ������ ���������� �����������, �������� ��������  ������� �������� �� ������
 * ��� ������� �������� ������� ������ �������� ��������� � ������������� �������� �������.
 * ��� ����, ����� �������� ������������ ��������, ���, ��� ���������� � ������ �������� ��������
 * ������������ ��� ������ ������. ��� ��������� �������� ��� ��� ��������� ������� ����������, ���������������� ��� �������� ������.
 * 
 * ��� ������ ������ � ������� ��������, ���������, �� ���������� �� � ������ ���-�� ���
 * ���� ���������� - ������� ������� ��� ���, ���� ���, ���������� ����� � ���, ��� ��� ��������� ��������� �� �������
 */
public class RequestQuerue extends BroadcastReceiver{
	private static final String QUERY_FILE = "query";
	private static final String QUERY_CONNECTION_WAITING = "connectionWaiting";
	private static final String QUERY_RESPONSE_WAITING = "responseWaiting";
	
	public static final String QUERY_ID = "query_id";
	
	public static final String ACTION_ADD = "org.dobrochin.civilsociety.requests.RequestQuery.ACTION_ADD";
	public static final String ACTION_CHECK_RESPONSE = "org.dobrochin.civilsociety.requests.RequestQuery.ACTION_CHECK_RESPONSE";
	private static final String ACTION_CONNECT_STATE_CHENGED = "android.net.conn.CONNECTIVITY_CHANGE";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(ACTION_CONNECT_STATE_CHENGED) && isConnected(context))
		{
			//�������� ��� ������� �� ���� � ���������� � ������
			sendQueryToService(context, getRequestQuery(context, QUERY_CONNECTION_WAITING));
		}
		else if(action.equals(ACTION_ADD) && isConnected(context))
		{
			//���������� ������ ����� �������������� � ������ (����� ����, �� ����� ����������� �������, ����� ���������.)
			sendQueryToService(context, intent.getStringExtra(RequestService.REQUEST_JSON));
		}
		else if(action.equals(ACTION_ADD))
		{
			//����������� ���, ������ ���������� ������ � �������
			putRequestInQuery(context, intent.getStringExtra(RequestService.REQUEST_JSON));
		}
		else if(action.equals(ACTION_CHECK_RESPONSE))
		{
			//������ ����� �� ������� �������� ��������, ��������� �� ������� ������, ������������
			checkResponse(context, intent);
		}
		
	}
	private boolean isConnected(Context context)
	{
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
	private String getRequestQuery(Context context, String queryName)
	{
		SharedPreferences reqQuery = context.getSharedPreferences(QUERY_FILE, Context.MODE_PRIVATE);
		return reqQuery.getString(queryName, "");
	}
	private void sendQueryToService(Context context, String requestQuery)
	{
		if(requestQuery != null && !requestQuery.equals(""))
		{
			String requestHash = RequestWrapper.md5(requestQuery)+new Date().toString();
			Intent serviceIntent =  new Intent(context, RequestService.class);
			serviceIntent.putExtra(RequestService.BACK_ADDRESS, ACTION_CHECK_RESPONSE);
			serviceIntent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_SEND_POST_PAIRS);
			serviceIntent.putExtra(RequestService.REQUEST_JSON, requestQuery);
			serviceIntent.putExtra(QUERY_ID, requestHash);
			removeQuery(QUERY_CONNECTION_WAITING, context);
			saveQuery(requestHash, requestQuery, context);
			context.startService(serviceIntent);	
		}
	}
	//������ ��� ���������� �������, ���� ��� ����, ��������� ������� � ���. ���� ���, ������ ���������� ���� �������
	//������������ ������ post ������� � ���������� p_json
	private void putRequestInQuery(Context context, String request)
	{
		try {
			JSONObject requestJSON = getRequestJSON(request); //����� ��������� JsonException ��� ������� - ������ ���������. ���� ������������ ������������
			JSONObject queryJSON = getQueryJSON(context);
			Iterator<String> keys = requestJSON.keys();
			while(keys.hasNext())
			{
				String taskName = keys.next();
				//���� � ������� ��� ���� ������ � ����� ��������� � � ��� �������� ������, �� ���������� ����� ������ � ������.
				//���� ��������� ������, �� ������������ ������ ������
				if(queryJSON.has(taskName))
				{
					Object requestData = queryJSON.get(taskName); // ������� jsonException
					if(requestData instanceof JSONArray)
					{
						JSONArray requestJSONArray = (JSONArray)requestJSON.get(taskName);
						JSONArray requestArray = (JSONArray)requestData;
						requestArray.put(requestJSONArray.get(0)); //������� jsonException
						queryJSON.put(taskName, requestArray); //������� jsonException
					}
					else queryJSON.put(taskName, requestJSON.get(taskName)); //������� jsonException
				}
				else
				{
					queryJSON.put(taskName, requestJSON.get(taskName)); //������� jsonException
				}
			}
			saveQuery(QUERY_CONNECTION_WAITING, queryJSON.toString(), context);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private JSONObject getQueryJSON(Context context)
	{
		String queryString = getRequestQuery(context, QUERY_CONNECTION_WAITING);
		JSONObject queryJSON;
		try {
			queryJSON = new JSONObject(queryString);	
		} catch (JSONException e) {
			queryJSON = new JSONObject(); //���� �� ����� ���������� ������� �������� - ������ �������� ��
		}
		return queryJSON;
	}
	private JSONObject getRequestJSON(String request) throws JSONException
	{
		JSONObject queryJSON = null;
		queryJSON = new JSONObject(request);	
		return queryJSON;
	}
	private void removeQuery(String queryName, Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(QUERY_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(queryName);
	    editor.commit();
	}
	private void saveQuery(String queryName, String queryValue, Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(QUERY_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(queryName, queryValue);
	    editor.commit();
	}
	private void checkResponse(Context context, Intent intent)
	{
		String queryName = intent.getStringExtra(QUERY_ID);
		String queryValue = getRequestQuery(context, queryName);
		removeQuery(queryName, context);
		if(intent.getIntExtra(RequestService.RESPONSE_STATE, -1) == RequestService.STATE_OK &&
				!intent.getStringExtra(RequestService.RESPONSE).matches("error"))
		{
			//������ ������ ������, ���� ���������, �������� �� ��� �������, ��������� �������� ��� ������
			SharedPreferences settings = context.getSharedPreferences(QUERY_FILE, Context.MODE_PRIVATE);
			Map<String, ?> allEntries = settings.getAll();
			boolean isWaiting = false;
			Log.i("wtf", "queries:");
			for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
				String query = getRequestQuery(context, entry.getKey());
				Log.i("wtf", entry.getKey());
			    if( query == null || query.equals(""))  removeQuery(entry.getKey(), context);
			    else
			    {
			    	if(entry.getKey().equals(QUERY_CONNECTION_WAITING)) sendQueryToService(context, entry.getValue().toString());
			    	isWaiting = true;
			    }
			}
			if(!isWaiting) Toast.makeText(context, R.string.request_queue_sended_successfuly, Toast.LENGTH_SHORT).show();
		}
		else if(intent.getIntExtra(RequestService.RESPONSE_STATE, -1) == RequestService.STATE_CONNECTION_ERROR)
		{
			putRequestInQuery(context, queryValue); //���������� ������� ������� � �������
		}
		else if(intent.getIntExtra(RequestService.RESPONSE_STATE, -1) == RequestService.STATE_REQUEST_ERROR ||
				intent.getStringExtra(RequestService.RESPONSE).matches("error"))
		{
			//do nothing
		}
	}
}
