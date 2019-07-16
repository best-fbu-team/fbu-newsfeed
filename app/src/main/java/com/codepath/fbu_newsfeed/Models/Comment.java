package com.codepath.fbu_newsfeed.Models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";
    public static final String KEY_SHARE = "share";

    private String text;
    private User user;
    private Share share;

    public Comment() {
        super();
    }

    public Comment(String text, User user, Share share) {
        super();
        this.text = text;
        put(KEY_TEXT, text);
        this.user = user;
        this.share = share;
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        this.text = text;
        put(KEY_TEXT, text);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseObject getShare() {
        return getParseObject(KEY_SHARE);
    }

    public void setShare(Share share) {
        put(KEY_SHARE, share);
    }
}
