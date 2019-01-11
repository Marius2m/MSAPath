package com.example.marius.path.user_data;

public class ParagraphContent extends PostContent{
    public String paragraph;

    public ParagraphContent(String paragraph){
        this.paragraph = paragraph;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return paragraph;
    }
}
