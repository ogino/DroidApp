package jp.leafnet.droid.news.conf;

import javax.crypto.spec.SecretKeySpec;

import jp.leafnet.droid.R;
import jp.leafnet.droid.util.CryptoUtil;
import jp.leafnet.droid.util.StringUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

public class UserPrefActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setTheme(android.R.style.Theme_Black);
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.layout.userprefs);
		this.createPreference(this.getString(R.string.insta_raw_password), this.getString(R.string.insta_password));
		this.createPreference(this.getString(R.string.readit_raw_password), this.getString(R.string.readit_password));
		this.clearRawPassword(this.getString(R.string.insta_raw_password));
		this.clearRawPassword(this.getString(R.string.readit_raw_password));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		this.restorePassword(this.getString(R.string.insta_raw_password), this.getString(R.string.insta_password));
		this.restorePassword(this.getString(R.string.readit_raw_password), this.getString(R.string.readit_password));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		this.clearRawPassword(this.getString(R.string.insta_raw_password));
		this.clearRawPassword(this.getString(R.string.readit_raw_password));
	}
	
	private void clearRawPassword(final String key) {
		Preference preference = this.findPreference(key);
		((EditTextPreference)preference).setText("");
	}

	private void createPreference(final String rawPassKey, final String passKey) {
		final Context context = this;
		Preference preference = this.findPreference(rawPassKey);
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				savePassword(context, newValue.toString(), passKey);
				return false;
			}
		});
	}
	
	private void savePassword(final Context context, final String password, final String passKey) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		SecretKeySpec keySpec = CryptoUtil.createKeySpec(context);
		String encPass = CryptoUtil.encodeString(password, keySpec);
		editor.putString(passKey, encPass);
		editor.commit();
	}

	private void restorePassword(final String rawPassKey, final String passKey) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String encodePass = preferences.getString(passKey, "");
		Preference preference = this.findPreference(rawPassKey);
		createPasswordField(preference, encodePass);
	}

	private void createPasswordField(Preference preference, final String encodePass) {
		if (!StringUtil.isEmpty(encodePass)) {
			SecretKeySpec keySpec = CryptoUtil.createKeySpec(this);
			String password = CryptoUtil.decodeString(encodePass, keySpec);
			if (!StringUtil.isEmpty(password))
				((EditTextPreference)preference).setText(password);
		}
	}
}
