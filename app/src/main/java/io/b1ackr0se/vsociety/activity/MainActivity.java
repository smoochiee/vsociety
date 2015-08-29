package io.b1ackr0se.vsociety.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.fragment.ForumFragment;
import io.b1ackr0se.vsociety.jsoup.Parser;
import io.b1ackr0se.vsociety.model.User;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.contentLayout) View contentLayout;

    private EditText usernameEdt;
    private EditText passwordEdt;
    private View header;
    private MaterialDialog dialog;
    private Parser parser;
    private SharedPreferences prefs;
    private boolean isLoggedIn;
    private HashMap<String, String> cookies;
    private User user;
    private boolean wrongCredentials;
    private boolean noInternetAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind views
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //init new instance of jsoup parser
        parser = new Parser(MainActivity.this);

        //get sharedPreferences file
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        new CheckForLogin().execute();

        setUpNavigation();

        showForumList();
    }

    private void readPrefs(){

    }

    private void checkLogin() {
    }

    private void login() {
        dialog = new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.login_to_voz)
                .customView(R.layout.dialog_login, true)
                .positiveText(R.string.login)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String name = usernameEdt.getText().toString();
                        String password = passwordEdt.getText().toString();

                        if (name.isEmpty())
                            usernameEdt.setError(getString(R.string.username_error));
                        if (password.isEmpty())
                            passwordEdt.setError(getString(R.string.password_error));

                        if (!name.isEmpty() && !password.isEmpty()) {
                            String[] credentials = {name, password};
                            dialog.dismiss();
                            new LoginAsyncTask().execute(credentials);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                }).build();

        if(dialog.getCustomView()!=null) {
            usernameEdt = (EditText) dialog.getCustomView().findViewById(R.id.usernameEdt);
            passwordEdt = (EditText) dialog.getCustomView().findViewById(R.id.passwordEdt);
        }

        dialog.show();
    }




    /**
     * Setup the DrawerLayout
     */
    private void setUpNavigation() {

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    /**
     * Inflate the forum fragment
     */
    private void showForumList() {
        ForumFragment fragment = new ForumFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Vsociety");
    }

    private void setTitle(String title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    private void inflateHeader() {
        if (!isLoggedIn) {
            header = navigationView.inflateHeaderView(R.layout.navigation_header);
            FrameLayout headerLayout = (FrameLayout) header.findViewById(R.id.headerFrame);
            headerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                    drawerLayout.closeDrawers();
                }
            });
        } else {
            if (header != null)
                navigationView.removeHeaderView(header);
            header = navigationView.inflateHeaderView(R.layout.navigation_header);
            FrameLayout headerLayout = (FrameLayout) header.findViewById(R.id.headerFrame);
            headerLayout.findViewById(R.id.loginText).setVisibility(View.GONE);
            headerLayout.findViewById(R.id.loginIcon).setVisibility(View.GONE);
            TextView welcome = (TextView) headerLayout.findViewById(R.id.name);
            welcome.setVisibility(View.VISIBLE);
            welcome.setText("Welcome, " + getUsername());
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return false;
            }
        });
    }

    private void saveUsername(String username){
        prefs = MainActivity.this.getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply();
    }

    private String getUsername() {
        prefs = MainActivity.this.getSharedPreferences("name", MODE_PRIVATE);
        return prefs.getString("username", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    public class CheckForLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            isLoggedIn = parser.isUserLoggedIn();
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            inflateHeader();
        }
    }

    public class LoginAsyncTask extends AsyncTask<String[], Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String[]... input) {
            try {
                String[] credentials = input[0];
                String title = parser.login(credentials[0], credentials[1]);
                switch (title) {
                    case "vozForums - User Control Panel":
                        user = parser.getLoggedInUser();
                        saveUsername(user.getName());
                        return true;
                    case "vozForums":
                        wrongCredentials = true;
                        return false;
                    default:
                        noInternetAccess = true;
                        return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                noInternetAccess = true;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                isLoggedIn = true;
                inflateHeader();
            } else {
                if (wrongCredentials)
                    Snackbar.make(contentLayout, "Wrong username or password", Snackbar.LENGTH_SHORT).show();
                else {
                    final Snackbar snackbar = Snackbar.make(contentLayout, "No internet access", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    login();
                                }
                            }, 500);

                        }
                    });
                    snackbar.show();
                }
            }
        }
    }
}
