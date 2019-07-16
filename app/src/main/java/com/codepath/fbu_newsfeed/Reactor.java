package com.codepath.fbu_newsfeed;

import java.lang.reflect.Type;

public class Reactor {
    private int user_ID;
    private int share_ID;
    private Type type;

    public Reactor(int user_ID, int share_ID, Type type) {
        this.user_ID = user_ID;
        this.share_ID = share_ID;
        this.type = type;
    }

    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    public int getShare_ID() {
        return share_ID;
    }

    public void setShare_ID(int share_ID) {
        this.share_ID = share_ID;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
