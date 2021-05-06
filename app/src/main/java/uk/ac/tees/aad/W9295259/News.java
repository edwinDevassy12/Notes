package uk.ac.tees.aad.W9295259;

import  androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;

public class News extends AppCompatActivity {

    ArrayList<String>  data;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

         listView = findViewById(R.id.listnews);
         data = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.
                Builder().
                url("https://newsapi.org/v2/everything?q=" +
                        getIntent().getExtras().get("words").toString() +
                        "&apiKey=f8074cf0e0bb46d6b532b057fd52947a").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                final String fata = response.body().string();
                News.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonObject jsonObject = new JsonParser().parse(fata).getAsJsonObject();
                        int size = jsonObject.get("articles").getAsJsonArray().size();

                        for (int i = 0; i < size; i++) {

                                try {
                                    JsonElement loop = jsonObject.get("articles").getAsJsonArray().get(i);
                                   data.add("\n"+loop.getAsJsonObject().get("title").getAsString()
                                           +" \n \n "+loop.getAsJsonObject().get("description").getAsString());
                                } catch (Exception e) {

                                }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
