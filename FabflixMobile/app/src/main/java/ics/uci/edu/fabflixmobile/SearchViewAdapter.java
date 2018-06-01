package ics.uci.edu.fabflixmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

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
    Vector<SearchModel> vector;
    final RequestQueue queue;

    //constructor
    public SearchViewAdapter(Context context, List<SearchModel> modelList) {
        mContext = context;
        this.modelList = modelList;
        inflater = LayoutInflater.from(mContext);
        this.vector = new Vector<SearchModel>();
        this.vector.addAll(modelList);
        queue = NetworkManager.sharedManager(context).queue;
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

        //set results into view
        holder.mIDTv.setText(modelList.get(position).getId());
        holder.mTitleTv.setText(modelList.get(position).getTitle());
        holder.mDirTv.setText(modelList.get(position).getDirector());
        holder.mYearTv.setText(modelList.get(position).getYear());
        holder.mGenresTv.setText(modelList.get(position).getGenres());
        holder.mStarsTv.setText(modelList.get(position).getStars());

        //listview itemm clicks
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String id = modelList.get(position).getId();

                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("id", id);

//                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, )

//                queue.add(jsonArrayRequest);
            }
        });

        return view;
    }

    public void destroyView() {

    }
}