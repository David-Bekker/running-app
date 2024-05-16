package classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.sportactivity.R;
import com.example.sportactivity.map_activity;

import java.util.Arrays;
import java.util.List;

public class StartSessionDialog extends AppCompatDialogFragment {
    Spinner spinner;
    FragmentActivity activity;

    // Create the dialog and set up its layout and behavior
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_start_session,null);

        activity = getActivity();

        spinner = view.findViewById(R.id.spinner);
        List<String> list = Arrays.asList("running","riding");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_list, list);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);


        builder.setView(view).setTitle("session settings").setNegativeButton("start", new DialogInterface.OnClickListener() {
            // Handle the start button click event
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (activity != null) {
                    Intent intent = new Intent(activity, map_activity.class);
                    if (spinner.getSelectedItemPosition() == 0) {
                        intent.putExtra("mode", "running");
                    }
                    else if (spinner.getSelectedItemPosition() == 1) {
                        intent.putExtra("mode", "riding");
                    }
                    String user_name = getArguments().getString("userName");
                    intent.putExtra("userName", user_name); // Pass the user_name to map_activity
                    activity.finish();
                    activity.startActivity(intent);
                    activity.startService(new Intent(activity,CounterService.class));
                    getDialog().dismiss();
                }
                else Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            // Handle the cancel button click event
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    // Called when the fragment is attached to its parent activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
