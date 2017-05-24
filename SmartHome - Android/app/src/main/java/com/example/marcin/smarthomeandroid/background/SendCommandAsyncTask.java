package com.example.marcin.smarthomeandroid.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.marcin.smarthomeandroid.data.MySharedPreferences;
import com.example.marcin.smarthomeandroid.data.Names;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Marcin on 15.05.2017.
 */

public class SendCommandAsyncTask extends AsyncTask<String, Void, Integer> {
    private final Context mContext;
    private String mPassword, mEmail;
    SendCommandListener listner;

    SendCommandAsyncTask(Context context, SendCommandListener loginListner) {
        mContext = context;
        listner = loginListner;
        mEmail = MySharedPreferences.getLoginEmail(context);
        mPassword = MySharedPreferences.getLoginPassword(context);
    }

    // result: ok, incorrect_login, incorrect_command
    @Override
    protected Integer doInBackground(String... command) {
        try {
            URL url = new URL("http://" + Names.SERVER_NAME + "/mobileController?email="
                    + mEmail + "&password=" + mPassword + "&command=" + command[0]);
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(1000);
            client.connect();
            Log.v("SendCommand", "Response code: " + client.getResponseCode());
            String string = readStream(client.getInputStream(), 1000);
            JSONObject obj = new JSONObject(string);
            if (obj.getString("result").equals("ok"))
                return 0;
            else if (obj.getString("result").equals("incorrect_login"))
                return -1;
            else if (obj.getString("result").equals("incorrect_command"))
                return -3;
            else throw new Exception("Unknown command");
        } catch (Exception e) {
            Log.e("CheckLoginDataAsyncT", "ERROR");
            e.printStackTrace();
            return -2;
        }
    }

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

    /**
     * Makes toast with code respond
     *
     * @param response - returned from doInBackground
     */
    @Override
    protected void onPostExecute(Integer response) {
        listner.sentCommandResult(response);
    }

    public interface SendCommandListener {
        public void sentCommandResult(int result);
    }
}