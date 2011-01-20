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
import android.preference.Preference.OnPreferenceClickListener;

public class UserPrefActivity extends PreferenceActivity {

	public void onCreate(Bundle savedInstanceState) {
		this.setTheme(android.R.style.Theme_Black);
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.layout.userprefs);
		this.createPasswordPreference();
	}

	private void createPasswordPreference() {
		final String rawPassKey = this.getString(R.string.insta_raw_password);
		final String passKey = this.getString(R.string.insta_password);
		final Context context = this;
		Preference preference = this.findPreference(rawPassKey);
		preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				((EditTextPreference)preference).setText("");
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = preferences.edit();
				SecretKeySpec keySpec = CryptoUtil.createKeySpec(context);
				String encPass = CryptoUtil.encodeString(newValue.toString(), keySpec);
				editor.putString(passKey, encPass);
				editor.commit();
				return false;
			}
		});
		preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				String encodePass = preferences.getString(passKey, "");
				if (!StringUtil.isEmpty(encodePass)) {
					SecretKeySpec keySpec = CryptoUtil.createKeySpec(context);
					String password = CryptoUtil.decodeString(encodePass, keySpec);
					((EditTextPreference)preference).setText(password);
				}
				return false;
			}
		});
	}
}
