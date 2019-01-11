package com.example.marius.path.user_data;

import java.io.Serializable;

public class PostContent implements Serializable {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String content;

    public PostContent(){}
}
