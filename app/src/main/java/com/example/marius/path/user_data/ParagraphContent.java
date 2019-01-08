package com.example.marius.path.user_data;

public class ParagraphContent extends PostContent{
    public String paragraph;

    public ParagraphContent(String paragraph){
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return paragraph;
    }
}
