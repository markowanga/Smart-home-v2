package com.example.marcin.smarthomeandroid.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcin.smarthomeandroid.data.MySharedPreferences;
import com.example.marcin.smarthomeandroid.data.Names;
import com.example.marcin.smarthomeandroid.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Marcin on 15.05.2017.
 */
public class ControlFragment extends Fragment {
    TextView textViewIntercom, textViewGate, textViewWicket;
    Button buttonAnalyse;
    View view;
    WebSocketClient mWebSocketClient;
    OnControlListner listner;


    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAnalyse = (Button) view.findViewById(R.id.buttonAnalyse);
        textViewIntercom = (TextView) view.findViewById(R.id.textViewIntercom);
        textViewGate = (TextView) view.findViewById(R.id.textViewGate);
        textViewWicket = (TextView) view.findViewById(R.id.textViewWicket);

        setButtonAnalyse(listner.isServiceRunning());
        connectWebSocket();
    }

    public void setState(String state) {
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

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://" + Names.SERVER_NAME + "/smartPhone?email=" +
                    MySharedPreferences.getLoginEmail(getContext()) +
                    "&password=" + MySharedPreferences.getLoginPassword(getContext()));
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setState(state);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String message = s;
                getActivity().runOnUiThread(new Runnable() {
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

    public void sendCommandViaSocket(String command) {
        if (mWebSocketClient!=null) {
            try {
                mWebSocketClient.send(String.valueOf(new JSONObject("{command: "+command+"}")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setButtonAnalyse(boolean isServiceRunning) {
        if (isServiceRunning)
            buttonAnalyse.setText(R.string.stopAnalyzeLocation);
        else buttonAnalyse.setText(R.string.startAnalyzeLocation);
    }

    public interface OnControlListner {
        public boolean isServiceRunning();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listner = (OnControlListner) context;
    }
}
