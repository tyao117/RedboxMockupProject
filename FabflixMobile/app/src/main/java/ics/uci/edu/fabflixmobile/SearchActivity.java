package ics.uci.edu.fabflixmobile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    Button next;
    Button prev;
    SearchViewAdapter adapter;
    List<SearchModel> modelViewList;
    List<SearchModel> fullModelList;
    private int initIndex;
    private int max;
    private int display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Movie List");

        listView = findViewById(R.id.listView);
        modelViewList = new ArrayList<SearchModel>();
        fullModelList = new ArrayList<SearchModel>();
        adapter = new SearchViewAdapter(this, modelViewList);

        initIndex = 0;
        max = 0;
        display = 10;

        prev = (Button)findViewById(R.id.prev);
        next = (Button)findViewById(R.id.next);
        setButtons(initIndex, initIndex);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Use the same network queue across out application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        MenuItem myActionMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView)myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                modelViewList.clear();

                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movie_title", query);
                String url = "http://10.0.2.2:8080/project/api/android-movielist?s=yes&movie_title=" + query;

                Log.e("wtf", query);
                final JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                String title = "";
                                try {
                                    for(int i = 0; i < response.length(); ++i) {
                                        JSONObject jsonObject = response.getJSONObject(i);
                                        String id = jsonObject.getString("movie_id");
                                        title = jsonObject.getString("movie_title");
                                        String director = jsonObject.getString("movie_director");
                                        String year = jsonObject.getString("movie_year");
                                        String genre = jsonObject.getString("movie_genre");
                                        JSONArray starsJA = jsonObject.getJSONArray("star_name_array");
                                        String stars = "";
                                        for(int j = 0; j < starsJA.length(); ++j) {
                                            if(!stars.equals("")) {
                                                stars += ", ";
                                            }
                                            stars += starsJA.getString(j);
                                        }

                                        SearchModel model = new SearchModel(id, title, director, year, genre, stars);
                                        if(i < display) {
                                            modelViewList.add(model);
                                        }
                                        fullModelList.add(model);
                                    }
                                    max = fullModelList.size();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("security.error", error.toString());

                                Toast.makeText(SearchActivity.this, "Search failed", Toast.LENGTH_LONG).show();
                            }
                        });

                queue.add(jsonRequest);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false ;
            }
        });

        return true;
    }

    public void goToPrev(View view) {
        modelViewList.clear();

        int start = initIndex-display;
        start = (start > 0) ? start : 0;
        int end = initIndex;
        end = (end < max) ? end : max;

        for(int i = start; i < display; ++i) {
            modelViewList.add(fullModelList.get(i));
        }

        setButtons(start, end);
        adapter.filter(modelViewList);
    }

    public void goToNext(View view) {
        modelViewList.clear();

        int start = initIndex;
        start = (start > 0) ? start : 0;
        int end = initIndex+display;
        end = (end < max) ? end : max;

        for(int i = start; i < end; ++i) {
            modelViewList.add(fullModelList.get(i));
        }

        setButtons(start, end);
        adapter.filter(modelViewList);
    }

    private void setButtons(int start, int end) {
        if(start == 0) {
            prev.setVisibility(View.INVISIBLE);
            prev.setClickable(false);
        } else {
            prev.setVisibility(View.VISIBLE);
            prev.setClickable(true);
        }

        if(end <= max) {
            next.setVisibility(View.INVISIBLE);
            next.setClickable(false);
        } else {
            next.setVisibility(View.VISIBLE);
            next.setClickable(true);
        }
    }
}