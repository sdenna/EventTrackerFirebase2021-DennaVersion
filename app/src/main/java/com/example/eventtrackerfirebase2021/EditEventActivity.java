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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {
    // reference to entire database
    private FirebaseFirestore db;
    public static final String TAG = "EditEventActivity";
    private EditText eventNameET;
    private CalendarView calendarView;
    private String keyToUpdate;
    private String dateSelected = "No date chosen";
    private int dateMonth;
    private int dateDay;
    private int dateYear;

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
        calendarView = (CalendarView) findViewById(R.id.eventCalendarDate);

        eventNameET.setText(eventNameToUpdate);

        // This allows us to parse out the date to get teh month, day year
        String parts[] = eventDateToUpdate.split("/");

        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        // Sets the month day, year on the calendar view so we can display the date
        // they chose and avoid having to error check their entry when they enter a new
        // date

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long milliTime = calendar.getTimeInMillis();
        calendarView.setDate(milliTime);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                dateSelected =  (month + 1) + "/" + day + "/" + year;
                dateYear = year;
                dateMonth = month + 1;
                dateDay = day;
                closeKeyboard();
            }
        });
    }

    public void updateEventData(View v) {
        String newName = eventNameET.getText().toString();

        Map<String, Object> eventToAdd = new HashMap<String, Object>();
        // Adds the all the key-value pairs to this object
        eventToAdd.put(MainActivity.NAME_KEY, newName);
        eventToAdd.put(MainActivity.DATE_KEY, dateSelected);
        eventToAdd.put(MainActivity.MONTH_KEY, dateMonth);
        eventToAdd.put(MainActivity.DAY_KEY, dateDay);
        eventToAdd.put(MainActivity.YEAR_KEY,dateYear);

        db.collection("events").document(keyToUpdate)
                .update(eventToAdd);
    }



    public void deleteEventData(View v) {
        db.collection("events").document(keyToUpdate).delete();
        this.finish();
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

        // I did a query where I order the data pulled from firestore by date so the events
        // are in chronological order.  However it orders them months 1,11,12, 2, 3, 4, etc :(
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();     // view will refer to the keyboard
        if (view != null ){                     // if there is a view that has focus
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}