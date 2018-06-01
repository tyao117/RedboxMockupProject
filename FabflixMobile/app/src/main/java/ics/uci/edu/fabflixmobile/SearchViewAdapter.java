package ics.uci.edu.fabflixmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

public class SearchViewAdapter extends BaseAdapter {

    //variables
    Context mContext;
    LayoutInflater inflater;
    List<SearchModel> modelList;
    Vector<SearchModel> vector;

    //constructor
    public SearchViewAdapter(Context context, List<SearchModel> modelList) {
        mContext = context;
        this.modelList = modelList;
        inflater = LayoutInflater.from(mContext);
        this.vector = new Vector<SearchModel>();
        this.vector.addAll(modelList);
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.search_row, null);

            //locate the views
            holder.mIDTv = view.findViewById(R.id.movieId);
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
                //TODO
            }
        });

        return view;
    }


    //filter
    public void filter(String charText) {
        modelList.clear();
        if(charText.length() == 0) {
            //do nothing
        } else {
            for(SearchModel model : vector) {
                if(model.getTitle().equals(charText)) {
                    modelList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }
}
