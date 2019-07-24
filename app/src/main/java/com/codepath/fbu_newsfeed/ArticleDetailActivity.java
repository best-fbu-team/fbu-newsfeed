package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Fragments.ReportArticleFragment;
import com.codepath.fbu_newsfeed.Fragments.ReportUserFragment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.parse.ParseFile;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "ArticleDetailActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ibReportArticleDetail)
    ImageButton ibReportArticleDetail;
    ImageView ivArticleImageDetail;
    TextView tvArticleTitleDetail;
    TextView tvArticleSummaryDetail;
    TextView tvArticleSourceDetail;
    TextView tvTagDetail;
    Button btnLink;
    Button btnShare;

    Article article;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        ivArticleImageDetail = findViewById(R.id.ivArtcleImageDetail);
        tvArticleTitleDetail = findViewById(R.id.tvArticleTitleDetail);
        tvArticleSummaryDetail = findViewById(R.id.tvArticleSummaryDetail);
        tvArticleSourceDetail = findViewById(R.id.tvSourceDetail);
        tvTagDetail = findViewById(R.id.tvTagDetail);
        btnLink = findViewById(R.id.btnLink);
        btnShare = findViewById(R.id.btnShare);

        article = (Article) getIntent().getSerializableExtra("article");

        tvArticleTitleDetail.setText(article.getTitle());
        tvArticleSummaryDetail.setText(article.getSummary());
        tvArticleSourceDetail.setText(article.getSource());
        tvTagDetail.setText(article.getTag());

        url = article.getUrl();

        ParseFile image = article.getImage();
        if (image != null ) {
            Glide.with(getBaseContext()).load(image.getUrl()).into(ivArticleImageDetail);
        }

        btnLink.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        ibReportArticleDetail.setOnClickListener(this);

        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibReportArticleDetail:
                Log.d(TAG, "trying to report article");
                reportArticle();
                break;
            case R.id.btnLink:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.btnShare:
                Intent intent = new Intent(ArticleDetailActivity.this, HomeActivity.class);
                intent.putExtra("article", (Serializable) article);
                startActivity(intent);

                break;
        }
    }

    private void reportArticle() {
        FragmentManager fm = getSupportFragmentManager();
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fm, "fragment_report");
    }

}
