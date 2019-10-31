package com.hilaylotan.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlertDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Context context = getActivity();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        builder.setTitle(R.string.error_title)
                .setMessage(R.string.error_messege)
                .setPositiveButton(R.string.error_button_text,null);

        return builder.create();
    }
}
