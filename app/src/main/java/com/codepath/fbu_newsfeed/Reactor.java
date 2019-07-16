package com.codepath.fbu_newsfeed;

import java.lang.reflect.Type;

public class Reactor {
    private User user_id;
    private Shares share_id;
    private Type type;

    public Reactor(User user_id, Shares share_id, Type type) {
        this.user_id = user_id;
        this.share_id = share_id;
        this.type = type;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public Shares getShare_id() {
        return share_id;
    }

    public void setShare_id(Shares share_id) {
        this.share_id = share_id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
