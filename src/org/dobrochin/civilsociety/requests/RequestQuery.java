package org.dobrochin.civilsociety.requests;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/*
 * Приемник, подписан на изменение статуса подключения и сообщения о необходимости сохранить запрос в очереди.
 * как только появляется подключение, пытается отослать  очередь запросов на сервер
 * при удачной отправке запроса должен получать сообщение о необходимости очистить очередь.
 * Для того, чтобы избежать дублирования запросов, все, что отсылается в сервис отправки запросов
 * записывается под другим именем. При неудачной отправке или при повторном запуске приложения, перезаписывается под основным именем.
 * 
 * При каждом отчете о удачной отправке, проверяем, не добавилось ли в список что-то еще
 * если добавилось - пробуем выслать еще раз, если нет, отправляем отчет о том, что все изменения сохрарены на сервере
 */
public class RequestQuery extends BroadcastReceiver{
	private static final String QUERY_FILE = "query";
	private static final String QUERY_CONNECTION_WAITING = "connectionWaiting";
	private static final String QUERY_RESPONSE_WAITING = "responseWaiting";
	
	public static final String ACTION_ADD = "org.dobrochin.civilsociety.requests.RequestQuery.ACTION_ADD";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		putRequestInQuery(context, intent);
		if(isConnected(context))
		{
			
		}
	}
	
	private String getRequestQuery(Context context)
	{
		SharedPreferences reqQuery = context.getSharedPreferences(QUERY_FILE, Context.MODE_PRIVATE);
		return reqQuery.getString(QUERY_CONNECTION_WAITING, "");
	}
	//читаем уже записанную очередь, если она есть, добавляем запросы в нее. если нет, просто дописываем свой запросы
	//записываются только post запросы с параметром p_json
	private void putRequestInQuery(Context context, Intent intent)
	{
		SharedPreferences reqQuery = context.getSharedPreferences(QUERY_FILE, Context.MODE_PRIVATE);
		String queryString = reqQuery.getString(QUERY_CONNECTION_WAITING, "");
		try {
			JSONObject requestJSON = getRequestJSON(intent); //Может сгененить JsonException нет запроса - нечего сохранять. Надо предупредить пользователя
			JSONObject queryJSON = getQueryJSON(context);
			Iterator<String> keys = requestJSON.keys();
			while(keys.hasNext())
			{
				Log.i("wtf", keys.next());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private JSONObject getQueryJSON(Context context)
	{
		String queryString = getRequestQuery(context);
		JSONObject queryJSON;
		try {
			queryJSON = new JSONObject(queryString);	
		} catch (JSONException e) {
			queryJSON = new JSONObject(); //если не можем распаристь очередь запросов - просто забываем ее
		}
		return queryJSON;
	}
	private JSONObject getRequestJSON(Intent intent) throws JSONException
	{
		String newRequest = intent.getStringExtra(RequestService.REQUEST_JSON);
		JSONObject queryJSON = null;
		queryJSON = new JSONObject(newRequest);	
		return queryJSON;
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
}
