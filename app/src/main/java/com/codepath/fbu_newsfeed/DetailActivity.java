package com.codepath.fbu_newsfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.codepath.fbu_newsfeed.Adapters.CommentAdapter;
import com.codepath.fbu_newsfeed.Adapters.RecommendAdapter;
import com.codepath.fbu_newsfeed.Fragments.InformationDialogFragment;
import com.codepath.fbu_newsfeed.Fragments.ReportArticleFragment;
import com.codepath.fbu_newsfeed.Helpers.BiasHelper;
import com.codepath.fbu_newsfeed.Helpers.ReactionHelper;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Fact;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Reaction;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.Source;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DetailActivity";

    @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
    @BindView(R.id.viewArticle) ConstraintLayout viewArticle;
    @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
    @BindView(R.id.tvArticleTitleCreate) TextView tvArticleTitle;
    @BindView(R.id.tvArticleSummary) TextView tvArticleSummary;
    @BindView(R.id.ibReportArticle) ImageButton ibReportArticle;
    @BindView(R.id.ibReactionLike) ImageButton ibReactionLike;
    @BindView(R.id.tvLike) TextView tvLike;
    @BindView(R.id.ibReactionDislike) ImageButton ibReactionDislike;
    @BindView(R.id.tvDislike) TextView tvDislike;
    @BindView(R.id.ibReactionHappy) ImageButton ibReactionHappy;
    @BindView(R.id.tvHappy) TextView tvHappy;
    @BindView(R.id.ibReactionSad) ImageButton ibReactionSad;
    @BindView(R.id.tvSad) TextView tvSad;
    @BindView(R.id.ibReactionAngry) ImageButton ibReactionAngry;
    @BindView(R.id.tvAngry) TextView tvAngry;
    @BindView(R.id.tvFactRating) TextView tvFactRating;
    @BindView(R.id.ivBias) ImageView ivBias;
    @BindView(R.id.ibInformation) ImageButton ibInformation;
    @BindView(R.id.tvCaption) TextView tvCaption;
    @BindView(R.id.rvComments) RecyclerView rvComments;
    @BindView(R.id.rvRecommend) RecyclerView rvRecommend;
    @BindView(R.id.etComment) EditText etComment;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.tvTag) TextView tvTag;
    @BindView(R.id.tvSource) TextView tvSource;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ibReactionExpand) ImageButton ibReactionExpand;

    private Share share;
    private Article article;
    private User user;
    private User currentUser = (User) ParseUser.getCurrentUser();

    private ArrayList<Comment> comments;
    private ArrayList<Article> articles;
    private CommentAdapter commentAdapter;
    private RecommendAdapter recommendAdapter;

    private Animation close_anim, open_anim;

    boolean isOpen = false;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        queryShare();

        close_anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.btn_close);
        open_anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.btn_open);

        comments = new ArrayList<>();
        articles = new ArrayList<>();
        commentAdapter = new CommentAdapter(getBaseContext(), comments, share);
        recommendAdapter = new RecommendAdapter(getBaseContext(), articles);

        rvComments.setAdapter(commentAdapter);
        rvRecommend.setAdapter(recommendAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        LinearLayoutManager linearLayoutManagerRecommend = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        rvComments.setLayoutManager(linearLayoutManager);
        rvRecommend.setLayoutManager(linearLayoutManagerRecommend);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        queryRecommended();

        String username = "username";
        try {
            username = user.fetchIfNeeded().getUsername();
        } catch (ParseException e){
            Log.e(TAG, "Error in retrieving username from user");
            e.printStackTrace();
        }
        tvUsername.setText(user.getUsername());

        if (user.getParseFile("profileImage") != null) {
            Glide.with(this).load(user.getParseFile("profileImage").getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("IMAGE_EXCEPTION", "Exception " + e.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Sometimes the image is not loaded and this text is not displayed");
                            return false;                        }
                    })
                    .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);
        }


        tvTimestamp.setText(share.getRelativeTime());
        ParseFile imageFile = article.getImage();
        String imageUrl = article.getImageUrl();
        if (imageFile != null ) {
            Glide.with(getBaseContext()).load(imageFile.getUrl()).into(ivArticleImage);
        } else if (imageUrl != null) {
            Glide.with(getBaseContext()).load(imageUrl).into(ivArticleImage);
        }
        tvArticleTitle.setText(article.getTitle());
        tvArticleSummary.setText(article.getSummary());
        tvTag.setText(article.getTag());

        article.getParseObject(Article.KEY_SOURCE).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Source source = (Source) object;
                tvSource.setText(source.getName());
            }
        });

        tvFactRating.setText(Fact.enumToString(article.getTruth()));

        int biasValue = article.getIntBias();
        BiasHelper.setBiasImageView(ivBias, biasValue);

        if (!share.getCaption().isEmpty()) {
            String captionUsername = "<b>@" + share.getUser().getUsername() + ": </b>";
            tvCaption.setText(Html.fromHtml(captionUsername + share.getCaption(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        } else {
            tvCaption.setVisibility(View.GONE);
        }
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser(user);
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser(user);
            }
        });

        Map<Reaction.ReactionType, Reaction> reactionMap = ReactionHelper.getReactions(share, ParseUser.getCurrentUser());

        for (int i = 0; i < Reaction.TYPES.length; i++) {
            final Reaction.ReactionType type = Reaction.TYPES[i];
            final TextView tv = getTextViewFromReactionType(type);
            final ImageButton ib = getImageButtonFromReactionType(type);

            tv.setText(String.valueOf(share.getCount(Reaction.enumToString(type))));

            ib.setSelected(false);
            if (reactionMap != null && reactionMap.get(type) != null)
                ib.setSelected(true);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateReactionText(type, share, currentUser, tv, ib);
                }
            });
        }


        tvArticleTitle.setOnClickListener(this);
        tvArticleSummary.setOnClickListener(this);
        ibReportArticle.setOnClickListener(this);
        ibInformation.setOnClickListener(this);
        tvTag.setOnClickListener(this);
        viewArticle.setOnClickListener(this);
        ivBias.setOnClickListener(this);
        tvFactRating.setOnClickListener(this);
        ibReactionExpand.setOnClickListener(this);

        setUpFriendPermissions();
        setSupportActionBar(toolbar);
        queryComments();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibReactionExpand:
                if(isOpen) {
                    setReactionVisibility(View.INVISIBLE);
                    setReactionAnim(close_anim);
                    setReactionClickable(false);

                    setClassificationVisibility(View.VISIBLE);
                    setClassificationAnim(open_anim);
                    setClassificationClickable(true);

                    isOpen = false;
                } else {
                    setReactionVisibility(View.VISIBLE);
                    setReactionAnim(open_anim);
                    setReactionClickable(true);

                    setClassificationVisibility(View.INVISIBLE);
                    setClassificationAnim(close_anim);
                    setClassificationClickable(false);

                    isOpen = true;
                }
                break;
            case R.id.ibReportArticle:
                reportArticle();
                break;
            case R.id.ibInformation:
                Log.d(TAG, "Clicked information");
                //showInformationDialog();
                Intent tempIntent = new Intent(DetailActivity.this, QuizActivity.class);
                startActivity(tempIntent);
                break;
            case R.id.ivBias:
                showInformationDialog();
                break;
            case R.id.tvFactRating:
                showInformationDialog();
                break;
            case R.id.tvTag:
                Intent intent = new Intent(DetailActivity.this, TagActivity.class);
                intent.putExtra("tag", article.getTag());
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
            case R.id.tvArticleSummary:
            case R.id.tvArticleTitleCreate:
            case R.id.viewArticle:
                Intent i = new Intent(this, BrowserActivity.class);
                i.putExtra("article", (Serializable) article);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void setUpFriendPermissions() {
        if (isFriends() || user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            etComment.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.VISIBLE);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    String message = etComment.getText().toString();
                    if (message == null || message.equals("")) {
                        Toast.makeText(getBaseContext(), "Please enter a comment", Toast.LENGTH_LONG).show();
                    } else {
                        Comment addedComment = new Comment(message, (User) ParseUser.getCurrentUser(), share);
                        addedComment.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("DetailActivity", "Success in saving comment");
                                    fetchTimelineAsync();
                                    etComment.setText("");
                                    etComment.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    return;
                                } else {
                                    Log.e("DetailActivity", "Error in creating comment");
                                    e.printStackTrace();
                                    Toast.makeText(DetailActivity.this, "Error in submitting comment", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        createNotification("COMMENT", share, message);
                    }
                }
            });
        } else {
            etComment.setVisibility(View.INVISIBLE);
            btnSubmit.setVisibility(View.INVISIBLE);
            rvComments.setVisibility(View.GONE);
            btnSubmit.setOnClickListener(null);

        }
    }

    private void showInformationDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fragmentTransaction, "fragment_information");
    }

    private void goToUser(ParseUser user) {
        Intent intent = new Intent(DetailActivity.this, HomeActivity.class);
        intent.putExtra("user_id", user.getObjectId());
        startActivity(intent);
    }

    private boolean isFriends() {
        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("user2", user);
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("user1", user);
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            return result.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }

    }

    private void queryShare() {
        share = (Share) getIntent().getSerializableExtra("share");
        user = (User) share.getUser();
        article = share.getArticle();
    }

    private void queryComments() {
        ParseQuery<Comment> commentQuery = ParseQuery.getQuery(Comment.class);
        commentQuery.include(Comment.KEY_SHARE);
        commentQuery.include(Comment.KEY_TEXT);
        commentQuery.include(Comment.KEY_USER);
        commentQuery.whereEqualTo(Comment.KEY_SHARE, share);

        commentQuery.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> newComments, ParseException e) {
                if (e != null) {
                    Log.e("DetailActivity", "Error in comment query");
                    e.printStackTrace();
                    return;
                }
                comments.addAll(newComments);
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateReactionText(Reaction.ReactionType type, Share share, User currentUser, TextView textView, ImageButton imageButton) {
        Reaction reaction = ReactionHelper.getReaction(type, share, currentUser);
        int count;
        if (reaction != null) {
            count = ReactionHelper.destroyReaction(reaction, type, share);
            imageButton.setSelected(false);
            deleteNotification("REACTION", share, Reaction.enumToString(type));
        } else {
            count = ReactionHelper.createReaction(type, share);
            imageButton.setSelected(true);
            createNotification("REACTION", share, Reaction.enumToString(type));
        }
        textView.setText(Integer.toString(count));
    }

    private void createNotification(String type, Share share, String typeText) {
        Log.d(TAG, "Creating notification of type: " + type);
        User shareUser = (User) share.getUser();
        if (ParseUser.getCurrentUser().getObjectId().equals(shareUser.getObjectId())) { return; }
        Notification notification = new Notification(type, (User) ParseUser.getCurrentUser(), shareUser, share, typeText);
        notification.saveInBackground();
    }

    private void deleteNotification(String type, Share share, String typeText) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);


        query.whereEqualTo(Notification.KEY_TYPE, type);
        query.whereEqualTo(Notification.KEY_TYPE_TEXT, typeText);
        query.whereEqualTo(Notification.KEY_SEND_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Notification.KEY_RECEIVE_USER, share.getUser());

        Notification notification;

        try {
            List<Notification> result = query.find();
            if (result.size() > 0 ) {
                notification = result.get(0);
            } else {
                return;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
            return;
        }

        notification.deleteInBackground();
    }


    private void queryRecommended() {
        final ParseQuery<Article> recommendQuery = ParseQuery.getQuery(Article.class);
        recommendQuery.include(Article.KEY_TITLE);
        recommendQuery.include(Article.KEY_IMAGE);

        recommendQuery.whereContains(Article.KEY_TAG, article.getTag());
        recommendQuery.whereNotEqualTo(Article.KEY_TITLE, article.getTitle());
        int biasValue = article.getIntBias();
        switch (biasValue) {
            case 1:
            case 2:
                recommendQuery.whereGreaterThanOrEqualTo(Article.KEY_BIAS, 2);
                recommendQuery.whereLessThanOrEqualTo(Article.KEY_BIAS, 4);
                recommendQuery.orderByDescending(Article.KEY_BIAS);
                break;
            case 3:  recommendQuery.addDescendingOrder(Article.KEY_CREATED_AT);
                break;
            case 4:
            case 5:
                recommendQuery.whereGreaterThanOrEqualTo(Article.KEY_BIAS, 2);
                recommendQuery.whereLessThanOrEqualTo(Article.KEY_BIAS, 4);
                recommendQuery.orderByAscending(Article.KEY_BIAS);
                break;
        }
        recommendQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> newArticles, ParseException e) {
                if (e != null) {
                    Log.e("TrendsQuery", "Error with query");
                    e.printStackTrace();
                    return;
                }

                for (int i = 0; i < newArticles.size(); i++) {
                    Article article = newArticles.get(i);
                    Log.d("TrendsQuery", "Article: " + article.getTitle());
                }
                recommendAdapter.addAll(newArticles);
            }
        });
    }

    private void fetchTimelineAsync() {
        commentAdapter.clear();
        queryComments();
        swipeContainer.setRefreshing(false);
    }

    private TextView getTextViewFromReactionType(Reaction.ReactionType type) {
        switch (type) {
            case LIKE:
                return tvLike;
            case DISLIKE:
                return tvDislike;
            case HAPPY:
                return tvHappy;
            case SAD:
                return tvSad;
            case ANGRY:
                return tvAngry;
            default:
                return null;
        }
    }

    private ImageButton getImageButtonFromReactionType(Reaction.ReactionType type) {
        switch (type) {
            case LIKE:
                return ibReactionLike;
            case DISLIKE:
                return ibReactionDislike;
            case HAPPY:
                return ibReactionHappy;
            case SAD:
                return ibReactionSad;
            case ANGRY:
                return ibReactionAngry;
            default:
                return null;
        }
    }

    private void setReactionVisibility(int visibility) {
        ibReactionLike.setVisibility(visibility);
        ibReactionDislike.setVisibility(visibility);
        ibReactionHappy.setVisibility(visibility);
        ibReactionSad.setVisibility(visibility);
        ibReactionAngry.setVisibility(visibility);

        tvLike.setVisibility(visibility);
        tvDislike.setVisibility(visibility);
        tvHappy.setVisibility(visibility);
        tvSad.setVisibility(visibility);
        tvAngry.setVisibility(visibility);
    }

    private void setReactionAnim(Animation anim) {
        ibReactionLike.startAnimation(anim);
        ibReactionDislike.startAnimation(anim);
        ibReactionHappy.startAnimation(anim);
        ibReactionSad.startAnimation(anim);
        ibReactionAngry.startAnimation(anim);

        tvLike.startAnimation(anim);
        tvDislike.startAnimation(anim);
        tvHappy.startAnimation(anim);
        tvSad.startAnimation(anim);
        tvAngry.startAnimation(anim);
    }

    private void setReactionClickable(boolean clickable) {
        ibReactionLike.setClickable(clickable);
        ibReactionDislike.setClickable(clickable);
        ibReactionHappy.setClickable(clickable);
        ibReactionSad.setClickable(clickable);
        ibReactionAngry.setClickable(clickable);
    }

    private void setClassificationVisibility(int visibility) {
        tvFactRating.setVisibility(visibility);
        ivBias.setVisibility(visibility);
        ibInformation.setVisibility(visibility);
    }

    private void setClassificationAnim(Animation anim) {
        tvFactRating.startAnimation(anim);
        ivBias.startAnimation(anim);
        ibInformation.startAnimation(anim);
    }

    private void setClassificationClickable(boolean clickable) {
        tvFactRating.setClickable(clickable);
        ivBias.setClickable(clickable);
        ibInformation.setClickable(clickable);
    }

    private void reportArticle() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fragmentTransaction, "fragment_report");
    }

}
