package com.example.chava.locationprivacy;
import HttpConnection.ServiceMethodListener;
import HttpConnection.ServiceWithoutParameters;
import HttpConnection.CustomAlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class Register extends AppCompatActivity implements ServiceMethodListener{
    EditText edUsername,edFname,edLname,edPhone;
    EditText edEmail;
    EditText edpassword;
    Button registerSubmit;
    Button login;
    String firstName, lastName, username, email, password, phone;
    CustomAlertDialog customAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edFname = (EditText)findViewById(R.id.fname);
        edLname = (EditText)findViewById(R.id.lname);
        edPhone = (EditText)findViewById(R.id.phone);
        edUsername = (EditText)findViewById(R.id.name);
        edEmail = (EditText)findViewById(R.id.email);
        edpassword = (EditText)findViewById(R.id.password);
        registerSubmit = (Button)findViewById(R.id.btnRegister);
        login = (Button)findViewById(R.id.btnLinkToLoginScreen);
        customAlertDialog = new CustomAlertDialog(Register.this);
        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = edFname.getText().toString();
                lastName = edLname.getText().toString();
                username = edUsername.getText().toString();
                email = edEmail.getText().toString();
                password = edpassword.getText().toString();
                phone = edPhone.getText().toString();
                String url1 = getResources().getString(R.string.base_url)+"register.php?"+"firstname="+firstName+"&lastname="+lastName+"&username="+username+"&email="+email+"&password="+password+"&phone="+phone+"";
                ServiceWithoutParameters postmethods = new ServiceWithoutParameters(Register.this, url1 , "RegClass","RegMethod");
                postmethods.execute();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
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
        Register.this.finish();
    }
    public void getResponse(String data, String classname, String methodname) {
        if (classname.equalsIgnoreCase("Regclass")) {
            if (methodname.equalsIgnoreCase("Regmethod")) {
                try {
                    JSONObject job = new JSONObject(data);
                    String status=job.getString("status");
                    String message=job.getString("message");

                    if(status.equalsIgnoreCase("200")){
                        customAlertDialog.showOkDialog(message);
                        Intent intent = new Intent(Register.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        customAlertDialog.showOkDialog(message);
                    }
                }catch (Exception e){

                }

            }
        }
    }

}
