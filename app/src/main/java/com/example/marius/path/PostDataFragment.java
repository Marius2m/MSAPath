package com.example.marius.path;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.user_data.PostData;


public class PostDataFragment extends Fragment {
    private ScrollView scrollView;
    private TextView test;
    private RelativeLayout parentRLayout;
    private CoordinatorLayout coordinatorLayout;

    private int currentId = 0;
    private int lineIndex = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_data, container, false);

        //scrollView = (ScrollView) v.findViewById(R.id.scrollViewId);
        parentRLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutId);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayoutId);
        test = (TextView) v.findViewById(R.id.row0);
        currentId = test.getId();

        Log.d("currentId test", ""+currentId);
        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabtn);
        final FloatingActionButton picBtn = (FloatingActionButton) v.findViewById(R.id.fabPicture);
        FloatingActionButton textBtn = (FloatingActionButton) v.findViewById(R.id.fabText);

        final LinearLayout picLayout = (LinearLayout) v.findViewById(R.id.pictureLayout);
        final LinearLayout textLayout = (LinearLayout) v.findViewById(R.id.textLayout);
        picLayout.setVisibility(View.GONE);
        textLayout.setVisibility(View.GONE);

        final Animation showBtn = AnimationUtils.loadAnimation(getActivity(), R.anim.show_button);
        final Animation hideBtn = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_button);
        final Animation showLayout = AnimationUtils.loadAnimation(getActivity(), R.anim.show_layout);
        final Animation hideLayout = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_layout);


        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(picLayout.getVisibility() == View.VISIBLE && textLayout.getVisibility() == View.VISIBLE){
                    picLayout.setVisibility(View.GONE);
                    textLayout.setVisibility(View.GONE);

                    picLayout.startAnimation(hideLayout);
                    textLayout.startAnimation(hideLayout);
                    fab.startAnimation(hideBtn);
                }else{
                    picLayout.setVisibility(View.VISIBLE);
                    textLayout.setVisibility(View.VISIBLE);

                    picLayout.startAnimation(showLayout);
                    textLayout.startAnimation(showLayout);
                    fab.startAnimation(showBtn);
                }
            }
        });

        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picLayout.setVisibility(View.GONE);
                textLayout.setVisibility(View.GONE);

                picLayout.startAnimation(hideLayout);
                textLayout.startAnimation(hideLayout);
                fab.startAnimation(hideBtn);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_post_text, null);

                final EditText paragraphText = (EditText) mView.findViewById(R.id.editParagraphText);
                Button cancelBtn = (Button) mView.findViewById(R.id.cancelBtn);
                Button postBtn = (Button) mView.findViewById(R.id.postBtn);

                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                postBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!paragraphText.getText().toString().isEmpty()){
                            Log.d("Dialog: text is: ", paragraphText.getText().toString());
                           // Toast.makeText(getActivity(), paragraphText.getText().toString(), Toast.LENGTH_SHORT).show();


                            parentRLayout.addView(createNewTextView(paragraphText.getText().toString(), 0, 5, 5));
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picLayout.setVisibility(View.GONE);
                textLayout.setVisibility(View.GONE);

                picLayout.startAnimation(hideLayout);
                textLayout.startAnimation(hideLayout);
                fab.startAnimation(hideBtn);

                Log.d("InsidePicture", "PICTURE");
            }
        });

        Bundle b = getArguments();
        PostData postData = (PostData) getArguments().getSerializable("PostData");
        Log.i("getPostData",postData.toString());

        return v;
    }

    TextView createNewTextView(String text, int alignment, int margin, int padding){
        TextView newDynamicTextView = new TextView(getActivity());

        /*String idName = "row"+lineIndex;
        int lineId = Integer.parseInt(idName);
        newDynamicTextView.setId(lineId);
*/
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.BELOW, currentId);
        newDynamicTextView.setLayoutParams(layoutParams);

        newDynamicTextView.setId(TextView.generateViewId());

        currentId = newDynamicTextView.getId();
        Log.d("currentId", ""+currentId);
        newDynamicTextView.setText(text);

        Log.d("newDynamic:", newDynamicTextView.getText().toString());
        return newDynamicTextView;
    }
}
