package HttpConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class ServiceWithoutParameters extends AsyncTask<Void, Void, String>{
	Context context;
	ServiceMethodListener listener;
	String url;
	//ProgressDialog progress;
	String classname;
	String methodname;
	CustomAlertDialog adialog;
	ProgressDialog dialog;

	public ServiceWithoutParameters(Context _context, String _url,String _classname,String _methodname) {
		context = _context;
		url = _url;
		classname = _classname;
		methodname = _methodname;
//		progress=new ProgressDialog(_context,R.style.MyTheme);
//		progress.setCancelable(false);
//		progress.setMessage("Loading..");
	}

	@Override                                 
	protected void onPreExecute() {
		super.onPreExecute();
		ConnectionDetector cd;
		Boolean isInternetPresent;


		cd = new ConnectionDetector(context);
		isInternetPresent = cd.isConnectingToInternet();
		adialog=new CustomAlertDialog(context);


		if(isInternetPresent == true){

			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();

		}else{
			adialog.showOkDialog("Please check your internet connection");

		}
	}           

	@Override
	protected String doInBackground(Void... params) {
		try {
			listener = (ServiceMethodListener) context;
		} catch (Exception e) {
			e.printStackTrace();
		}

		String responseBody = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams httpParams = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
			HttpConnectionParams.setSoTimeout(httpParams, 60000);
			HttpGet httppost = new HttpGet(url);
	
			HttpResponse response = httpclient.execute(httppost);
			responseBody = EntityUtils.toString(response.getEntity());
			Log.i("JJA", "In HttpPostMethodCalls Response is ::"+responseBody);
			return responseBody;
		} catch(ConnectTimeoutException e){
			//progress.dismiss();
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseBody;

	}  

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		dialog.dismiss();
		Log.i("FAP", " result responseBody"+result);
		try
		{
		if( result.equalsIgnoreCase(null)){
			String message = "Connection time out !!!!!!!";
		}else{
			listener.getResponse(result, classname, methodname);
		}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}


}
