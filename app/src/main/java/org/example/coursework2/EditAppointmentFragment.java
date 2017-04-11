package org.example.coursework2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DAVID on 19/03/2016.
 */
public class EditAppointmentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_edit_details, container, false);

        Button exit = (Button) rootView.findViewById(R.id.button_to_exit_edit_menu);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.INVISIBLE);
            }
        });


        return rootView;


    }
}
