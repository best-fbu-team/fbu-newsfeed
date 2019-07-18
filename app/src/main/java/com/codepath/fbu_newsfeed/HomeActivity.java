package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.fbu_newsfeed.Fragments.ComposeFragment;
import com.codepath.fbu_newsfeed.fragments.TrendsFragment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.fragments.FeedFragment;
import com.codepath.fbu_newsfeed.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private final String TAG ="HomeActivity";

    public BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new FeedFragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        break;
                    case R.id.action_trending:
                        fragment = new TrendsFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    // TODO: notifications fragment
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);

        Intent intent = getIntent();
        if (intent != null) {
            Article article = (Article) intent.getSerializableExtra("article");

            if (article != null) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                ComposeFragment composeFragment = new ComposeFragment();
                composeFragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.flContainer, composeFragment).commit();
            }
        }


    }
}
