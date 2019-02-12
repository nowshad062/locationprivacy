package com.example.chava.locationprivacy;

import HttpConnection.CustomAlertDialog;
import HttpConnection.ServiceMethodListener;
import HttpConnection.ServiceWithoutParameters;
import android.net.wifi.WifiManager;
import java.net.NetworkInterface;
import android.os.Debug;
import android.text.format.Formatter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MaxLength;
import eu.inmite.android.lib.validations.form.annotations.MaxNumberValue;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class MainActivity extends AppCompatActivity implements ServiceMethodListener{
    @NotEmpty(messageId = R.string.validation_not_empty, order = 1)
    EditText edUsername;
    @NotEmpty(messageId = R.string.validation_not_empty, order = 2)
    @MaxLength(value = 20, order = 3, messageId = R.string.validation_max_number)
    EditText edPassword;
    Button loginSubmit;
    Button register;
    String username, password;
    Boolean isValid;
    String[] list1;
    CustomAlertDialog alertDialog;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.
                formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.e("ip address" , ip);*/

        edUsername = (EditText)findViewById(R.id.email);
        edPassword = (EditText)findViewById(R.id.password);
        loginSubmit = (Button)findViewById(R.id.btnLogin);
        register = (Button)findViewById(R.id.btnLinkToRegisterScreen);
        alertDialog = new CustomAlertDialog(MainActivity.this);
        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = FormValidator.validate(MainActivity.this, new SimpleErrorPopupCallback(getApplicationContext(), true));
                if(isValid){
                    username = edUsername.getText().toString();
                    password = edPassword.getText().toString();
                    loginServiceCall();
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        moveTaskToBack(true);
        MainActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void loginServiceCall(){


        String url1 = getResources().getString(R.string.base_url) +"login.php?"+"username="+username+"&password="+password+"";
        //Log.d("Vikki", url1);
        ServiceWithoutParameters postmethods = new ServiceWithoutParameters(MainActivity.this, url1, "Loginclass", "Loginmethod");
        postmethods.execute();
    }
    public void getResponse(String data, String Loginclass, String Loginmethod){
        try{
            JSONObject job = new JSONObject(data);
            String notes = job.getString("record");
            //jarray = new JSONArray(notes);
            JSONArray ja = new JSONArray(notes);
            int len = ja.length();
            list1 = new String[len];
            for (int i = 0; i < len; i++) {
                JSONObject jb1 = ja.getJSONObject(i);
                list1[i] = jb1.getString("username");
            }
            String status=job.getString("status");
            String message=job.getString("message");
            if(status.equalsIgnoreCase("200")) {
                Intent intent = new Intent(MainActivity.this, SelectLocationActivity.class);
                intent.putExtra("username",list1[0]);
                startActivity(intent);
            }
            else{
                alertDialog.showOkDialog("Something went wrong, please try again");
            }
    }
        catch (Exception e){
            Log.d("JSON Exception", e.toString());

        }
    }

}
