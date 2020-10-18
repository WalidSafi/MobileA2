package com.example.sofe4640bookstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.GenericLifecycleObserver;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class SignupUsers extends AppCompatActivity {
    MySQLiteHelper mySQLiteHelper;

    EditText txtFirstName;
    EditText txtLastName;
    EditText txtUserName;
    EditText txtPassword;
    EditText txtEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_users);
        mySQLiteHelper = new MySQLiteHelper(this,null,null,1);

        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtUserName = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtEmail = (EditText) findViewById(R.id.txtEmail);

    }


    public void addNewUser(View view) {

        Users user = new Users(txtFirstName.getText().toString(),
                txtLastName.getText().toString(),
                txtUserName.getText().toString(),
                txtPassword.getText().toString(),
                txtEmail.getText().toString());
        boolean status =  mySQLiteHelper.addRecord(user);
        if (status)
            Toast.makeText(this,"Record Added",Toast.LENGTH_LONG).show();
            else
            Toast.makeText(this,"Failed to add record",Toast.LENGTH_LONG).show();


    }


}