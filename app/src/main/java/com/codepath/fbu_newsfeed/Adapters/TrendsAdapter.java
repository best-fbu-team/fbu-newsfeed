package com.codepath.fbu_newsfeed.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.ArticleDetailActivity;
import com.codepath.fbu_newsfeed.Fragments.ReportArticleFragment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseFile;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrendsAdapter extends RecyclerView.Adapter<TrendsAdapter.ViewHolder> {

    Context context;
    List<Article> articles;

    public TrendsAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public TrendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trend_article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendsAdapter.ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ibInformation) ImageButton ibInformationTrends;
        @BindView(R.id.ivBiasTrends) ImageView ivBiasTrends;
        @BindView(R.id.ibReportArticle) ImageButton ibReportArticle;
        @BindView(R.id.ivArticleImageTrends) ImageView ivArticleImage;
        @BindView(R.id.tvArticleTitleTrends) TextView tvTitle;
        @BindView(R.id.tvArticleSummaryTrends) TextView tvSummary;
        @BindView(R.id.tvSourceTrends) TextView tvSource;
        @BindView(R.id.tvFactRatingTrends) TextView tvFactRatingTrends;
        @BindView(R.id.tvTagTrends) TextView tvTagTrends;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            ibReportArticle.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Article article = articles.get(position);

                switch(view.getId()) {
                    case R.id.ibReportArticle:
                        reportArticle(article);
                        break;
                    default:
                        Intent intent = new Intent(context, ArticleDetailActivity.class);
                        intent.putExtra("article", (Serializable) article);
                        context.startActivity(intent);
                        break;
                }

            }
        }

        public void bind(Article article) {
            tvTitle.setText(article.getTitle());
            tvSummary.setText(article.getSummary());
            tvSource.setText(article.getSource());
            tvFactRatingTrends.setText(article.getTruth());
            tvTagTrends.setText(article.getTag());

            int biasValue = article.getIntBias();
            switch (biasValue) {
                case 1:  ivBiasTrends.setColorFilter(Article.liberalColor);
                    break;
                case 2:  ivBiasTrends.setColorFilter(Article.slightlyLiberalColor);
                    break;
                case 3:  ivBiasTrends.setColorFilter(Article.moderateColor);
                    break;
                case 4:  ivBiasTrends.setColorFilter(Article.slightlyConservativeColor);
                    break;
                case 5:  ivBiasTrends.setColorFilter(Article.conservativeColor);
                    break;
            }

            ParseFile image = article.getImage();
            if (image != null && !(((Activity) context).isFinishing())) {
                Glide.with(context.getApplicationContext()).load(image.getUrl()).into(ivArticleImage);
            }
        }
    }

    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Article> newArticles) {
        articles.addAll(newArticles);
        notifyDataSetChanged();
    }

    private void reportArticle(Article article) {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fm, "fragment_report");
    }


}
