package com.example.marcin.smarthomeandroid.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcin.smarthomeandroid.data.MySharedPreferences;
import com.example.marcin.smarthomeandroid.data.Names;
import com.example.marcin.smarthomeandroid.R;
import com.example.marcin.smarthomeandroid.background.MyService;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements ExitDialog.ExitDialogListener {

    WebSocketClient mWebSocketClient;
    TextView textViewIntercom, textViewGate, textViewWicket;
    Button buttonAnalyse;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/
        view = findViewById(android.R.id.content);
        buttonAnalyse = (Button) findViewById(R.id.buttonAnalyse);

        textViewIntercom = (TextView) findViewById(R.id.textViewIntercom);
        textViewGate = (TextView) findViewById(R.id.textViewGate);
        textViewWicket = (TextView) findViewById(R.id.textViewWicket);

        connectWebSocket();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://" + Names.SERVER_NAME + "/smartPhone?email=" +
                    MySharedPreferences.getLoginEmail(getApplicationContext()) +
                    "&password=" + MySharedPreferences.getLoginPassword(getApplicationContext()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getString("command").equals("new_state")) {
                        final String state = object.getString("state");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (state) {
                                    case Names.GATE_CLOSE_STATE:
                                        textViewGate.setText("Gate: close");
                                        break;
                                    case Names.GATE_MOVE_STATE:
                                        textViewGate.setText("Gate: move");
                                        break;
                                    case Names.GATE_OPEN_STATE:
                                        textViewGate.setText("Gate: open");
                                        break;
                                    case Names.WICKET_CLOSE_STATE:
                                        textViewWicket.setText("Wicket: close");
                                        break;
                                    case Names.WICKET_OPEN_STATE:
                                        textViewWicket.setText("Wicket: open");
                                        break;
                                    case Names.INTERCOM_QUIET_STATE:
                                        textViewIntercom.setText("Intercom: quiet");
                                        break;
                                    case Names.INTERCOM_RING_STATE:
                                        textViewIntercom.setText("Intercom: ring");
                                        break;
                                    default:
                                        Log.e("get from websocket", state);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("websocket", message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                mWebSocketClient = null;
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void sendCommandViaSocket(String command) {
        if (mWebSocketClient!=null) {
            try {
                mWebSocketClient.send(String.valueOf(new JSONObject("{command: "+command+"}")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.drawer_logout: {
                Log.e("log", "logout");
                logout();
                return true;
            }
            case R.id.drawer_maps: {
                startActivity(new Intent(this, MapsActivity.class));
                return true;
            }
            case R.id.drawer_state_history: {
                startActivity(new Intent(this, StateHistoryActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void logout() {
        MySharedPreferences.rememberIncorrectData(getApplicationContext());
        finish();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        DialogFragment newFragment = new ExitDialog();
        newFragment.show(getSupportFragmentManager(), "missiles");
    }

    @Override
    public void exit() {
        finish();
    }

    public void openGate(View view) {
        sendCommandViaSocket(Names.GATE_OPEN_COMMAND);
    }

    public void openWicket(View view) {
        sendCommandViaSocket(Names.WICKET_OPEN_COMMAND);
    }

    public void closeGate(View view) {
        sendCommandViaSocket(Names.GATE_CLOSE_COMMAND);
    }

    public void analyserClick(View view) {
        if (!isServiceRunning(MyService.class)) {
            startService(new Intent(getApplicationContext(), MyService.class));
            Snackbar.make(view, "Start analizy", Snackbar.LENGTH_SHORT).show();
            buttonAnalyse.setText(R.string.stopAnalyzeLocation);
            MySharedPreferences.setLocationAnalise(true, getApplicationContext());
        } else {
            stopService(new Intent(getApplicationContext(), MyService.class));
            Log.e("main", "koniec");
            Snackbar.make(view, "Koniec analizy", Snackbar.LENGTH_SHORT).show();
            buttonAnalyse.setText(R.string.startAnalyzeLocation);
            MySharedPreferences.setLocationAnalise(false, getApplicationContext());
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;

        return false;
    }
}