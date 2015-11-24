package com.dsi.darshan.samplegcm_chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static ArrayList<String> messages = new ArrayList<>();
    EditText et;
    MyAdapter adapter;
    String id;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activty);
        dbHelper = new DbHelper(this);
        dbHelper.getWritableDatabase();
        et = (EditText) findViewById(R.id.msgEd);
        setUpRecView();
        Intent in = getIntent();
        id = in.getStringExtra(Constants.RECEIVED_REG_ID);
//        fetchMessages();

        setUpBroadcastReceiver();
    }

//    private void fetchMessages() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String to = sharedPreferences.getString(Constants.REG_ID,"not available");
////        ArrayList<String> msg = dbHelper.getAllMessages(id, to);
//        for(String message: msg){
//            messages.add(message);
//        }
//        adapter.notifyDataSetChanged();
//    }

    public void sendMessage(View view) {

        String msg = et.getText().toString();
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
        String myRegID = mSharedPreferences.getString(Constants.REG_ID, "reg id missing");
        sendmessageTask(msg,myRegID,id);
        et.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.MESSAGE_ARRIVED));

    }

    private void setUpBroadcastReceiver() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                adapter.notifyItemInserted(messages.size() - 1);
            }
        };


    }

    private void setUpRecView() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recViewChat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);


    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.single_user_card, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            try {
                holder.tv.setText(messages.get(position));
            } catch (Exception e) {
                System.out.print("" + e);
            }

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            TextView tv;

            public Holder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv);
            }
        }
    }

    void sendmessageTask(final String msg, final String from,final String to) {

        new AsyncTask<Void, Void, Void>() {
            BufferedReader mBufferedInputStream;
            String Response = "";

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    URL url = new URL("http://204.152.203.111/gcm_server_php/send_message_to.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setConnectTimeout(15000);
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);



                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("message", msg)
                            .appendQueryParameter("regId", to)
                            .appendQueryParameter("senderId", from);
//            Log.d("pageno", "" + page_no);

                    String query = builder.build().getEncodedQuery();

                    OutputStream os = httpURLConnection.getOutputStream();

                    BufferedWriter mBufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    mBufferedWriter.write(query);
                    mBufferedWriter.flush();
                    mBufferedWriter.close();
                    os.close();

                    httpURLConnection.connect();

                    Log.d("DARSHAN", "response code " + httpURLConnection.getResponseCode());

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        mBufferedInputStream = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String inline;
                        while ((inline = mBufferedInputStream.readLine()) != null) {
                            Response += inline;
                        }
                        mBufferedInputStream.close();
                        Log.d("DARSHAN", "sent the msg successfully");

//                parseJson(Response);
                        Log.d("DARSHAN", Response);
//                        dbHelper.insertMessage(msg,from,to);
//                parseJSON(Response);

                    } else {
                        Log.d("darshan", "something wrong");

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return null;
            }
        }.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        messages.clear();

    }
}
