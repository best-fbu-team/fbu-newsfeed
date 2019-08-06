package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.fbu_newsfeed.Models.Annotation;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BrowserActivity";

    public @BindView(R.id.webview) WebView webView;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnShare) Button btnShare;
    @BindView(R.id.ibBack) ImageButton ibBack;
    @BindView(R.id.ibForward) ImageButton ibForward;
    @BindView(R.id.ibRefresh) ImageButton ibRefresh;
    @BindView(R.id.tvPrompt) TextView tvPrompt;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public @BindView(R.id.etAnnotation) EditText etAnnotation;
    public @BindView(R.id.btnSubmit) Button btnSubmit;
    public @BindView(R.id.annotationConstraintLayout)
    ConstraintLayout annotationConstraintLayout;
    @BindView(R.id.shareConstraintLayout) ConstraintLayout shareConstraintLayout;

    private String url;
    private Article article;

    private ArrayList<Annotation> annoList;

    private boolean urlChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        annoList = new ArrayList<>();
        urlChanged = false;

        setSupportActionBar(toolbar);

        article = (Article) getIntent().getSerializableExtra("article");
        url = article.getUrl();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AnnotationInterface(this), "Android");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                if (view.canGoBack()) {
                    ibBack.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorBlack));
                } else {
                    ibBack.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorModerate));
                }

                if (view.canGoForward()) {
                    ibForward.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorBlack));
                } else {
                    ibForward.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorModerate));
                }
                if (annoList.isEmpty())
                    getAnnotations();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJS(view, R.raw.annotate);
                renderAnnotations();
                progressBar.setVisibility(View.GONE);
                tvPrompt.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                urlChanged = true;

                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webView.loadUrl(url);

        btnShare.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        ibForward.setOnClickListener(this);
        ibRefresh.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnShare:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("url", webView.getUrl());
                startActivity(intent);
                break;
            case R.id.ibBack:
                if (webView.canGoBack())
                    webView.goBack();
                break;
            case R.id.ibForward:
                if (webView.canGoForward())
                    webView.goForward();
                break;
            case R.id.ibRefresh:
                webView.reload();
                break;
        }
    }

//   TODO: For when I decide to work on swipe up and see comments

