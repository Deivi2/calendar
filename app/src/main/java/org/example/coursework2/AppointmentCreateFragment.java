package org.example.coursework2;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.security.auth.callback.Callback;


/**
 * Created by DAVID on 09/03/2016.
 */
public class AppointmentCreateFragment extends Fragment {

    EditText editText_for_synonym;

    SynonymsListFragment synonymsListFragment;
   SecondSynonymsListFragment secondSynonymsListFragment;

    Button invoke;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_create_appointment, container, false);

        Button exit = (Button) rootView.findViewById(R.id.button_exit_create_appointment_menu);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.INVISIBLE);
            }
        });




        return rootView;
    }

    String sString;
    int eSelection;
    int sSelection;

    @Override
    public void onStart() {
        super.onStart();
        synonymsListFragment = (SynonymsListFragment) getFragmentManager().findFragmentById(R.id.list_synonymus_fragment);
        secondSynonymsListFragment = (SecondSynonymsListFragment) getFragmentManager().findFragmentById(R.id.second_list_synonymus_fragment);
        final View synonims_list_view = getActivity().findViewById(R.id.list_synonymus_fragment);
        final View second_synonims_list_view = getActivity().findViewById(R.id.second_list_synonymus_fragment);


        editText_for_synonym = (EditText) getActivity().findViewById(R.id.editText_for_synonym);


      //  final TextView text = (TextView) getActivity().findViewById(R.id.syn_textView11);
        invoke = (Button)getActivity().findViewById(R.id.button_to_open_list_of_synonyms);
        invoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               synonymsListFragment.doSuggest(editText_for_synonym.getText().toString());
               synonims_list_view.setVisibility(View.VISIBLE);

            }
        });

        final EditText details_text_pop_up = (EditText) getActivity().findViewById(R.id.editText3);



        //THIS GETS HILIGHTED TEXS IN DETAILS AND MAKES IT TO BE CHOSEN FROM THESAURUS LIST
        details_text_pop_up.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.my_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.second_thesaurus_button:

                        sSelection = details_text_pop_up.getSelectionStart();
                        eSelection = details_text_pop_up.getSelectionEnd();
                        sString = details_text_pop_up.getText().toString().substring(sSelection, eSelection);

                        secondSynonymsListFragment.doSuggest(sString);
                       second_synonims_list_view.setVisibility(View.VISIBLE);


                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {


            }

        });


    }

    public void replace(String query){
        final EditText details_text_pop_up = (EditText) getActivity().findViewById(R.id.editText3);
        details_text_pop_up.getText().replace(sSelection, eSelection, query);

    }


}
