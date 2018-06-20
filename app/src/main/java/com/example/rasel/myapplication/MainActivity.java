package com.example.rasel.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import android.provider.Settings.Secure;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;


public class MainActivity extends AppCompatActivity {

    EditText txtUserName;
    Button btnSubmit;
    MySocket mySocket = new MySocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
        System.out.println("Android ID = " + android_id + "\n\n");

        mySocket.connect();

        txtUserName = (EditText) findViewById(R.id.editText);
        btnSubmit = (Button) findViewById(R.id.button);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = txtUserName.getText().toString().trim();

                if(username.compareTo("") != 0) {

                    String userInfo = "{\"androidId\": \""+ android_id +"\", \"username\": \""+ username +"\"}";

                    MySocket.socket.emit("setUser", userInfo);

                    Intent intent = new Intent(getApplication(), BuddyActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

}
