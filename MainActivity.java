package com.example.alex.settings;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final int CAMERA_REQUEST_CODE = 10;
    TimePickerDialog timePickerDialog;
    private ImageView imageView;
    private Button retakePhotoButton;
    private PendingIntent pendingAlarmIntent;
    private AlarmManager alarmManager;
    Firebase mRef;
    int year_picker,month_picker,day_picker;
    static final int DIALOG_ID = 70;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = new Firebase("https://resplendent-inferno-9296.firebaseio.com/");

        final Calendar cal = Calendar.getInstance();
        year_picker = cal.get(Calendar.YEAR);
        month_picker = cal.get(Calendar.MONTH);
        day_picker = cal.get(Calendar.DAY_OF_MONTH);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        imageView = (ImageView)findViewById(R.id.imageView);
        retakePhotoButton = (Button) findViewById(R.id.retakePhotoButton);
        retakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            takePhoto();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_setAlarm) {
            openTimePickerDialog(false);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_cancelAlarm){
            cancelAlarm();
        } else if (id == R.id.nav_calendar){
            openCalendar(DIALOG_ID);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


        private void openTimePickerDialog(boolean is24r){
            Calendar calendar = Calendar.getInstance();

            timePickerDialog = new TimePickerDialog(
                    MainActivity.this,
                    onTimeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    is24r);
            timePickerDialog.setTitle("Set Alarm Time");
            timePickerDialog.show();

        }

        TimePickerDialog.OnTimeSetListener onTimeSetListener
                = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();

                calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calSet.set(Calendar.MINUTE, minute);
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);

                if(calSet.compareTo(calNow) <= 0){
                    //Today Set time passed, count to tomorrow
                    calSet.add(Calendar.DATE, 1);
                }

                setAlarm(calSet);
            }};

    private void setAlarm(Calendar targetCal){
        Toast.makeText(this, "Alarm is set@ " + targetCal.getTime() + "\n", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingAlarmIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,targetCal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pendingAlarmIntent);

    }
    /** Initialize DatePickerDialog in order to open up calendar selector */
    DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_picker = year;
            month_picker = monthOfYear +1;
            day_picker = dayOfMonth;
            Toast.makeText(MainActivity.this,year_picker +"/"+ month_picker +"/"+day_picker,Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID)
            return new DatePickerDialog(this,dpickerListner,year_picker,month_picker,day_picker);
        return null;
    }
    //use this helper method to cancel an alarm
    private void cancelAlarm(){
        if(alarmManager != null) {
            alarmManager.cancel(pendingAlarmIntent);
            Toast.makeText(this,"Alarm cancelled",Toast.LENGTH_SHORT).show();
        }
    }

    private void openCalendar(int id){
        showDialog(id);
    }

    private void takePhoto(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //did user click OK?
        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_REQUEST_CODE){
                // we are hearing back from the camera
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                //at this point, we have the image from the camera
                imageView.setImageBitmap(cameraImage);
            }
        }

    }

    public void retakePhoto(View view) {
        takePhoto();
    }
}
