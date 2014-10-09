package org.dobrochin.civilsociety.views;


import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class DialogWebView extends Dialog{
	private WebView mainView;
	private ProgressBar progressBar;
	private AuthFinishListener finishListener;
	private String fakeUserAgent = "something";
	private String realUserAgent;
	public DialogWebView(Context context, String resultUrl, AuthFinishListener authFinishListener) {
		super(context);
		// TODO Auto-generated constructor stub
		finishListener = authFinishListener;
		init(context, resultUrl);
	}
	private void init(Context context, final String resultUrl)
	{
		
		RelativeLayout rl = new RelativeLayout(context);
		mainView = new WebView(context);
		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
		rl.addView(mainView);
		rl.addView(progressBar);
		RelativeLayout.LayoutParams lppb = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
		lppb.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		progressBar.setLayoutParams(lppb);
		
		RelativeLayout.LayoutParams lpwv = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
		lpwv.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		lpwv.height = RelativeLayout.LayoutParams.MATCH_PARENT;
		lpwv.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		mainView.setLayoutParams(lpwv);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mainView.setVisibility(View.INVISIBLE);
		realUserAgent = mainView.getSettings().getUserAgentString();	
		mainView.getSettings().setJavaScriptEnabled(true);
		//mainView.clearCache(true);
		mainView.setWebViewClient(new WebViewClient() {
			@Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		        // do your handling codes here, which url is the requested url
		        // probably you need to open that url rather than redirect:
		    	if(url.startsWith(resultUrl))
		    	{
		    		finishListener.onAuthFinish(url);
		    		DialogWebView.this.dismiss();
		    	}
		    	else
		    	{
		    		view.loadUrl(url);
		    	}
		        return false; // then it is not handled by default action
		   }
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mainView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
				mainView.setVisibility(View.INVISIBLE);
			}
		});
		setContentView(rl);
	}
	public void setURL(String url, boolean needFakeUserAgent)
	{
		if(needFakeUserAgent) mainView.getSettings().setUserAgentString(fakeUserAgent);
		else mainView.getSettings().setUserAgentString(realUserAgent);
		mainView.loadUrl(url);
	}
	public void setHtml(String text)
	{
		mainView.loadData(text, "text/html", "utf-8");
	}
	public interface AuthFinishListener
	{
		public void onAuthFinish(String authData);
	}
}
