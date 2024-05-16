package classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.sportactivity.MainActivity;
import com.example.sportactivity.R;
import com.example.sportactivity.login;

import classes.ForgotDialog;

public class Credits2Dialog extends AppCompatDialogFragment {

    // Create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.credis2_layout,null);

        // Set the view and title for the dialog, and add a "close" button
        builder.setView(view).setTitle("credits").setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    // Attach the dialog to the context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

}
