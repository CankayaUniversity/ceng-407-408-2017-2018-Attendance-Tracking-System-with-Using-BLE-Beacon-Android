package seniorproject.attendancetrackingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class WelcomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BackgroundWorker.TaskCompleted {
    private Actor user;
    private Bundle bundle;
    private TextView TV_Name, TV_Mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        bundle = intent.getExtras();



        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        if (bundle.get("userType").toString().equals("student")) {

            user = new Student();
            backgroundWorker.execute("get", "student-info", "Request", "true", "id", bundle.get("user_id").toString());
        } else if (bundle.get("userType").toString().equals("lecturer")) {
            user = new Lecturer();
            backgroundWorker.execute("get", "lecturer-info", "Request", "true", "id", bundle.get("user_id").toString());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TV_Name = (TextView) header.findViewById(R.id.w_user_name);
        TV_Mail = (TextView) header.findViewById(R.id.w_user_mail);
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
        getMenuInflater().inflate(R.menu.welcome_page, menu);
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTaskComplete(String result) {
        String[] tokens = result.split("[\n]+");


        if (bundle.get("userType").toString().equals("student")) {
            user = new Student();
            ((Student) user).setStudentNumber(Integer.parseInt(tokens[0]));
            user.setName(tokens[1]);
            user.setSurname(tokens[2]);
            user.setMail(tokens[3]);
            ((Student) user).setPhoneNumber(tokens[4]);
        } else if (bundle.get("userType").toString() == "lecturer") {
            user = new Lecturer();
            user.setName(tokens[0]);
            user.setSurname(tokens[1]);
            user.setMail(tokens[2]);
        }

         TV_Mail.setText(user.getMail());
         TV_Name.setText(user.getName()+ " " + user.getSurname());
    }
}
