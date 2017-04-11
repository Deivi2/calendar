package org.example.coursework2;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import static android.provider.BaseColumns._ID;
import static org.example.coursework2.Constants.DATE;
import static org.example.coursework2.Constants.DETAILS;
import static org.example.coursework2.Constants.TABLE_NAME;
import static org.example.coursework2.Constants.TIME;
import static org.example.coursework2.Constants.TITLE;

/**
 * Created by DAVID on 26/03/2016.
 */
public class SearchActivity extends FragmentActivity {


    private static String[] FROM = { _ID , TITLE, TIME, DETAILS, DATE };
    private static String ORDER_BY = _ID + " ASC";


    AppointmentData appointments;



    private EditText origText;
        private ListView suggList;
        private TextView ebandText;
        private Handler guiThread;
        private ExecutorService suggThread;
        private Runnable updateTask;
        private Future<?> suggPending;
        private List<String> items;
        private ArrayAdapter<String> adapter;

    View show_details ;



    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search_activity);

            appointments = new AppointmentData(this);


            initThreading();
            findViews();
            setListeners();
            setAdapters();


        //SET FRAGMENT OF LIST OF SERSHED APPOINTMENTS
   //   show_details =  findViewById(R.id.list_of_searshed_appointments);
    //    show_details.setVisibility(View.INVISIBLE);



    }

        @Override
        protected void onDestroy() {
            // Terminate extra threads here
            suggThread.shutdownNow();
            super.onDestroy();
        }



    private void showEvents(Cursor cursor) {
        // Stuff them all into a big string
        StringBuilder builder = new StringBuilder();


        while (cursor.moveToNext()) {
            // Could use getColumnIndexOrThrow() to get indexes
            //  cursor.getColumnIndexOrThrow(String.valueOf(builder));

        //    int id = (int) cursor.getLong(0);
            String title = cursor.getString(1);
            long time = cursor.getLong(2);
            String details = cursor.getString(3);
            String date = cursor.getString(4);

         //       builder.append(id).append(":  ");
                builder.append("Title:  ").append(title).append("\n");
                builder.append("Time: ").append(time).append("\n");
                builder.append("Date: ").append(date).append("\n");
                builder.append("Details: ").append(details).append("\n");
        }

        // Display on the screen
   //     TextView text = (TextView) findViewById(R.id.textView_for_searched_appointments);
     //   text.setText(builder);

        Toast.makeText(SearchActivity.this, builder,
                Toast.LENGTH_LONG).show();

    //    TextView text_to_appointment = (TextView) findViewById(R.id.view_for_appointments_in_edit);
    //    text_to_appointment.setText(builder);

    }



    public Cursor getEmployeeName(String empNo) {
        SQLiteDatabase db = appointments.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM appointments WHERE details=?", new String[] {empNo + ""});
        startManagingCursor(cursor);
        return cursor;
    }




    /** Get a handle to all user interface elements */
    private void findViews() {
        origText = (EditText) findViewById(R.id.original_text);
        suggList = (ListView) findViewById(R.id.result_list);
     //   ebandText = (TextView) findViewById(R.id.eband_text);
    }

    /** Setup user interface event handlers */
    private void setListeners() {
        // Define listener for text change
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            /* Do nothing */
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                queueUpdate(1000 /* milliseconds */);
            }
            public void afterTextChanged(Editable s) {
            /* Do nothing */
            }
        };

        // Set listener on the original text field
        origText.addTextChangedListener(textWatcher);



        // Define listener for clicking on an item
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
         //       doSearch(query);

                Cursor cursor = getEmployeeName(query);
                showEvents(cursor);


//               show_details.setVisibility(View.VISIBLE);

            }
        };

        // Set listener on the suggestion list
        suggList.setOnItemClickListener(clickListener);

        // Make website link clickable
     //   ebandText.setMovementMethod(LinkMovementMethod.getInstance());
    }


  //  private void doSearch(String query) {
  //      Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
  //      intent.putExtra(SearchManager.QUERY, query);
   //     startActivity(intent);
  //  }

    /** Set up adapter for list view. */
    private void setAdapters() {
        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        suggList.setAdapter(adapter);
    }

    /**
     * Initialize multi-threading. There are two threads: 1) The main
     * graphical user interface thread already started by Android,
     * and 2) The suggest thread, which we start using an executor.
     */
    private void initThreading() {
        guiThread = new Handler();
        suggThread = Executors.newSingleThreadExecutor();

        // This task gets suggestions and updates the screen
        updateTask = new Runnable() {
            public void run() {
                // Get text to suggest
                String original = origText.getText().toString().trim();

                // Cancel previous suggestion if there was one
                if (suggPending != null)
                    suggPending.cancel(true);

                // Check to make sure there is text to work on
                if (original.length() != 0) {
                    // Let user know we're doing something
                    setText(R.string.working);

                    // Begin suggestion now but don't wait for it
                    try {
                        SuggestTask suggestTask = new SuggestTask(
                               SearchActivity.this, // reference to activity
                                original // original text
                        );
                        suggPending = suggThread.submit(suggestTask);
                    } catch (RejectedExecutionException e) {
                        // Unable to start new task
                        setText(R.string.error);
                    }
                }
            }
        };
    }

    /** Request an update to start after a short delay */
    private void queueUpdate(long delayMillis) {
        // Cancel previous update if it hasn't started yet
        guiThread.removeCallbacks(updateTask);
        // Start an update if nothing happens after a few milliseconds
        guiThread.postDelayed(updateTask, delayMillis);
    }

    /** Modify list on the screen (called from another thread) */
    public void setSuggestions(List<String> suggestions) {
        guiSetList(suggList, suggestions);
    }

    /** All changes to the GUI must be done in the GUI thread */
    private void guiSetList(final ListView view,
                            final List<String> list) {
        guiThread.post(new Runnable() {
            public void run() {
                setList(list);

            }
        });
    }

    /** Display a message */
    private void setText(int id) {
        adapter.clear();
        adapter.add(getResources().getString(id));
    }

    /** Display a list */
    private void setList(List<String> list) {
        adapter.clear();
        adapter.addAll(list);
    }


}

