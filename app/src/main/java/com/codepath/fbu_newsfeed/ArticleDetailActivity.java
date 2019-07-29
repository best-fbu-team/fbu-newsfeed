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
import com.codepath.fbu_newsfeed.Models.Article;
import com.parse.ParseFile;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ArticleDetailActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewArticle) View viewArticle;
    @BindView(R.id.ivArticleImageDetail) ImageView ivArticleImageDetail;
    @BindView(R.id.tvArticleTitleDetail) TextView tvArticleTitleDetail;
    @BindView(R.id.tvArticleSummaryDetail) TextView tvArticleSummaryDetail;
    @BindView(R.id.tvSourceDetail) TextView tvArticleSourceDetail;
    @BindView(R.id.tvTagDetail) TextView tvTagDetail;
    //@BindView(R.id.btnLink) Button btnLink;
    @BindView(R.id.btnShare) Button btnShare;
    @BindView(R.id.ibReportArticleDetail) ImageButton ibReportArticleDetail;

    private Article article;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        article = (Article) getIntent().getSerializableExtra("article");

        tvArticleTitleDetail.setText(article.getTitle());
        tvArticleSummaryDetail.setText(article.getSummary());
        tvArticleSourceDetail.setText(article.getSource());
        tvTagDetail.setText(article.getTag());

        url = article.getUrl();

        ParseFile imageFile = article.getImage();
        String imageUrl = article.getImageUrl();
        if (imageFile != null ) {
            Glide.with(getBaseContext()).load(imageFile.getUrl()).into(ivArticleImageDetail);
        } else if (imageUrl != null) {
            Glide.with(getBaseContext()).load(imageUrl).into(ivArticleImageDetail);
        }

        viewArticle.setOnClickListener(this);
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
            case R.id.viewArticle:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
            case R.id.btnShare:
                Intent intent = new Intent(ArticleDetailActivity.this, HomeActivity.class);
                intent.putExtra("article", (Serializable) article);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }
    }

    private void reportArticle() {
        FragmentManager fm = getSupportFragmentManager();
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fm, "fragment_report");
    }

}
