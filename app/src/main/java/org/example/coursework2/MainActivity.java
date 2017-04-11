package org.example.coursework2;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import static android.provider.BaseColumns._ID;
import static org.example.coursework2.Constants.TABLE_NAME;
import static org.example.coursework2.Constants.TIME;
import static org.example.coursework2.Constants.TITLE;
import static org.example.coursework2.Constants.DETAILS;
import static org.example.coursework2.Constants.DATE;

public class MainActivity extends AppCompatActivity  {


    CalendarView calendar;

    private static String[] FROM = { _ID , TITLE, TIME, DETAILS, DATE };
    private static String ORDER_BY = _ID + " ASC";
    private AppointmentData appointments;

    ArrayList array = new ArrayList();

    private String data;
    private String togetid;
    private String get_date;


   private  EditText new_title;
   private  EditText new_time;
   private  EditText new_details;
   private  ListView applicList;


    private View move_appointment_view;
    private View edit_appiontment_view;

    private  Boolean flag_set_if_move_appointments_pressed =false;
 //   private android.view.ActionMode.Callback actionModeCallback;
  //  private CompoundButton.OnCheckedChangeListener checkedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appointments = new AppointmentData(this);
        initializeCalendar();



        // SET SECOND SYNONYMS FROMGMENT INVISABLE ON APPLICATION STARTS
        final View second_synonims_list_view = findViewById(R.id.second_list_synonymus_fragment);
        second_synonims_list_view.setVisibility(View.INVISIBLE);



