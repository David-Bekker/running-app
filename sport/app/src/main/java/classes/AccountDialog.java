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

public class AccountDialog extends AppCompatDialogFragment {
    Button btn1;
    Button btn2;
    String flag ="";
    FragmentActivity activity;

    /**
     * Called to create the dialog that will be displayed.
     * Sets up the layout and button listeners for the dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_account_dialog,null);

        activity = getActivity();

        btn1 = view.findViewById(R.id.button2);
        btn2 = view.findViewById(R.id.button3);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            flag = bundle.getString("mode");
        }

        if (flag.equals("1")) {
            btn1.setVisibility(View.VISIBLE);
        }

        btn1.setOnClickListener(this::onClick);
        btn2.setOnClickListener(this::onClick);

        builder.setView(view).setTitle("account settings").setNegativeButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    /**
     * Called when the fragment is attached to an activity.
     * Currently not doing anything.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Called when a button in the dialog is clicked.
     * Opens a forgot password dialog if the "forgot password" button is clicked,
     * or finishes the current activity and starts the MainActivity if the "exit" button is clicked.
     */
    public void onClick(View view) {
        if (view == btn1) {
            openForgotDialog();
        } else if (view == btn2) {
            if (activity != null) {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.finish();
                activity.startActivity(intent);
                getDialog().dismiss();
            }

        }
    }

    /**
     * Opens a forgot password dialog.
     */
    public void openForgotDialog(){
        ForgotDialog dialog = new ForgotDialog();
        dialog.show(getParentFragmentManager(),"forgot dialog");
    }
}
