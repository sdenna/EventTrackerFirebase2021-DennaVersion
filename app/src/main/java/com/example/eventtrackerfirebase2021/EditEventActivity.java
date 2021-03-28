package com.example.eventtrackerfirebase2021;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {
    // reference to entire database
    private FirebaseFirestore db;
    public static final String TAG = "EditEventActivity";
    private EditText eventNameET;
    private EditText eventDateET;
    private String keyToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");

        String eventNameToUpdate = event.getEventName();
        String eventDateToUpdate = event.getEventDate();
        keyToUpdate = event.getKey();

        eventNameET = (EditText)findViewById(R.id.eventName);
        eventDateET = (EditText)findViewById(R.id.eventDate);

        eventNameET.setText(eventNameToUpdate);
        eventDateET.setText(eventDateToUpdate);
    }

    public void updateEventData(View v) {
        String newName = eventNameET.getText().toString();
        String newDate = eventDateET.getText().toString();
        int month = -1;
        int day = -1;
        int year = -1;

        // error checking to ensure date is of the form 01/17/1979 etc.
        if (newDate.length() != 10) {
            toastMessage("Please enter date as MM/DD/YYYY");
            return;
        }
        else if (newName.length() == 0) {
            toastMessage("Please enter a name for the event");
            return;
        }
        else
        {
            // prevents the app from crashing if user doesn't use correct date format
            try{
                month = Integer.parseInt(newDate.substring(0, 2));
                day =  Integer.parseInt(newDate.substring(3, 5));
                year =  Integer.parseInt(newDate.substring(6));
            }
            catch (Exception e) {
                toastMessage("Please enter date as MM/DD/YYYY");
                return;
            }
        }

        if (!(month > 0 && month < 13 && day > 0 && day < 32 )) {
            toastMessage("Please enter a valid month/day");
            return;
        }

        Map<String, Object> eventToAdd = new HashMap<String, Object>();
        // Adds the all the key-value pairs to this object
        eventToAdd.put(MainActivity.NAME_KEY, newName);
        eventToAdd.put(MainActivity.DATE_KEY, newDate);
        eventToAdd.put(MainActivity.MONTH_KEY, month);
        eventToAdd.put(MainActivity.DAY_KEY, day);
        eventToAdd.put(MainActivity.YEAR_KEY,year);

        db.collection("events").document(keyToUpdate)
                .update(eventToAdd);
        }



    public void deleteEventData(View v) {
        db.collection("events").document(keyToUpdate).delete();
        onHome(v);              // reloads opening screen
    }

    public void onHome(View v){
        Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onRetrieve(View v){
            // This ArrayList will hold the current contents of the database and will be sent to the
            // DisplayEventsActivity after it is populated from firestore contents

            ArrayList<Event> myEvents = new ArrayList<Event>();

            // I did a query where I order the data pulled from firestore by date so the events are in chronological order
            // https://firebase.google.com/docs/firestore/query-data/order-limit-data

            db.collection("events").orderBy(MainActivity.DATE_KEY)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document: task.getResult()) {
                                    Log.i(TAG, document.getId() + " =>" + document.getData());
                                    Event e = new Event(document.getString(MainActivity.NAME_KEY),
                                            document.getString(MainActivity.DATE_KEY),
                                            document.getLong(MainActivity.MONTH_KEY).intValue(),
                                            document.getLong(MainActivity.DAY_KEY).intValue(),
                                            document.getLong(MainActivity.YEAR_KEY).intValue(),
                                            document.getId());
                                    myEvents.add(e);
                                }
                            }
                            else {
                                Log.i(TAG, "Error getting documents", task.getException());
                            }

                            // Start new activity and send it the ArrayList of Event objects
                            Intent intent = new Intent(EditEventActivity.this, DisplayEventsActivity.class);
                            intent.putExtra("events", myEvents);
                            startActivity(intent);
                        }
                    });
        }




    /*
       This method improves readability of the code for toast messages.  It is a simple helper method
    */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}