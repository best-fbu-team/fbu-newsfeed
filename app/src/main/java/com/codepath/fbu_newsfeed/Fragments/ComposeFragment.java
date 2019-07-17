package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;
import com.codepath.fbu_newsfeed.fragments.FeedFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ComposeFragment extends Fragment {

    Article article;

    EditText etUrl;
    ImageView ivArticlePreview;
    TextView tvArticleTitle;
    TextView tvFactCheck;
    ImageView  ivBias;
    ImageButton ibReport;
    EditText etCaption;
    Button btnShare;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable("article");
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUrl = view.findViewById(R.id.etURL);
        ivArticlePreview  = view.findViewById(R.id.ivArticlePreview);
        tvArticleTitle = view.findViewById(R.id.tvArticleTitle);
        tvFactCheck = view.findViewById(R.id.tvFactCheck);
        ivBias = view.findViewById(R.id.ivBias);
        ibReport = view.findViewById(R.id.ibReport);
        etCaption = view.findViewById(R.id.etCaption);
        btnShare = view.findViewById(R.id.btShareArticle);

        //Article article = (Article) getActivity().getIntent().getSerializableExtra("article");
        //article = (Article) getArguments().getSerializable("article");

        final String factCheck = article.getTruth();
        final Article.Bias bias = article.getBias();
        final ParseFile imageFile = article.getImage();
        final String title = article.getTitle();
        final String summary = article.getSummary();
        final String source = article.getSource();

        tvFactCheck.setText(factCheck);
        tvArticleTitle.setText(title);
        if(imageFile != null) {
            Glide.with(getContext()).load(imageFile.getUrl()).into(ivArticlePreview);
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = etUrl.getText().toString();
                final String caption = etCaption.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                shareArticle(user, caption, url, imageFile, title, summary, factCheck, bias, source);
            }
        });

    }

    private void shareArticle(ParseUser user, String caption, String url, ParseFile imageFile, String title, String summary, String factCheck, Article.Bias bias, String source) {
        final Article newArticle = new Article(url, title, imageFile, summary, bias, factCheck, source);
        newArticle.saveInBackground();

        final Share share = new Share(user, newArticle, caption);
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "Share article success");
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new FeedFragment()).commit();
                } else {
                    Log.e("ComposeFragment", "Error in sharing article");
                    e.printStackTrace();
                }
            }
        });
    }

}
