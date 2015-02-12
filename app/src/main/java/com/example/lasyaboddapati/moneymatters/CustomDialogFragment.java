package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by lasyaboddapati on 1/30/15.
 */
public class CustomDialogFragment extends DialogFragment {

    CustomDialogListener mListener;
    //AlertDialog.Builder builder;
    AlertDialog dialog;
    private View dialogLayoutView;

    public interface CustomDialogListener {
        public void onDialogPositiveClick(CustomDialogFragment dialog);
        public void onDialogNegativeClick(CustomDialogFragment dialog);
    }

    public static CustomDialogFragment newInstance(View view) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.dialogLayoutView = view;
        return dialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (CustomDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogLayoutView)
               .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogPositiveClick(CustomDialogFragment.this);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogNegativeClick(CustomDialogFragment.this);
                   }
               });
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart(){
        super.onStart();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public View getDialogView() {
        return dialogLayoutView;
    }
}