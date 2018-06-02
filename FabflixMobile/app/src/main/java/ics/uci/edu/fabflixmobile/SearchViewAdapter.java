package ics.uci.edu.fabflixmobile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static ics.uci.edu.fabflixmobile.R.id.movieId;

public class SearchViewAdapter extends BaseAdapter {

    //variables
    Context mContext;
    LayoutInflater inflater;
    List<SearchModel> modelList;
    final RequestQueue queue;

    //constructor
    public SearchViewAdapter(Context context, List<SearchModel> modelList) {
        mContext = context;
        this.modelList = modelList;
        inflater = LayoutInflater.from(mContext);
        queue = NetworkManager.sharedManager(context).queue;
        Log.d("modelList size", Integer.toString(modelList.size()));
    }

    public class ViewHolder {
        TextView mIDTv, mTitleTv, mDirTv, mYearTv, mGenresTv, mStarsTv;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;

        Log.d("modelList size", Integer.toString(modelList.size()));

        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.search_row, null);

            //locate the views
            holder.mIDTv = view.findViewById(movieId);
            holder.mTitleTv = view.findViewById(R.id.movieTitle);
            holder.mDirTv = view.findViewById(R.id.movieDirector);
            holder.mYearTv = view.findViewById(R.id.movieYear);
            holder.mGenresTv = view.findViewById(R.id.movieGenres);
            holder.mStarsTv = view.findViewById(R.id.movieStars);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        Log.d("modelList size", Integer.toString(modelList.size()));

            //set results into view
            holder.mIDTv.setText(modelList.get(position).getId());
            holder.mTitleTv.setText(modelList.get(position).getTitle());
            holder.mDirTv.setText(modelList.get(position).getDirector());
            holder.mYearTv.setText(modelList.get(position).getYear());
            holder.mGenresTv.setText(modelList.get(position).getGenres());
            holder.mStarsTv.setText(modelList.get(position).getStars());

            //listview item clicks
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String id = modelList.get(position).getId();

                    Log.e("click", id);

                    // Post request form data
                    final Map<String, String> params = new HashMap<>();
                    params.put("id", id);

                    String url = "https://18.216.228.119:8443/project/api/single-movie?id=" + id;

                    final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        String id = "", title = "", director = "", year = "", genre = "";
                                        String stars = "";
                                        for (int i = 0; i < response.length(); ++i) {
                                            JSONObject jsonObject = response.getJSONObject(i);
                                            id = jsonObject.getString("movie_id");
                                            title = jsonObject.getString("movie_title");
                                            director = jsonObject.getString("movie_director");
                                            year = jsonObject.getString("movie_year");
                                            genre = jsonObject.getString("movie_genre");
                                            if (!stars.equalsIgnoreCase("")) {
                                                stars += ", ";
                                            }
                                            stars += jsonObject.getString("star_name");

                                            //SearchModel model = new SearchModel(id, title, director, year, genre, stars);
                                        }

                                        Intent goToIntent = new Intent(mContext, MovieActivity.class);
                                        goToIntent.putExtra("id", id);
                                        goToIntent.putExtra("title", title);
                                        goToIntent.putExtra("director", director);
                                        goToIntent.putExtra("year", year);
                                        goToIntent.putExtra("genre", genre);
                                        goToIntent.putExtra("stars", stars);

                                        mContext.startActivity(goToIntent);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                    queue.add(jsonArrayRequest);
                }
            });

        return view;

    }

    //filter
    public void filter() {

        notifyDataSetChanged();
    }

}