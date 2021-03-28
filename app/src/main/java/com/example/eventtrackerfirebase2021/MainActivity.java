package com.example.eventtrackerfirebase2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    private String dateSelected = "No date chosen";
    private int dateMonth;
    private int dateDay;
    private int dateYear;
    private String eventName;

    // Constants to use for labels in database
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY = "date";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";
    public static final String YEAR_KEY = "year";


    // reference to entire database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Video to learn basic access to CalendarView Data
        //  https://www.youtube.com/watch?v=WNBE_3ZizaA

        CalendarView calendarView = findViewById(R.id.eventCalendarDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
                public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                    dateSelected =  (month + 1) + "/" + day + "/" + year;
                    dateYear = year;
                    dateMonth = month + 1;
                    dateDay = day;
                    Log.i(TAG, "" + dateSelected);
                    closeKeyboard();
                }
            }
        );
    }

    public void addEventButtonPressed(View v) {
        EditText eventNameET = (EditText) findViewById(R.id.eventName);
        eventName = eventNameET.getText().toString();
        toastMessage("inside addEvent method");

        // verify there is a name and date
        if (eventName.length() == 0 ) {
            toastMessage("Please enter name");
        }
        else if (dateSelected.equals("No date chosen")) {
            toastMessage("Please select Date");
        }
        else {
            Log.i(TAG, "Trying to add: " + eventName + ", " + dateSelected);
            addEvent();
        }
    }

    public void addEvent() {
        // Creates a key-value map of the object to add to the collection
        Map<String, Object> eventToAdd = new HashMap<String, Object>();
        // Adds the all the key-value pairs to this object
        eventToAdd.put(NAME_KEY, eventName);
        eventToAdd.put(DATE_KEY, dateSelected);
        eventToAdd.put(MONTH_KEY, dateMonth);
        eventToAdd.put(DAY_KEY, dateDay);
        eventToAdd.put(YEAR_KEY,dateYear);
        Log.i(TAG, eventToAdd.toString());

        db.collection("events")
                .add(eventToAdd)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        toastMessage("Event stored successfully");
                        Log.i(TAG, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMessage("Event failed to add");
                        Log.i(TAG, "Failure");
                    }
                });

        // Clear the event name field
        EditText eventNameET = (EditText) findViewById(R.id.eventName);
        eventNameET.setText("");
    }

    public void showData(View v) {
        // This ArrayList will hold the current contents of the database and will be sent to the
        // DisplayEventsActivity after it is populated from firestore contents

        ArrayList<Event> myEvents = new ArrayList<Event>();

        // I did a query where I order the data pulled from firestore by date so the events are in chronological order
        // https://firebase.google.com/docs/firestore/query-data/order-limit-data

        db.collection("events").orderBy(DATE_KEY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Log.i(TAG, document.getId() + " =>" + document.getData());
                                Event e = new Event(document.getString(NAME_KEY),
                                                    document.getString(DATE_KEY),
                                                    document.getLong(MONTH_KEY).intValue(),
                                                    document.getLong(DAY_KEY).intValue(),
                                                    document.getLong(YEAR_KEY).intValue(),
                                                    document.getId());
                                myEvents.add(e);
                            }
                        }
                        else {
                            Log.i(TAG, "Error getting documents", task.getException());
                        }

                        // Start new activity and send it the ArrayList of Event objects
                        Intent intent = new Intent(MainActivity.this, DisplayEventsActivity.class);
                        intent.putExtra("events", myEvents);
                        startActivity(intent);
                    }
                });
    }

    /**
     * This method will be called to minimize the on screen keyboard in the Activity
     * When we get the current view, it is the view that has focus, which is the keyboard
     * Credit - Found and suggested by Ram Dixit, 2019
     *
     * Source:  https://www.youtube.com/watch?v=CW5Xekqfx3I
     */
    private void closeKeyboard() {
        View view = this.getCurrentFocus();     // view will refer to the keyboard
        if (view != null ){                     // if there is a view that has focus
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*
        This method improves readability of the code for toast messages.  It is a simple helper method
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}