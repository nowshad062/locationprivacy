package HttpConnection;






import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.chava.locationprivacy.R;


public class CustomAlertDialog {

	private static final String TAG = CustomAlertDialog.class.getSimpleName();

	Context context;

	Activity mActivity;

	public AlertDialog.Builder builder;

	public AlertDialog alertDialog;

	public static boolean flag = false;

	public CustomAlertDialog(Context context) {
		this.context = context;
		builder = new AlertDialog.Builder(context);
	}

	public void showOkDialog(String msg) {

		if (msg == null) {
			msg = "A problem has occured, please check connection or try again later";
		}
		try {

		//	builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle(context.getResources().getString(R.string.app_name));

			builder.setMessage(msg);

			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alertDialog = builder.create();
			alertDialog.show();
		} catch (Exception e) {
//			Log.e(TAG, "---alertExecption--");
		}
	}

	public boolean isShowing() {

		return alertDialog.isShowing();
	}

	public void dismiss() {
		alertDialog.dismiss();
	}
}
