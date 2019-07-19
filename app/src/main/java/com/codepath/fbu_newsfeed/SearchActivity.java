package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.codepath.fbu_newsfeed.Models.Friendship;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.rvResults) RecyclerView rvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fetchUsers(s);

                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);

                return true;

            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void fetchUsers(String query) {
        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereFullText("username", query);

        ParseQuery<ParseUser> fullNameQuery = ParseUser.getQuery();
        fullNameQuery.whereFullText("fullName", query);

        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<>>();
        queries.add(usernameQuery);
        queries.add(fullNameQuery);
        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);

        try {
            List<ParseUser> result = mainQuery.find();
            // TODO: add to adapter and display results
        } catch(Exception e) {
            Log.d(TAG, "Error searching users " + e.getMessage());
        }
    }
}
