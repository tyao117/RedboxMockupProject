package ics.uci.edu.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ActionBar actionBar = getSupportActionBar();
        TextView mIdTv = findViewById(R.id.movieId);
        TextView mDirTv = findViewById(R.id.movieDirector);
        TextView mYearTv = findViewById(R.id.movieYear);
        TextView mGenresTv = findViewById(R.id.movieGenres);
        TextView mStarsTv = findViewById(R.id.movieStars);

        //get data of clicked item
        Intent intent = getIntent();
        String mActionBarTitle = intent.getStringExtra("title");
        String mId = intent.getStringExtra("id");
        String mDir = intent.getStringExtra("director");
        String mYear = intent.getStringExtra("year");
        String mGenres = intent.getStringExtra("genre");
        String mStars = intent.getStringExtra("stars");

        //set ActionBar title
        actionBar.setTitle(mActionBarTitle);

        //set text in text views
        mIdTv.setText("ID:\n"+mId+"\n");
        mDirTv.setText("Director:\n"+mDir+"\n");
        mYearTv.setText("Year:\n"+mYear+"\n");
        mGenresTv.setText("Genre(s):\n"+mGenres+"\n");
        mStarsTv.setText("Stars(s):\n"+mStars);
    }
}
