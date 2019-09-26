package com.spotify.sdk.android.authentication.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListPlaylist extends AppCompatActivity {
    private ArrayList<String> playlistNames;
    private String mAccessToken;
    private ListView listView;
    String[] listItem;
    String[] names;
    private Call mCall;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private PlaylistAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_playlists);
        getSupportActionBar().setTitle("List of playlist");
        Intent intent = getIntent();
        mAccessToken = intent.getStringExtra("mAccessToken");
        Log.d("TOKEN","THIS IS THE TOKEN: " + mAccessToken);

        listItem = getResources().getStringArray(R.array.array_technology);
        listView = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,onGetUserProfileClicked(mAccessToken));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value=adapter.getItem(position);
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public String[] onGetUserProfileClicked(String mAccessToken) {
        Log.d("TOKEN",mAccessToken);

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        names = new String[ 10];
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setResponse("Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jArray = jsonObject.getJSONArray("items");
                    String wholestring = " ";

                    for (int i=0; i < jArray.length(); i++)
                    {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            // Pulling items from the array
                            String oneObjectsItem = oneObject.getString("name");
                            names[i] = oneObjectsItem;
                            wholestring += oneObjectsItem + "n/";

                        } catch (JSONException e) {
                            // Oops
                        }
                    }

                    //setResponse(wholestring);
                } catch (JSONException e) {
                    setResponse("Failed to parse data: " + e);
                }
            }

        });
        return names;
    }
    public void SetListValues(String messageReturn){

    }
    private void setResponse(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView responseView = findViewById(R.id.response_text_view);
                responseView.setText(text);
            }
        });
    }
}
