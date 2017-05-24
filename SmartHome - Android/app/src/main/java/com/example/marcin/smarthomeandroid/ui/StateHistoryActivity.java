package com.example.marcin.smarthomeandroid.ui;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcin.smarthomeandroid.data.Names;
import com.example.marcin.smarthomeandroid.R;
import com.github.pavlospt.roundedletterview.RoundedLetterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class StateHistoryActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.hisory_toolbar);
        myToolbar.setTitle(R.string.history_activity_name);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);*/

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new HistoryAsyncTask().execute();
    }

    void prepareList(ArrayList<StateHistoryRecord> records) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        HistoryRecordAdapter adapter = new HistoryRecordAdapter(records);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
    }

    private class HistoryRecordAdapter extends RecyclerView.Adapter<ViewHolder> {
        ArrayList<StateHistoryRecord> mRecords;

        public HistoryRecordAdapter(ArrayList<StateHistoryRecord> records) {
            mRecords = records;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_record_view, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            StateHistoryRecord currentRecord = mRecords.get(position);
            holder.roundedLetterView.setTitleText(currentRecord.letter);
            holder.textViewTitle.setText(currentRecord.state);
            holder.textViewSubTitle.setText(currentRecord.start + " -- " + currentRecord.stop);
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewSubTitle;
        RoundedLetterView roundedLetterView;

        ViewHolder(View v) {
            super(v);
            roundedLetterView = (RoundedLetterView) v.findViewById(R.id.roundedLetterView);
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            textViewSubTitle = (TextView) v.findViewById(R.id.textViewSubTitle);
        }
    }

    private class StateHistoryRecord implements Comparable<StateHistoryRecord> {

        String start;
        String stop;
        String state;
        String letter;
        int id;

        StateHistoryRecord(int id, String start, String stop, String state) {
            this.id = id;
            this.start = start;
            this.stop = stop;
            this.state = "111";
            switch (state) {
                case Names.GATE_OPEN_STATE: {
                    this.state = "Brama otawrta";
                    letter = "B";
                    break;
                }
                case Names.GATE_CLOSE_STATE: {
                    this.state = "Brama zamknięta";
                    letter = "B";
                    break;
                }
                case Names.WICKET_CLOSE_STATE: {
                    this.state = "Furtka zamknięta";
                    letter = "F";
                    break;
                }
                case Names.WICKET_OPEN_STATE: {
                    this.state = "Furtka otawrta";
                    letter = "F";
                    break;
                }
                case Names.INTERCOM_RING_STATE: {
                    this.state = "Domofon dzwoni";
                    letter = "D";
                    break;
                }
            }
        }

        @Override
        public int compareTo(@NonNull StateHistoryRecord historyRecord) {
            return historyRecord.id - id;
        }

        @Override
        public String toString() {
            return "[" + id + ", " + state + ", " + start + ", " + stop + "]";
        }
    }

    private class HistoryAsyncTask extends AsyncTask<Void, Void, ArrayList<StateHistoryRecord>> {

        JSONArray getJSONArray(InputStream is) throws IOException, JSONException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String responseString;
            StringBuilder sb = new StringBuilder();
            while ((responseString = reader.readLine()) != null) {
                sb = sb.append(responseString);
            }
            String photoData = sb.toString();
            return new JSONArray(photoData);
        }

        ArrayList<StateHistoryRecord> getArrayList(JSONArray array) throws JSONException {
            ArrayList<StateHistoryRecord> arrayList = new ArrayList<>();
            for (int index = 0; index < array.length(); index++) {
                JSONObject object = (JSONObject) array.get(index);
                arrayList.add(new StateHistoryRecord(object.optInt("Id"), object.optString("Start"), object.optString("End"),
                        object.optString("Module")));
            }
            Collections.sort(arrayList);
            return arrayList;
        }

        @Override
        protected ArrayList<StateHistoryRecord> doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + Names.SERVER_NAME + "/stateHistoryForPhone");
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setConnectTimeout(2000);
                return getArrayList(getJSONArray(client.getInputStream()));
            } catch (Exception e) {
                Log.e("HistoryAsyncTask", "ERROR");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<StateHistoryRecord> list) {
            if (list == null) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Nie udało sie nawiązac połączenia", Toast.LENGTH_SHORT).show();
            } else
                prepareList(list);
        }
    }
}