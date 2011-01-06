package jp.leafnet.droid.dialog.factory;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogFactory {

	public static ProgressDialog getSpinnerInstance(final Context context, final String message) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setProgress(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(message);
		dialog.setCancelable(true);
		return dialog;
	}
}
