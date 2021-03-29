package com.example.eventtrackerfirebase2021;

        import android.os.Parcel;
        import android.os.Parcelable;
        import java.text.ParseException;

/**
 * This class implements the Parcelable interface so that Event objects can be passed through the intent
 * https://code.tutsplus.com/tutorials/how-to-pass-data-between-activities-with-android-parcelable--cms-29559
 *
 */
public class Event implements Parcelable
{
    private String eventName;
    private String eventDate;
    private int month;
    private int day;
    private int year;
    private String key;

    // needed  for the Parcelable code to work
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel parcel) {
            return new Event(parcel);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[0];
        }
    };


    /** This is a "constructor" of sorts that is needed with the Parceable interface to
     * tell the intent how to create an Event object when it is received from the intent
     * basically it is setting each instance variable as a String or Int
     * if the instance variables were objects themselves you would need to do more complex code
     *
     * @param parcel    the parcel that is received from the intent
     */

    public Event(Parcel parcel) {
        eventName = parcel.readString();
        eventDate = parcel.readString();
        month = parcel.readInt();
        day = parcel.readInt();
        year = parcel.readInt();
        key = parcel.readString();
    }

    /**
     * This is the regular constructor used in the traditional sense
     * We use this one when we do not know the unique Firebase key yet for the Event
     * @param eventName
     * @param eventDate
     * @param month
     * @param day
     * @param year
     */

    public Event(String eventName, String eventDate, int month, int day, int year) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.month = month;
        this.day = day;
        this.year = year;
        this.key = "no key yet";
    }

    /**
     * This constructor is used when the unique Firebase key is already known.
     * @param eventName
     * @param eventDate
     * @param month
     * @param day
     * @param year
     * @param key
     */
    public Event(String eventName, String eventDate, int month, int day, int year, String key) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.month = month;
        this.day = day;
        this.year = year;
        this.key = key;
    }

    @Override
    /**
     * This is what is used when we send the Event object through an intent
     * It is also a method that is part of the Parceable interface and is needed
     * to set up the object that is being sent.  Then, when it is received, the
     * other Event constructor that accepts a Parcel reference can "unpack it"
     *
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventDate);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(year);
        dest.writeString(key);
    }


    /**
     * This method is required for Parceable interface.  As of now, this method is in the default state
     * and doesn't really do anything.
     *
     * If your Parcelable class will have child classes, you'll need to
     *          * take some extra care with the describeContents() method. This will
     *          * let you identify the specific child class that should be created by
     *          * the Parcelable.Creator. You can read more about how this works on
     *          * Stack Overflow.
     *          *
     *          * https://stackoverflow.com/questions/4778834/purpose-of-describecontents-of-parcelable-interface
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    // BEGIN TYPICAL JAVA METHODS FOR EVENT CLASS

    public boolean equals(Event other) {
        return this.eventDate.equals(other.eventDate) && this.eventName.equals(other.eventName);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public int getYear(){
        return year;}

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }


    /**
     * This method creates the display ready version of the Event that will show up
     * in the listview when we show all events.
     * @return
     */
    public String toString() {
        String str = "";
        if (month == 1)
            str += "Jan ";
        else if (month == 2)
            str += "Feb ";
        else if (month == 3)
            str += "Mar ";
        else if (month == 4)
            str += "Apr ";
        else if (month == 5)
            str += "May ";
        else if (month == 6)
            str += "Jun ";
        else if (month == 7)
            str += "Jul ";
        else if (month == 8)
            str += "Aug ";
        else if (month == 9)
            str += "Sep ";
        else if (month == 10)
            str += "Oct ";
        else if (month == 11)
            str += "Nov ";
        else
            str += "Dec ";

        // Extra space to keep it looking uniform in listview
        if (day < 10)
            str += " ";

        str += day;
        str += ", " + year;

        return str;
    }
}
