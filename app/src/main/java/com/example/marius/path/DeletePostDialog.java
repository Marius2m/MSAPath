package com.example.marius.path;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeletePostDialog extends DialogFragment
{
    public static final String ARG_TITLE = "Path Deletion";
    public static final String ARG_MESSAGE = "Are you sure you want to delete this path? Action cannot be undone.";

    public static DeletePostDialog newInstance() {
        DeletePostDialog frag = new DeletePostDialog();

        return frag;
    }

    public DeletePostDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
//        Bundle args = getArguments();
//        String title = args.getString(ARG_TITLE);
//        String message = args.getString(ARG_MESSAGE);

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        builder.setView(inflater.inflate(R.layout.fragment_myDialog, null))


        return new AlertDialog.Builder(getActivity())
                .setTitle(ARG_TITLE)
                .setMessage(ARG_MESSAGE)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                    }
                })
                .create();
    }
}