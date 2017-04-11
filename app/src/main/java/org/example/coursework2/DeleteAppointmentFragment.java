package org.example.coursework2;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DAVID on 12/03/2016.
 */
public class DeleteAppointmentFragment extends Fragment {

    MainActivity mainActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_delete_appointment, container, false);

        Button exit = (Button) rootView.findViewById(R.id.exit_delete_button);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.INVISIBLE);
            }
        });
        return rootView;
    }
}
