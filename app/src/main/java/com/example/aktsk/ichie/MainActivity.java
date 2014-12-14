package com.example.aktsk.ichie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setIcon(R.drawable.logo);
//        getSupportActionBar().setTitle("");

//        startActivity(new Intent(this, MapActivity.class));

        new Task(new Task.Callback() {
            @Override
            public void onResult(List<ImageModel> items) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                findViewById(R.id.label_progress).setVisibility(View.GONE);
                GridView listview = (GridView) findViewById(R.id.list_images);
                listview.setVisibility(View.VISIBLE);
                ArrayList<ImageModel> item = new ArrayList<ImageModel>();
                item.add(new ImageModel("フリーラン", null, -1));
                item.addAll(items);
                ImageModelAdapter adapter = new ImageModelAdapter(MainActivity.this, item);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(MainActivity.this);
            }
        }).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageModelAdapter adapter = (ImageModelAdapter) parent.getAdapter();
        ImageModel item = adapter.getItem(position);
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("model", item);
        // TODO
//        intent.putExtra("friend", new ArrayList<String>());
        startActivity(intent);
    }

    private static class Task extends AsyncTask<Void, Void, List<ImageModel>> {

        private final Callback callback;

        public Task(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<ImageModel> doInBackground(Void... params) {
            String url = "http://ec2-54-148-165-193.us-west-2.compute.amazonaws.com/cakephp/images/index";
            OkHttpClient client = new OkHttpClient();
            Request request = new Builder()
                    .get()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                JSONArray jsonArray = new JSONArray(json);
                List<ImageModel> items = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONObject object1 = object.getJSONObject("Image");
                    String path = object1.getString("path");
                    String name = object1.getString("name");
                    int good = Integer.parseInt(object1.getString("good"));
                    items.add(new ImageModel(name, path, good));
                }
                return items;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ImageModel> imageModels) {
            super.onPostExecute(imageModels);
            callback.onResult(imageModels);
        }
        public interface Callback {
            void onResult(List<ImageModel> items);
        }

        private class InternalResult {
            public ImageModel image;
        }
    }
}
