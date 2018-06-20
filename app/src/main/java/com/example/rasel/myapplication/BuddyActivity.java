package com.example.rasel.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rasel on 5/6/18.
 */

public class BuddyActivity extends AppCompatActivity {

    ListView listview;
    ListView listviewInvitation;
    final List<String> deviceList = new ArrayList<>();
    final List<String> playerList = new ArrayList<>();
    final List<String> invitationList = new ArrayList<>();
    final List<String> invitationDeviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy);

        final String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        listview = (ListView) findViewById(R.id.listview);
        listviewInvitation = (ListView) findViewById(R.id.listviewInvitation);

        final ArrayAdapter<String> listviewArrayAdapter = new ArrayAdapter<>(
                this,R.layout.support_simple_spinner_dropdown_item, playerList);
        listview.setAdapter(listviewArrayAdapter);

        final ArrayAdapter<String> listviewInvitationArrayAdapter = new ArrayAdapter<>(
                this,R.layout.support_simple_spinner_dropdown_item, invitationList);
        listviewInvitation.setAdapter(listviewInvitationArrayAdapter);

        MySocket.socket.emit("players", "Get All Active Players");
        MySocket.socket.on("players", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            listviewArrayAdapter.clear();
                            JSONObject data = (JSONObject) args[0];

                            JSONArray arr = data.getJSONArray("players");

                            for(int i=0; i<arr.length();i++) {
                                JSONObject player = (JSONObject) arr.get(i);

                                String deviceId = player.get("androidId").toString();
                                if(deviceId.compareTo(android_id) != 0) {

                                    playerList.add(player.get("username").toString());
                                    deviceList.add(deviceId);
                                }
                            }

                            listviewArrayAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String deviceId = deviceList.get(i);
                //MySocket.socket.emit("invite", deviceId);

                Intent intent = new Intent(getApplication(), ChessActivity.class);
                intent.putExtra("receiverDeviceId", deviceId);
                intent.putExtra("playerAs", "white");
                startActivity(intent);
            }
        });

        MySocket.socket.on("invitations", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            listviewInvitationArrayAdapter.clear();
                            JSONObject data = (JSONObject) args[0];

                            JSONObject invitations = data.getJSONObject("invitations");
                            JSONArray arr = invitations.getJSONArray(android_id);

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject invitation = (JSONObject) arr.get(i);
                                invitationList.add(invitation.getString("username"));
                                invitationDeviceList.add(invitation.getString("androidId"));
                            }

                            listviewInvitationArrayAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        listviewInvitation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String invitationDeviceId = invitationDeviceList.get(i);
                //MySocket.socket.emit("acceptInvitation", invitationDeviceId);

                Intent intent = new Intent(getApplication(), ChessActivity.class);
                intent.putExtra("receiverDeviceId", invitationDeviceId);
                intent.putExtra("playerAs", "black");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MySocket.socket.emit("invitations", "Get Invitations");
    }
}
