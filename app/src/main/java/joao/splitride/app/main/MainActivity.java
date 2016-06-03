package joao.splitride.app.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;

import joao.splitride.R;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.fragments.MovementsFragment;
import joao.splitride.app.fragments.MyCalendarsFragment;
import joao.splitride.app.fragments.RoutesFragment;
import joao.splitride.app.fragments.Segments;
import joao.splitride.app.fragments.UsersFragment;
import joao.splitride.app.fragments.VehiclesFragment;
import joao.splitride.app.login.DispatchActivity;
import joao.splitride.app.settings.AddEditCalendar;
import joao.splitride.app.settings.AddEditMovements;
import joao.splitride.app.settings.AddEditRoute;
import joao.splitride.app.settings.AddEditSegment;
import joao.splitride.app.settings.AddEditTrip;
import joao.splitride.app.settings.AddEditVehicle;
import joao.splitride.app.settings.SearchUsers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;
    private Segments segments;
    private RoutesFragment routesFragment;
    private UsersFragment usersFragment;
    private MyCalendarsFragment calendarsFragment;
    private VehiclesFragment vehiclesFragment;
    private MovementsFragment movementsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Segments segments_frag = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
                RoutesFragment routes_frag = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");
                MyCalendarsFragment calendars_frag = (MyCalendarsFragment) getSupportFragmentManager().findFragmentByTag("CALENDARS");
                UsersFragment users_frag = (UsersFragment) getSupportFragmentManager().findFragmentByTag("PERSONS");
                VehiclesFragment vehicles_frag = (VehiclesFragment) getSupportFragmentManager().findFragmentByTag("VEHICLES");
                MovementsFragment movements_frag = (MovementsFragment) getSupportFragmentManager().findFragmentByTag("MOVEMENTS");

                if (segments_frag != null && segments_frag.isVisible()) {
                    Intent intent = new Intent(MainActivity.this, AddEditSegment.class);
                    startActivityForResult(intent, 1);
                }else if(routes_frag != null && routes_frag.isVisible()){
                    Intent intent = new Intent(MainActivity.this, AddEditRoute.class);
                    startActivityForResult(intent, 1);
                }else if(calendars_frag != null && calendars_frag.isVisible()){
                    Intent intent = new Intent(MainActivity.this, AddEditCalendar.class);
                    startActivity(intent);
                } else if (users_frag != null && users_frag.isVisible()) {
                    Intent intent = new Intent(MainActivity.this, SearchUsers.class);
                    startActivity(intent);
                } else if (vehicles_frag != null && vehicles_frag.isVisible()) {
                    Intent intent = new Intent(MainActivity.this, AddEditVehicle.class);
                    startActivityForResult(intent, 1);
                } else if (movements_frag != null && movements_frag.isVisible()) {
                    Intent intent = new Intent(MainActivity.this, AddEditMovements.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, AddEditTrip.class);
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();


        // Cenas do calendário


        final CaldroidListener calendarListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {

                AlertDialog levelDialog;

                // Strings to Show In Dialog with Radio Buttons
                final CharSequence[] items = {" Easy ", " Medium ", " Hard ", " Very Hard "};

                // Creating and Building the Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select The Difficulty Level");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        switch (item) {
                            case 0:
                                // Your code when first option seletced
                                break;
                            case 1:
                                // Your code when 2nd  option seletced

                                break;
                            case 2:
                                // Your code when 3rd option seletced
                                break;
                            case 3:
                                // Your code when 4th  option seletced
                                break;

                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                levelDialog = builder.create();
                levelDialog.show();

            }
        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(calendarListener);

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
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home){

            CaldroidFragment caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();

            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);

            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendar1, caldroidFragment);
            t.commit();

        }else  if (id == R.id.nav_my_calendars) {

            calendarsFragment = new MyCalendarsFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.calendar1, calendarsFragment, "CALENDARS");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();


        } else if (id == R.id.nav_movements) {
            if (hasCalendars()) {
                movementsFragment = new MovementsFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.calendar1, movementsFragment, "MOVEMENTS");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Error");
                alertDialog.setMessage("You have no calendars set. Please go to the My Calendars and create one.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
            }
        } else if (id == R.id.nav_routes) {
            if(hasCalendars()){
                routesFragment = new RoutesFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.calendar1, routesFragment, "ROUTES");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Error");
                alertDialog.setMessage("You have no calendars set. Please go to the My Calendars and create one.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {  }
                });
                alertDialog.show();
            }

        } else if (id == R.id.nav_persons) {
            if(hasCalendars()){
                usersFragment = new UsersFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.calendar1, usersFragment, "PERSONS");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Error");
                alertDialog.setMessage("You have no calendars set. Please go to the My Calendars and create one.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {  }
                });
                alertDialog.show();
            }


        } else if (id == R.id.nav_segments) {

            if(hasCalendars()){
                segments = new Segments();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.calendar1, segments, "SEGMENTS");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Error");
                alertDialog.setMessage("You have no calendars set. Please go to the My Calendars and create one.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {  }
                });
                alertDialog.show();

            }

        } else if (id == R.id.nav_vehicles) {

            if (hasCalendars()) {
                vehiclesFragment = new VehiclesFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.calendar1, vehiclesFragment, "VEHICLES");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Error");
                alertDialog.setMessage("You have no calendars set. Please go to the My Calendars and create one.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();

            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
        REMOVE METHODS
     */

    public void removeOnClickHandler(View v){

        Segments segment = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
        RoutesFragment route = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");
        MyCalendarsFragment myCalendars = (MyCalendarsFragment) getSupportFragmentManager().findFragmentByTag("CALENDARS");
        UsersFragment users = (UsersFragment) getSupportFragmentManager().findFragmentByTag("PERSONS");
        VehiclesFragment vehicles = (VehiclesFragment) getSupportFragmentManager().findFragmentByTag("VEHICLES");

        if (segment != null && segment.isVisible()) {
            segments.removeOnClickHandler(v);
        }else if(route != null && route.isVisible()){
            routesFragment.removeOnClickHandler(v);
        }else if(myCalendars != null && myCalendars.isVisible()){
            calendarsFragment.removeOnClickHandler(v);
        } else if (users != null && users.isVisible()) {
            usersFragment.removeOnClickHandler(v);
        } else if (vehicles != null && vehicles.isVisible()) {
            vehiclesFragment.removeOnClickHandler(v);
        }

    }

    /*
        EDIT METHODS
     */

    public void editOnClickHandler(View v){

        Segments segment = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
        RoutesFragment route = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");
        MyCalendarsFragment myCalendars = (MyCalendarsFragment) getSupportFragmentManager().findFragmentByTag("CALENDARS");
        VehiclesFragment vehicles = (VehiclesFragment) getSupportFragmentManager().findFragmentByTag("VEHICLES");

        if (segment != null && segment.isVisible()) {
            segments.editOnClickHandler(v);
        }else if(route != null && route.isVisible()){
            routesFragment.editOnClickHandler(v);
        }else if(myCalendars != null && myCalendars.isVisible()){
            calendarsFragment.editOnClickHandler(v);
        } else if (vehicles != null && vehicles.isVisible()) {
            vehiclesFragment.editOnClickHandler(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VehiclesFragment vehicles = (VehiclesFragment) getSupportFragmentManager().findFragmentByTag("VEHICLES");
        Segments segment = (Segments) getSupportFragmentManager().findFragmentByTag("SEGMENTS");
        RoutesFragment route = (RoutesFragment) getSupportFragmentManager().findFragmentByTag("ROUTES");

        if (segment != null && segment.isVisible()) {
            segments.onActivityResult(requestCode, resultCode, data);
        } else if (route != null && route.isVisible()) {
            routesFragment.onActivityResult(requestCode, resultCode, data);
        } else if (vehicles != null && vehicles.isVisible()) {
            vehiclesFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
        UTILS
     */
    private boolean hasCalendars(){

        boolean calendars = false;

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "");

        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
        query.whereEqualTo("UserID", userID);

        UsersByCalendars usersByCalendars = null;
        try {
            usersByCalendars = query.getFirst();

            if(usersByCalendars != null){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("calendarID", usersByCalendars.getCalendarID());
                editor.commit();

                calendars = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendars;
    }
}
