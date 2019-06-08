package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

public class ParagraphContent extends PostContent{
    public String paragraph;

    @Exclude
    private static final String contentType = "paragraph";

    public ParagraphContent() {}

    public ParagraphContent(String paragraph){
        this.paragraph = paragraph;
    }

    public ParagraphContent(String paragraph, int id){
        this.paragraph = paragraph;
    }

    @Exclude @Override
    public String getType(){
        return contentType;
    }


    @Override
    public void setContent(String paragraph) {
        this.paragraph = paragraph;
    }

    @Override @Exclude
    public int getViewType() {
        return this.VIEW_TYPE_PARAGRAPH;
    }

    @Override
    public String toString() {
        return paragraph;
    }

    @Override @Exclude
    public String getContent() {
        return paragraph;
    }
}
