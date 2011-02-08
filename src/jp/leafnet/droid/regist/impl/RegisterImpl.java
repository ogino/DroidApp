package jp.leafnet.droid.regist.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;

import jp.leafnet.droid.R;
import jp.leafnet.droid.regist.Register;
import jp.leafnet.droid.util.CryptoUtil;
import jp.leafnet.droid.util.StringUtil;

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

public abstract class RegisterImpl implements Register {

	private final static String NO_USERNAME = "Username not found...";
	private final static String NO_PASSWORD = "Password not found...";

	private Logger logger = Logger.getLogger(RegisterImpl.class.getPackage().getName());
	private Context context;
	private String url;
	private Map<String, String> resultMap;
	private Handler handler;

	protected String username;
	protected String password;
	protected String apiRoot;
	protected String apiKey;

	protected RegisterImpl(final Context context, final String url, final Handler handler) {
		super();
		this.context = context;
		this.url = url;
		this.resultMap = new HashMap<String, String>();
		this.handler = handler;
	}

	@Override
	public void run() {
		this.request();
		this.handler.sendMessage(this.createMessage());
	}
	
	public Map<String, String> getResultMap() {
		return this.resultMap;
	}
	
	protected void createUserInfo(final int usernameId, final int passwordId) {
		this.username = this.context.getString(usernameId);
		this.password = this.context.getString(passwordId);
	}

	private void request() {
		this.resultMap.clear();
		SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		String username = userPreferences.getString(this.username, NO_USERNAME);
		String password = this.createPassword(userPreferences);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(this.apiRoot);
		String title = this.context.getString(R.string.success_title);
		String message = "";
		try {
			httpPost.setEntity(createEntity(username, password));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_CREATED && statusCode != HttpStatus.SC_OK) {
				title = this.context.getString(R.string.error_title);
				message = String.format(this.context.getString(R.string.regist_err_message), statusCode);
			} else {
				message = String.format(this.context.getString(R.string.regist_suc_message), statusCode);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		} finally {
			this.resultMap.put("title", title);
			this.resultMap.put("message", message);
		}
	}
	
	private String createPassword(final SharedPreferences userPreferences) {
		String encodePass = userPreferences.getString(this.password, NO_PASSWORD);
		if (encodePass.equals(NO_PASSWORD)) return "";
		SecretKeySpec keySpec = CryptoUtil.createKeySpec(context);
		return CryptoUtil.decodeString(encodePass, keySpec);
	}

	private MultipartEntity createEntity(final String username, final String password)
			throws UnsupportedEncodingException {
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("username", new StringBody(username));
		entity.addPart("password", new StringBody(password));
		if (!StringUtil.isEmpty(apiKey)) entity.addPart("apikey", new StringBody(this.apiKey));
		entity.addPart("url", new StringBody(this.url));
		return entity;
	}

	private Message createMessage() {
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("operation", "register");
		message.setData(bundle);
		return message;
	}
}
