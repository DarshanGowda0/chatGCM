package com.dsi.darshan.samplegcm_chat;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by darshan on 13/11/15.
 */
public class FetchUsers extends AsyncTask<Void, Void, Void> {

    String TAG = "FetchUsers";

    public static ArrayList<DataClass> mList = new ArrayList<>();

    @Override
    protected Void doInBackground(Void... params) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL("http://204.152.203.111/gcm_server_php/fetch_users.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 200) {
                String responseString = readStream(urlConnection.getInputStream());
                Log.v(TAG, responseString);
                parseJson(responseString);
            } else {
                Log.v(TAG, "Response code:" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return null;
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    void parseJson(String data) {


        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                DataClass mDataClass = new DataClass();
                mDataClass.name = jsonObject.getString("name");
                mDataClass.number = jsonObject.getString("number");
                mDataClass.id = jsonObject.getString("regId");

                mList.add(mDataClass);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        try {
            MainActivity.mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
