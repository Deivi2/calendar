package org.example.coursework2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DAVID on 20/03/2016.
 */
public class MoveAppointmentFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_move_appointment, container, false);

        Button exit = (Button) rootView.findViewById(R.id.button_to_exit_move_appointment);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.INVISIBLE);
            }
        });

        return rootView;


    }
}
