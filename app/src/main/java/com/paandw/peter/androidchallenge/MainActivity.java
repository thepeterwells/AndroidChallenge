package com.paandw.peter.androidchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener, KingdomListFragment.OnFragmentInteractionListener,
                   KingdomInfoFragment.OnFragmentInteractionListener{

    private LoginFragment login;
    private KingdomListFragment kingdoms;
    private KingdomInfoFragment selectedKingdom;
    private KingdomInfo selectedKingdomInfo;
    private FragmentManager fragmentManager;

    private SharedPreferences emailStorage;
    private String userEmail;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private FrameLayout fragmentContainer;


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
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        if(userEmail.equals("null")) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, login).commit();
        }
        else {
            updateToolbar(userEmail, false);
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
        else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return false;
    }

    public void submitClick(View view){
        userEmail = login.submissionClick();

        if(!userEmail.equals("")){
            SharedPreferences.Editor editor = emailStorage.edit();
            editor.putString("Email", userEmail);
            editor.commit();

            updateToolbar(userEmail, false);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, kingdoms).commit();
        }
    }

    public void kingdomListSelection(View view){
        ArrayList<Kingdom> kingdomList = kingdoms.getKingdomList();
        int kingdomIndex = kingdoms.getSelectedKingdomIndex();

        if(kingdomList != null && kingdomIndex != -1){
            int id = kingdomList.get(kingdomIndex).getID();
            String name = kingdomList.get(kingdomIndex).getName();
            String image = kingdomList.get(kingdomIndex).getImage();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://challenge2015.myriadapps.com/api/v1/kingdoms/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            SelectedKingdomInfoGrabber infoGrabber = retrofit.create(SelectedKingdomInfoGrabber.class);

            retrieveData(id, infoGrabber, view, 2);

            selectedKingdom = KingdomInfoFragment.newInstance(id, name, image);
            //getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    //.add(R.id.fragment_container, selectedKingdom).show(selectedKingdom).hide(kingdoms).commit();

            viewPager.setAdapter(new CustomPageAdapter(getSupportFragmentManager()));
            fragmentContainer.setVisibility(View.INVISIBLE);

            updateToolbar(name, true);
        }
    }

    private void retrieveData(final int kingdomID, final SelectedKingdomInfoGrabber infoGrabber,
                              final View v, final int recursions){
        infoGrabber.getKingdomInfo(kingdomID).enqueue(new Callback<KingdomInfo>() {
            @Override
            public void onResponse(Call<KingdomInfo> call, Response<KingdomInfo> response) {
                selectedKingdomInfo = response.body();
            }

            @Override
            public void onFailure(Call<KingdomInfo> call, Throwable t) {
                if(recursions >= 1) {
                    int temp = recursions;
                    retrieveData(kingdomID, infoGrabber, v, temp - 1);
                }
            }
        });
    }

    private void updateToolbar(String title, boolean hasBackArrow){
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(hasBackArrow);
        getSupportActionBar().show();
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getFragments().contains(selectedKingdom)){
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(kingdoms).remove(selectedKingdom).commit();
            fragmentContainer.setVisibility(View.VISIBLE);
            updateToolbar(userEmail, false);
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface SelectedKingdomInfoGrabber{
        @GET("{id}")
        Call<KingdomInfo> getKingdomInfo(@Path("id") int id);
    }


    //Nested class defining ViewPager adapter (for scrolling through quests)
    private class CustomPageAdapter extends FragmentPagerAdapter{

        public CustomPageAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return selectedKingdom;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}