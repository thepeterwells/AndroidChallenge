package com.paandw.peter.androidchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener, KingdomListFragment.OnFragmentInteractionListener{

    private LoginFragment login;
    private KingdomListFragment kingdoms;

    private SharedPreferences emailStorage;
    private String userEmail;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailStorage = getPreferences(Context.MODE_PRIVATE);
        userEmail = emailStorage.getString("Email", "null");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        login = new LoginFragment();
        kingdoms = new KingdomListFragment();

        login.setArguments(getIntent().getExtras());
        kingdoms.setArguments(getIntent().getExtras());

        if(userEmail.equals("null")) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, login).commit();
        }
        else {
            updateToolbar(userEmail);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, kingdoms).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout_item) {
            SharedPreferences.Editor editor = emailStorage.edit();
            editor.putString("Email", "null");
            editor.commit();
            getSupportActionBar().hide();
            login = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, login).commit();
        }

        return false;
    }

    public void submitClick(View view){
        userEmail = login.submissionClick();

        if(!userEmail.equals("")){
            SharedPreferences.Editor editor = emailStorage.edit();
            editor.putString("Email", userEmail);
            editor.commit();

            updateToolbar(userEmail);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, kingdoms).commit();
        }
    }

    private void updateToolbar(String title){
        getSupportActionBar().setTitle(title);
        getSupportActionBar().show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}