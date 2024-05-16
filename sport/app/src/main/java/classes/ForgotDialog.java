package classes;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

import com.example.sportactivity.R;

import helpers.FirebaseHelper;

public class ForgotDialog extends AppCompatDialogFragment {
    EditText username;
    Button sign_in;
    FirebaseHelper firebaseHelper = new FirebaseHelper();

    /**
     * Create the dialog and set its properties.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state
     * @return the created dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_forgotdialog,null);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS},1);

        username = view.findViewById(R.id.userName);
        sign_in = view.findViewById(R.id.button);
        sign_in.setOnClickListener(this::onClick);
        builder.setView(view).setTitle("forget password").setNegativeButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    /**
     * Called when the fragment is associated with its activity.
     *
     * @param context the context of the activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Handles the button click event.
     *
     * @param view the view that was clicked
     */
    public void onClick(View view){
        if (view == sign_in) {
            if (!username.getText().toString().equals("")) {
                firebaseHelper.userExists(username.getText().toString(), new FirebaseHelper.Callback() {
                    @Override
                    public void onSuccess(boolean exists) {
                        if (exists) {
                            firebaseHelper.getUser(username.getText().toString(), new FirebaseHelper.ReturnCall<Data>() {
                                @Override
                                public void onSuccess(Data result) {
                                    if (result instanceof User) {

                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(((User) result).getPhone(),null,"your password is: " + ((User) result).getPassword(),null,null);
                                        Toast.makeText(getContext(), "password in sms", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else Toast.makeText(getContext(), "user does not exists", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else Toast.makeText(getContext(), "must enter all fields", Toast.LENGTH_LONG).show();

        }
    }
}

