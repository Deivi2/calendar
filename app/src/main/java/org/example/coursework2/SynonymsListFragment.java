package org.example.coursework2;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DAVID on 20/03/2016.
 */
public class SynonymsListFragment extends Fragment  {


    ArrayList list = new ArrayList();
    private static final String TAG = "SuggestTask";
    ArrayList list_to_seperated_elements = new ArrayList();
    String getletters;
    Handler handler = new Handler(Looper.getMainLooper());


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list_of_synonyms, container, false);

        Button exit = (Button) rootView.findViewById(R.id.button_exit_list_of_synonyms);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.INVISIBLE);
            }
        });
        return rootView;
    }

    ListView listViewSyn;

    @Override
    public void onStart() {
        super.onStart();
        listViewSyn = (ListView) getActivity().findViewById(R.id.listView_to_list_synonyms);
        listViewSyn.setOnItemClickListener(clickListener);
    }


    public void passArrayAdapter() {
        handler.post(new Runnable() {
            public void run() {
                ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
                listViewSyn.setAdapter(adapter);
            }
    });
    }


    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
           String  query = (String) parent.getItemAtPosition(position);

                Log.d("MainActivity", "ITEMS FROM ONLICK LISTENER DEBUGING " + query);

            seperateElements(query);
        }
    };


  public void  seperateElements(String s) {

      ArrayList element = new ArrayList();
      element.clear();
      list_to_seperated_elements.clear();

      element.add(s);
      element.add("|");

      Object[] arrayList = element.toArray();
      String a = (String) arrayList[0];

      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < arrayList.length; i++) {

          String words = (String) arrayList[i];
          char[] toletters = words.toCharArray();
    //      Log.d("SynonymsListFragment", "FROM ARRAY " + words);

          for (int j = 0; j < toletters.length; j++) {
              char lett = toletters[j];
              //    Log.d("SynonymsListFragment", "FROM ARRAY CHARS " + lett);

              if (lett != '|' ) {
                  getletters = String.valueOf(lett);
                  buffer.append(getletters);
              }
              if (lett == '|' || lett == ')') {
                  list_to_seperated_elements.add(buffer.toString());
                  Log.d("SynonymsListFragment", "FROM Buffer " + buffer);
                  buffer.delete(0, buffer.length());
              }
          }
      }
          for (int i = 0; i < list_to_seperated_elements.size(); i++) {
            Log.d("SynonymsListFragment", "FROM ARRAY CHARS inside arraylist    " + list_to_seperated_elements.get(i));
          }
  }

    public  void doSuggest(final String original) {

        list.clear();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> messages = new LinkedList<String>();
                String error = null;
                HttpURLConnection con = null;
                Log.d(TAG, "doSuggest(" + original + ")");

                try {
                    // Check if task has been interrupted
                    if (Thread.interrupted())
                        throw new InterruptedException();

                    // Build RESTful query for Google API
                    String q = URLEncoder.encode(original, "UTF-8");
                    URL url = new URL(
                            "http://thesaurus.altervista.org/thesaurus/v1?word="+ q +"&language=en_US&key=BBTGAUccf0ltEgaGUbPj&output=xml"
                          );
                    con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(10000 /* milliseconds */);
                    con.setConnectTimeout(15000 /* milliseconds */);
                    con.setRequestMethod("GET");
                    con.addRequestProperty("Referer",
                            "http://www.pragprog.com/book/eband4");
                    con.setDoInput(true);

                    // Start the query
                    con.connect();

                    // Check if task has been interrupted
                    if (Thread.interrupted())
                        throw new InterruptedException();

                    // Read results from the query
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(con.getInputStream(), null);
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String name = parser.getName();


                        if (eventType == XmlPullParser.TEXT) {
                            System.out.println("Text!!!!!!!!!!!!!!!!!!!!!!!!! " + parser.getText().toString());
                            list.add(parser.getText().toString());
                        }

                        eventType = parser.next();
                    }

                    passArrayAdapter();


                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                    //         error = suggest.getResources().getString(R.string.error)
                    //                  + " " + e.toString();
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "XmlPullParserException", e);
                    //      error = suggest.getResources().getString(R.string.error)
                    //                + " " + e.toString();
                } catch (InterruptedException e) {
                    Log.d(TAG, "InterruptedException", e);
                    //        error = suggest.getResources().getString(
                    //              R.string.interrupted);
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }



                // All done
                Log.d(TAG, "   -> returned " + messages);
                //  return messages;

            }


        });

        thread.start();
    }
}