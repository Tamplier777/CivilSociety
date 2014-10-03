package org.dobrochin.civilsociety.social;


import java.io.IOException;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.views.DialogWebView.AuthFinishListener;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnPeopleLoadedListener;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class GooglePAuth extends CurrentAuth implements ConnectionCallbacks, OnConnectionFailedListener, OnPeopleLoadedListener{
	private PlusClient mPlusClient;
	private BaseActivity activity;
	private AuthFinishListener listener;
	private ProgressDialog mConnectionProgressDialog;
	public static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	@Override
	public String getName(JSONObject data) {
		String name="";
		try {
			name = data.getString("name");
		} catch (JSONException e) {}
		return name;
	}

	@Override
	protected String getProfileRequest(String token) {return null;}
	@Override
	protected String getAuthURL() {
		return null;
	}
	@Override
	protected String getRedirectURL() {return null;}

	@Override
	protected String getAuthToken(String authResponse) {return null;}
	@Override
	public void showAuthDialog(BaseActivity activity, AuthFinishListener listener) {
		this.activity = activity;
		this.listener = listener;
		if(mPlusClient == null)
		{
			mPlusClient = new PlusClient.Builder(activity, this, this)
			.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
			.setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_ME)
	        .build();
			mConnectionProgressDialog = new ProgressDialog(activity);
	        mConnectionProgressDialog.setMessage("Пробуем подключиться...");
		}
        mPlusClient.connect();
        mConnectionProgressDialog.show();
	}
	@Override
	public void sendGetProfileRequest(final BaseActivity activity, final String authData) {
		
		mPlusClient.loadPeople(this, "me");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if (result.hasResolution()) {
            try {
                result.startResolutionForResult(activity, REQUEST_CODE_RESOLVE_ERR);
                mConnectionProgressDialog.dismiss();
            } catch (SendIntentException e) {
            	e.printStackTrace();
                mPlusClient.connect();
            }
        }
		else
		{
			mConnectionProgressDialog.dismiss();
			Toast.makeText(activity, "Подключение не удалось", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		getAuthToken();
	}

	private void getAuthToken()
	{
		AsyncTask<Void, Integer, String> task = new AsyncTask<Void, Integer, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				String token = null;
				try {
					 token = GoogleAuthUtil.getToken(activity, mPlusClient.getAccountName(), "oauth2:".concat(Scopes.PLUS_LOGIN).concat(" ").concat(Scopes.PLUS_ME));
				} catch (UserRecoverableAuthException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return token;
			}
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				mConnectionProgressDialog.dismiss();
				listener.onAuthFinish(result);
			}
		};
		task.execute();
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeopleLoaded(ConnectionResult status,
			PersonBuffer personBuffer, String nextPageToken) {
		if (status.getErrorCode() == ConnectionResult.SUCCESS) {
			Person p =personBuffer.get(0);
			JSONObject person = new JSONObject();
			try {
				person.put("name", p.getDisplayName());
				person.put("birthday", p.getBirthday());
				person.put("gender", p.getGender());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//p.getPlacesLived().get(p.getPlacesLived().size()-1);
			
			Intent intentResponse = new Intent();
			intentResponse.setAction(activity.getActivityName());
			intentResponse.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_SOCIAL_PROFILE);
			intentResponse.putExtra(RequestService.RESPONSE_STATE, RequestService.STATE_OK);
			intentResponse.putExtra(RequestService.RESPONSE, person.toString());
			activity.sendBroadcast((intentResponse));
			
		}
	}
}
