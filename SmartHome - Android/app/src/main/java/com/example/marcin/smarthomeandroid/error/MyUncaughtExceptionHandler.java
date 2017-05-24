package com.example.marcin.smarthomeandroid.error;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.marcin.smarthomeandroid.data.DataFileSaver;
import com.example.marcin.smarthomeandroid.data.Names;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Marcin on 25.03.2017.
 * <p>
 * Class to handle errors
 * It save stackTrace to server - it is big simplification to find error
 * additionally send log to server - developer can find bug remotely
 */

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Context mContext;

    public MyUncaughtExceptionHandler(Context context) {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String simpleError = prepareStringError(throwable);
        String htmlError = prepareHtmlError(throwable);

        DataFileSaver.saveError(mContext, simpleError);
        new SendErrorToServerAsyncTask(simpleError, htmlError).execute();

        System.out.print(simpleError);
        defaultUEH.uncaughtException(thread, throwable);
    }

    private String prepareStringError(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(throwable.toString());
        stringBuilder.append(" at ");
        stringBuilder.append(Calendar.getInstance().getTime().toString());
        stringBuilder.append("\n");

        for (StackTraceElement s : throwable.getStackTrace()) {
            stringBuilder.append("    at ");
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        stringBuilder.append("--------------------------------------\n");
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    private String prepareHtmlError(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(throwable.toString());
        stringBuilder.append(" at ");
        stringBuilder.append(Calendar.getInstance().getTime().toString());
        stringBuilder.append("\n<br>");

        for (StackTraceElement s : throwable.getStackTrace()) {
            stringBuilder.append("|||    at ");
            stringBuilder.append(s);
            stringBuilder.append("\n<br>");
        }
        stringBuilder.append("--------------------------------------\n");
        stringBuilder.append("\n<br>");

        return stringBuilder.toString();
    }

    private class SendErrorToServerAsyncTask extends AsyncTask<Void, Void, String> {
        private String mErrorSimpleLog, mErrorHtmlLog;

        SendErrorToServerAsyncTask(String errorSimpleLog, String errorHtmlLog) {
            mErrorSimpleLog = errorSimpleLog;
            mErrorHtmlLog = errorHtmlLog;
        }

        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://" + Names.SERVER_NAME + "/sendErrorLog");
                HttpsURLConnection client = (HttpsURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoOutput(true);

                DataOutputStream ds = new DataOutputStream(client.getOutputStream());
                ds.writeBytes("logSimple=" + mErrorSimpleLog);
                ds.writeBytes("&logHtml=" + mErrorHtmlLog);
                ds.flush();
                ds.close();

                client.connect();
                return client.getResponseMessage();
            } catch (Exception e) {
                Log.e("SendErrorToServerAsyncT", "ERROR");
                e.printStackTrace();
                return "Error during sending data to server";
            }
        }
    }
}