        //TO SET CREATE APPOINTMENT FRAGMENT VISABLE AFTER BUTTON CLICKED
        final View app_frag = findViewById(R.id.appointment_create_fragment);
        app_frag.setVisibility(View.INVISIBLE);
        Button btnLoad = (Button) findViewById(R.id.create_appoinment_button);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_frag.setVisibility(View.VISIBLE);

            }
        });

        //DETAILS ENTERED INSIDE CREATE APPOINTMENT
        final EditText title_text = (EditText) findViewById(R.id.editText);
        final EditText time_text = (EditText) findViewById(R.id.editText2);
        final EditText details_text = (EditText) findViewById(R.id.editText3);



        //TO SAVE APPOINTMENT BUTTON THAT IS INSIDE CREATE APPOINTMENT FRAGMENT
        Button save_appointmenr_button = (Button) findViewById(R.id.save_button);
        save_appointmenr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addEvent(title_text.getText().toString(), time_text.getText().toString(), details_text.getText().toString());
                } catch (Exception e){
                    Toast.makeText(MainActivity.this, "SELECT A DATE BEFORE SAVE",
                            Toast.LENGTH_LONG).show();
                }
                finally {
                    appointments.close();
                }
                app_frag.setVisibility(View.INVISIBLE);
            }
        });


        //DELETE APPOINTMENT BUTTON HERE
        final View delete_app_fragment = findViewById(R.id.delete_appiontment_fragment);
        delete_app_fragment.setVisibility(View.INVISIBLE);
        final Button delete_appointment = (Button) findViewById(R.id.delete_appointment_button);
        delete_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_app_fragment.setVisibility(View.VISIBLE);
            }
        });




        //VISABILITY FOR CHOSE TO DELETE APPOINTMENT VIEW
        final View fragment_show_appointment = findViewById(R.id.fragment_delete_chose_appointmenrt);
        fragment_show_appointment.setVisibility(View.INVISIBLE);
        final Button show_appointment = (Button) findViewById(R.id.button_delete_selected);

        show_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_app_fragment.setVisibility(View.INVISIBLE);
                fragment_show_appointment.setVisibility(View.VISIBLE);
                try {
                    Cursor cursor = getEmployeeName(get_date);
                    showEvents(cursor);

                } finally {
                    appointments.close();
                }
            }
        });


        //DELTE CHOSEN APPOINTMENT
        final TextView id_to_delete = (TextView) findViewById(R.id.text_of_appointment_id_to_delete);
        Button delete_id = (Button) findViewById(R.id.button_to_delete_id);
        delete_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  appointment_value;

                String appointment_id;

                try {
                    appointment_value = id_to_delete.getText().toString();
                    appointment_id = array.get(Integer.parseInt(appointment_value)-1).toString().substring(0,3).replace(" ", "");
                    deleteAppointment(appointment_id);
                    fragment_show_appointment.setVisibility(View.INVISIBLE);
                } catch (Exception e){
                    Toast.makeText(MainActivity.this, "APPOINTMENT NOT FOUND FOR THAT DAY, SELECT A DAY OR OTHE APPOINTMENT ID",
                            Toast.LENGTH_LONG).show();
                }
            }
        });



        //TO DELETE ALL ON PRESS DELETE ALL
        Button delete_all = (Button) findViewById(R.id.button_delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll(get_date);
            }
        });



        //ON PRESS EDIT/VIEWM OPEN IT FRAGMENT
        edit_appiontment_view = findViewById(R.id.edit_appiontment_fragment);
        edit_appiontment_view.setVisibility(View.INVISIBLE);
        Button open_edit_menu = (Button) findViewById(R.id.edit_appointment_button);
        open_edit_menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                edit_appiontment_view.setVisibility(View.VISIBLE);
                try {
                    Cursor cursor = getEmployeeName(get_date);
                    showEvents(cursor);
                } finally {
                    appointments.close();
                }


            }
        });



        //ON PRESS MOVE APPOITMENT
        move_appointment_view = findViewById(R.id.move_appiontment_fragment);
        move_appointment_view.setVisibility(View.INVISIBLE);

        Button open_move_menu = (Button) findViewById(R.id.move_appintment_button);
        open_move_menu.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                move_appointment_view.setVisibility(View.VISIBLE);
                try {
                    Cursor cursor = getEmployeeName(get_date);
                    showEvents(cursor);

                } finally {
                    appointments.close();
                }
            }
        });



        //TO EDIT APOINTMENT BUTTON
        final TextView id_to_edit = (TextView) findViewById(R.id.editText_in_edit_appointment_to_enter_keey);
        Button edit_appointment_value = (Button) findViewById(R.id.button_to_edit);
        edit_appointment_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  appointment_value;
                String appointment_id;

                try {
                    appointment_value = id_to_edit.getText().toString();
                    appointment_id = array.get(Integer.parseInt(appointment_value)-1).toString().substring(0,3).replace(" ", "");
                    Log.d("MainActivity" , "£Appoitnment id from edit" + appointment_id);
                    edit_appointment(appointment_id);
                }
                catch(Exception e) {
                    Toast.makeText(MainActivity.this, "NO FIND THIS APPOINTMENT ID OR DAY IS NOT CHOSEN",
                            Toast.LENGTH_LONG).show();
                }

            }
        });


        // TEXT EDITORS TO EDIT APPOINTMENT
         new_title = (EditText) findViewById(R.id.editText_new_title);
         new_time = (EditText) findViewById(R.id.editText_new_time);
         new_details = (EditText) findViewById(R.id.editText_new_details);



        //DECLERE LIST AND SET ONCLICK LISTENER
        applicList = (ListView) findViewById(R.id.appointments_list);
        applicList.setOnItemClickListener(clickListener);



        //DECLARE LIST FOR SYNONYMS INVISABLE FIRST
        final View synonims_list_view = findViewById(R.id.list_synonymus_fragment);
        synonims_list_view.setVisibility(View.INVISIBLE);


        //BUTTON TO OPEN SEARCH ACTIVITY
        Button open_search = (Button) findViewById(R.id.search_button);
        open_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent novice_game = new Intent(MainActivity.this, SearchActivity.class);
                MainActivity.this.startActivity(novice_game);



            }
        });




    }



    private void addEvent(String string,String time, String Details) {
        // Insert a new record into the Events data source.
        // You would do something similar for delete and update.
        final EditText title_text = (EditText) findViewById(R.id.editText);
        SQLiteDatabase db =  appointments.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(string != title_text.getText().toString()) {
            values.put(TITLE, string);
        }else        Log.d("MainActivity", "ALLERT!!!!!!!!!!!!!");
        values.put(TIME,time);
        values.put(DETAILS,Details);
        values.put(DATE, get_date);
        db.insertOrThrow(TABLE_NAME, null, values);
        db.close();
    }


    public void edit_appointment(String id){

        SQLiteDatabase db =  appointments.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title",new_title.getText().toString());
        cv.put("time",new_time.getText().toString());
        cv.put("details", new_details.getText().toString());
        db.update(TABLE_NAME, cv, "_id=" + id, null);

    }


    public void change_date_for_appointment(String id, String date){

        SQLiteDatabase db =  appointments.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date",date);
        db.update(TABLE_NAME, cv, "_id=" + id, null);
        flag_set_if_move_appointments_pressed = false;

    }


    private Cursor getEvents() {
        // Perform a managed query. The Activity will handle closing
        // and re-querying the cursor when needed.
        SQLiteDatabase db = appointments.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
                null, ORDER_BY);
        startManagingCursor(cursor);
        return cursor;
    }


    private void showEvents(Cursor cursor) {
        // Stuff them all into a big string
       StringBuilder builder = new StringBuilder();
        array.clear();

         while (cursor.moveToNext()) {
            // Could use getColumnIndexOrThrow() to get indexes
          //  cursor.getColumnIndexOrThrow(String.valueOf(builder));

            int id = (int) cursor.getLong(0);
            String title = cursor.getString(1);
            long time = cursor.getLong(2);
            String details = cursor.getString(3);
            String date = cursor.getString(4);

            //    builder.append(id).append(":  ");
    //        builder.append(time).append(": ");
    //        builder.append(details).append(":");
    //        builder.append(date).append("\n ");

            data = id + "   Titile: " + title + " Time: " + time + "\n";

            array.add(data);

             setAdapter();

            int i;
            for( i=0; i<array.size(); i++) {
            }
             builder.append(i + " " + data.substring(3));
        }

        // Display on the screen
        TextView text = (TextView) findViewById(R.id.view_for_appointments);
        for(int i=0; i<array.size(); i++) {
            Log.d("MainActivity", "inside arrqay " + i + " " + array.get(i));
        }
        text.setText(builder);

        TextView text_to_appointment = (TextView) findViewById(R.id.view_for_appointments_in_edit);

     //   text_to_appointment.setMovementMethod(new ScrollingMovementMethod());

        text_to_appointment.setText(builder);
    }


    //ADAPTER SETS ITEMS TO VIEWLIST
    public void setAdapter(){
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        applicList.setAdapter(adapter);
    }



      // CLICK LISTENER FOR MOVE APPOINTMENT
    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            String query = (String) parent.getItemAtPosition(position);
          //  doSearch(query);
            flag_set_if_move_appointments_pressed = true;
            togetid = query.substring(0,3).replace(" ", "");
      //     Log.d("MainActivity", "ITEMS FROM ONLICK LISTENER DEBUGING " + query.substring(0,3).replace(" ", "") + " " + flag_set_if_move_appointments_pressed);
            move_appointment_view.setVisibility(View.INVISIBLE);
        }
    };




    public Cursor getEmployeeName(String empNo) {
        String empName = "";
        SQLiteDatabase db = appointments.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM appointments WHERE date=?", new String[] {empNo + ""});
        startManagingCursor(cursor);
        return cursor;
    }



    public void deleteAppointment(String id)
    {
        SQLiteDatabase db = appointments.getWritableDatabase();
        db.delete("appointments", "_id=?", new String[]{id + ""});
    }
    public void deleteAll(String id)
    {
        SQLiteDatabase db = appointments.getWritableDatabase();
        db.delete("appointments", "date=?", new String[]{id + ""});
    }



    public void initializeCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendar);
        //  calendar.setMaxDate(daysInMonth);
        // sets whether to show the week number.
        calendar.setShowWeekNumber(false);
        // calendar.setMaxDate(daysInMonth);
        // sets the first day of week according to Calendar.
        // here we set Monday as the first day of the Calendar
        calendar.setFirstDayOfWeek(2);

        //sets the listener to be notified upon selected date change.
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();

                get_date = day + "/" + month + "/" + year;

                if (flag_set_if_move_appointments_pressed == true) {

                    change_date_for_appointment(togetid, get_date);
                }

                Log.d("MainActivity", "datais : " + day + month + year);
            }
        });
    }









}