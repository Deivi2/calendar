package org.example.coursework2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static org.example.coursework2.Constants.DATE;
import static org.example.coursework2.Constants.DETAILS;
import static org.example.coursework2.Constants.TABLE_NAME;
import static org.example.coursework2.Constants.TIME;
import static org.example.coursework2.Constants.TITLE;

/**
 * Created by DAVID on 26/03/2016.
 */
public class SuggestTask implements Runnable {




    AppointmentData appointments;

    private final SearchActivity suggest;
    private final String original;

    SuggestTask(SearchActivity context, String original){
        this.suggest = context;
        this.original = original;
    }

    @Override
    public void run() {
        List<String> suggestions = getArrayOfRessults(original);
        suggest.setSuggestions(suggestions);
    }



    //EXTRACT DATABASE TO JSON
    private JSONArray getResults()
    {
        appointments = new AppointmentData(suggest);

        String myPath = appointments.databasePath;// Set path to your database
        String myTable = TABLE_NAME;//Set name of your table
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();

        }
        cursor.close();
        Log.d("TAG_NAME JSON FILE", resultSet + "JSON OBJECT");
        return resultSet;
    }


    //EXTRAST JSON TO LIST
    public List<String> getArrayOfRessults(String original){

        StringBuffer buffer = new StringBuffer();
        ArrayList list_of_results = new ArrayList();
        ArrayList list_of_original = new ArrayList();
        JSONObject jsonObject= null;

        String value;

        try {
            JSONArray jsonArray = new JSONArray(getResults().toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                buffer.delete(0, buffer.length());

                jsonObject = jsonArray.getJSONObject(i);

                list_of_results.add(jsonObject.getString("details"));
           //     list_of_results.add(jsonObject.getString("title"));

                buffer.append(list_of_results.get(i));

        //        Log.d("MainActivity", "LIST OF JSON FROM ARRAY STRING INSIDE BUFFER INDEX " + buffer.indexOf(original));

                if(buffer.indexOf(original) > -1){
                    value = String.valueOf(buffer);
            //        Log.d("MainActivity", "LIST OF JSON FROM ARRAY STRING INSIDE IF  " + value + " " + i);
                list_of_original.add(value);
                }
          //      Log.d("MainActivity", "LIST OF JSON FROM ARRAY LIST OF ORIGINAL  " + list_of_original.get(i));
            }//end for

        } catch (JSONException e) {
            //  Log.e("onPostExecute > Try > JSONException => " , e);
            e.printStackTrace();
        }
        return list_of_original;

    }
}
