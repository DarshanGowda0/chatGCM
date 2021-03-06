package com.dsi.darshan.samplegcm_chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivityListView extends AppCompatActivity {

    ListView listView;
    static ArrayList<MessageData> messages = new ArrayList<>();
    DbHelper dbHelper;
    MyAdapter adapter;
    String id;
    EditText et;
    BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity_list_view);
        dbHelper = new DbHelper(this);
        dbHelper.getWritableDatabase();
        et = (EditText) findViewById(R.id.msgEd);
        setupListView();
        Intent in = getIntent();
        id = in.getStringExtra(Constants.RECEIVED_REG_ID);
        setChatUserName(id);
        Log.d("DARSHAN", "" + messages.size());
        fetchMessages();
        Log.d("DARSHAN", "" + messages.size());

        setUpBroadcastReceiver();
    }

    private void setChatUserName(String id) {
        String name = dbHelper.getNameFromId(id);
        Log.d("DARSHAN",name);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
    }

    public void sendMessage(View view) {

        String msg = et.getText().toString();
        MessageData messageData = new MessageData();
        messageData.message=msg;
        messageData.me=1;
        messages.add(messageData);
        adapter.notifyDataSetChanged();
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChatActivityListView.this);
        String myRegID = mSharedPreferences.getString(Constants.REG_ID, "reg id missing");
        sendmessageTask(msg, myRegID, id);
        et.setText("");
        scrollMyListViewToBottom();
    }


    private void setUpBroadcastReceiver() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                adapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
        };


    }

    private void setupListView() {

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        messages.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.MESSAGE_ARRIVED));

    }

    private void fetchMessages() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String to = sharedPreferences.getString(Constants.REG_ID, "not available");
        ArrayList<MessageData> list = dbHelper.getAllMessages(id, to);

        for (int i=0;i<list.size();i++) {
            messages.add(list.get(i));
        }
        adapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
        Log.d("DARSHAN", "" + messages.size());

    }


    public class MyAdapter extends BaseAdapter {

        private class ViewHolder {
            TextView tv;
            RelativeLayout container;
        }


        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {

            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater)
                    getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.single_list_row,parent,false);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.chatText);
                holder.container = (RelativeLayout) convertView;
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(messages.get(position).me == 1){
                holder.container.setGravity(Gravity.END);
                holder.tv.setBackgroundResource(R.drawable.right_list_drawable);
            }else{
                holder.container.setGravity(Gravity.START);
                holder.tv.setBackgroundResource(R.drawable.left_list_drawable);
            }
            holder.tv.setText(messages.get(position).message);


            return convertView;
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
                        dbHelper.insertMessage(msg,from,to,1);
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


    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }
}
