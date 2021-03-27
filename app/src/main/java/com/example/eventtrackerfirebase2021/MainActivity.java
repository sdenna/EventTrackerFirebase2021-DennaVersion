package com.example.eventtrackerfirebase2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    private String dateSelected = "No date chosen";
    private int dateMonth;
    private int dateDay;
    private int dateYear;

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
        String eventName = eventNameET.getText().toString();

        // verify there is a name and date
        if (eventName.length() == 0 ) {
            Toast.makeText(MainActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
        else if (dateSelected.equals("No date chosen")) {
            Toast.makeText(MainActivity.this, "Please select Date", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i(TAG, "Trying to add: " + eventName + ", " + dateSelected);
        }
    }

    /**
     * This method will be called to minimize the on screen keyboard in the Activity
     * When we get the current view, it is the view that has focus, which is the keyboard
     * Credit - Found by Ram Dixit, 2019
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
}