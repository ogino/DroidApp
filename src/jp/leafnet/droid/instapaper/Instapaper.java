package jp.leafnet.droid.instapaper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.leafnet.droid.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class Instapaper implements Runnable {

	private final static String NO_USERNAME = "Username not found...";
	private final static String NO_PASSWORD = "Password not found...";

	private Logger logger = Logger.getLogger(Instapaper.class.getPackage().getName());
	private Context context;
	private String url;
	private Map<String, String> resultMap;
	private Handler handler;

	public Instapaper(final Context context, final String url, final Handler handler) {
		super();
		this.context = context;
		this.url = url;
		this.resultMap = new HashMap<String, String>();
		this.handler = handler;
	}

	private void requestInstapaper() {
		this.resultMap.clear();
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(this.context);
		String username = userPreference.getString(this.context.getResources().getString(R.string.insta_username), NO_USERNAME);
		String password = userPreference.getString(this.context.getResources().getString(R.string.insta_password), NO_PASSWORD);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("https://www.instapaper.com/api/add");
		String title = this.context.getResources().getString(R.string.success_title);
		String message = "";
		MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart("username", new StringBody(username));
			entity.addPart("password", new StringBody(password));
			entity.addPart("url", new StringBody(this.url));
			httpPost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_CREATED) {
				title = this.context.getResources().getString(R.string.error_title);
				message = String.format(this.context.getResources().getString(R.string.insta_err_message), statusCode);
			} else {
				message = String.format(this.context.getResources().getString(R.string.insta_suc_message), statusCode);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		} finally {
			this.resultMap.put("title", title);
			this.resultMap.put("message", message);
		}
	}

	@Override
	public void run() {
		this.requestInstapaper();
		this.handler.sendMessage(this.createMessage());
	}

	private Message createMessage() {
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("operation", "instapaper");
		message.setData(bundle);
		return message;
	}

	public Map<String, String> getResultMap() {
		return this.resultMap;
	}
}