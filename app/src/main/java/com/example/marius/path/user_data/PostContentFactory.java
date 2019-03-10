package com.example.marius.path.user_data;
import com.example.marius.path.user_data.ImageContent;
import com.example.marius.path.user_data.ParagraphContent;
import com.example.marius.path.user_data.PostContent;
import com.example.marius.path.user_data.PostContents;
import com.example.marius.path.user_data.PostData;


public class PostContentFactory {
    public PostContent getPostContentObject(String contentType){

//        if(contentType.equals("ImageContent")){
//            return new ImageContent();
//        }else if(contentType.equals("MapContent")){
//            return new MapContent();
//        }else if(contentType.equals("ParagraphContent")){
//            return new ParagraphContent();
//        }
//        return null;

        Class postContent = null;
        try {
            postContent = Class.forName("com.example.marius.path.user_data." + contentType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            return (PostContent) postContent.newInstance();
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