//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//
//        int action = MotionEventCompat.getActionMasked(event);
//
//        switch(action) {
//            case (MotionEvent.ACTION_UP) :
//                Log.d(TAG,"Action was UP");
//                return true;
//            default :
//                return super.onTouchEvent(event);
//        }
//    }

    private void injectJS(WebView view, int scriptFile) {
        InputStream input;
        try {
            input = new BufferedInputStream(getResources().openRawResource(scriptFile));
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpAnnotation(int tempX, int tempY, int screenWidth) {
        if (urlChanged) {
            Toast.makeText(this, "You may only annotate on the originally shared article", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tempX > (screenWidth - 120)) { // annotations don't go beyond viewport
            tempX = screenWidth - 120;
        }

        final int positionX = tempX;
        final int positionY = tempY;

        annotationConstraintLayout.setVisibility(View.VISIBLE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etAnnotation.getText().toString();
                final Annotation annotation = new Annotation(ParseUser.getCurrentUser(), article, positionX, positionY, text);
                annoList.add(annotation);
                displayAnnotation(annotation);
                annotation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(BrowserActivity.this, "Added annotation!", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "Error saving annotation", e);
                        }
                        etAnnotation.setText("");
                        etAnnotation.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        annotationConstraintLayout.setVisibility(View.GONE);

                    }
                });
            }
        });

    }

    private void getAnnotations() {
        List<ParseUser> friends = getFriends();

        friends.add(ParseUser.getCurrentUser());

        ParseQuery<Annotation> annoQuery = new ParseQuery<>(Annotation.class);
        annoQuery.whereEqualTo(Annotation.KEY_ARTICLE, article);
        annoQuery.whereContainedIn("user", friends);
        annoQuery.findInBackground(new FindCallback<Annotation>() {
            @Override
            public void done(List<Annotation> objects, ParseException e) {
                Log.d(TAG, "Got " + objects.size() + " annotations");
                annoList.addAll(objects);
            }
        });

    }

    private List<ParseUser> getFriends() {

        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            Log.d(TAG, "Found " + result.size() + " friendships");
            List<ParseUser> friends = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                Friendship friendship = result.get(i);
                if (friendship.isUser1(ParseUser.getCurrentUser())) {
                    friends.add(friendship.getUser2());
                } else {
                    friends.add(friendship.getUser1());
                }
            }
            return friends;
        } catch(Exception e) {
            Log.d(TAG, "Error retrieving friends: " + e.getMessage());

        }
        return new ArrayList<>();
    }

    private void displayAnnotation(Annotation anno) {
            String positionX = String.valueOf(anno.getPositionX()) + "px";
            String positionY = String.valueOf(anno.getPositionY()) + "px";
            String text = anno.getText();
            String id = anno.getObjectId();

            try {
                String username = anno.getUser().fetchIfNeeded().getUsername();
                String jsScript = "    var body = document.querySelector('body');\n" +
                        "    console.log(\"Trying to annotate: ID " + id + " " + positionX + " " + positionY + " " + text + " " + username + "\");\n" +
                        "    var outerdiv = document.createElement('div');\n" +
                        "    var icon = document.createElement('span');\n" +
                        "    icon.id = \"annotation-icon-" + id + "\";\n" +
                        "    icon.style.cssText = 'position: absolute; height: 24px; width: 24px; display: block; z-index: 2';\n" +
                        "    icon.innerHTML = '<svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\"> <path fill=\"#70E7CA\" d=\"M9,22A1,1 0 0,1 8,21V18H4A2,2 0 0,1 2,16V4C2,2.89 2.9,2 4,2H20A2,2 0 0,1 22,4V16A2,2 0 0,1 20,18H13.9L10.2,21.71C10,21.9 9.75,22 9.5,22V22H9M10,16V19.08L13.08,16H20V4H4V16H10Z\" /></svg>';\n" +
                        "    outerdiv.appendChild(icon);" +
                        "    outerdiv.style.cssText = 'position: absolute; width: 120px; z-index: 1';\n" +
                        "    outerdiv.style.top = '" + positionY + "';\n" +
                        "    outerdiv.style.left = '" + positionX + "';\n" +
                        "    var div = document.createElement('div');\n" +
                        "    div.innerHTML = \"<b>@" + username + ":</b> " + text + "\";\n" +
                        "    div.id = 'annotation-body-" + id + "';\n" +
                        "    div.style.cssText = 'position: absolute; background-color: #F7F7F7; padding: 8px; border-radius: 8px; overflow: auto; width: 120px; z-index: 1; display: none';\n" +
                        "    outerdiv.appendChild(div);\n" +
                        "    body.appendChild(outerdiv);\n" +
                        "icon.addEventListener('click', function(e) {\n" +
                        "    console.log(\"CLICKED AT: X=\" + e.pageX + \" Y=\" + e.pageY);\n" +
                        "    var div = document.getElementById('annotation-body-" + id + "');\n" +
                        "    div.style.display = 'block';\n" +
                        "    this.style.display = 'none';\n" +
                        "})\n" +
                        "div.addEventListener('click', function(e) {\n" +
                        "    console.log(\"CLICKED AT: X=\" + e.pageX + \" Y=\" + e.pageY);\n" +
                        "    this.style.display = 'none';\n" +
                        "    var icon = document.getElementById('annotation-icon-" + id + "');\n" +
                        "    icon.style.display = 'block';\n" +
                        "})\n";
                Log.d(TAG, jsScript);
                webView.evaluateJavascript(jsScript, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d(TAG, "YAY! " + s);
                                }
                });
            } catch (Exception e) {
                Log.d(TAG, "Error getting user: ", e);
            }

    }

    public void renderAnnotations() {
        for (Annotation anno : annoList) {
            displayAnnotation(anno);
        }
    }



    public class AnnotationInterface {
        Context mContext;
        int positionX;
        int positionY;
        int screenWidth;

        AnnotationInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void sendCoords(String X, String Y, String width) {
            this.positionX = Integer.valueOf(X);
            this.positionY = Integer.valueOf(Y);
            this.screenWidth = Integer.valueOf(width);

            Log.d(TAG, "SENT THESE COORDS: " + X + ", " + Y);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((BrowserActivity) mContext).setUpAnnotation(positionX, positionY, screenWidth);
                }
            });

        }

    }

}
