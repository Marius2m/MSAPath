package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

public class ParagraphContent extends PostContent{
    public String paragraph;
    @Exclude
    private static final String contentType = "paragraph";

    public ParagraphContent() {}

    public ParagraphContent(String paragraph, int id){
        this.paragraph = paragraph;
        this.id = id;
    }

    @Exclude @Override
    public String getType(){
        return contentType;
    }

    public ParagraphContent(String paragraph){
        this.paragraph = paragraph;
    }

    public String getParagraph() {
        return paragraph;
    }

    @Override
    public void setContent(String paragraph) {
        this.paragraph = paragraph;
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
