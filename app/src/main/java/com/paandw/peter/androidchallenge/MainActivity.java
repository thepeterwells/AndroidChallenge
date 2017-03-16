package com.paandw.peter.androidchallenge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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
                   KingdomInfoFragment.OnFragmentInteractionListener, QuestInfoFragment.OnFragmentInteractionListener{

    private LoginFragment login;
    private KingdomListFragment kingdoms;
    private KingdomInfoFragment selectedKingdom;
    private KingdomInfo selectedKingdomInfo;
    private FragmentManager fragmentManager;
    private ArrayList<QuestInfo> questList;

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
        questList = new ArrayList<>();

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

            viewPager.setAdapter(new CustomPageAdapter(getSupportFragmentManager(), questList));

            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                @Override
                public void onPageSelected(int pos){
                    if(pos > 0)
                        updateToolbar(selectedKingdomInfo.getName() + " | " + questList.get(pos - 1).getName(), true);
                    else
                        updateToolbar(selectedKingdomInfo.getName(), true);
                }
            });

            slideView(-view.getWidth());
            updateToolbar(name, true);
        }
    }

    public void questListSelection(View view){
        if(!questList.isEmpty() && selectedKingdom.getQuestSelectionIndex() != -1){
            int index = selectedKingdom.getQuestSelectionIndex();
            viewPager.setCurrentItem(index + 1, true);
        }
    }

    private void retrieveData(final int kingdomID, final SelectedKingdomInfoGrabber infoGrabber,
                              final View v, final int recursions){
        infoGrabber.getKingdomInfo(kingdomID).enqueue(new Callback<KingdomInfo>() {
            @Override
            public void onResponse(Call<KingdomInfo> call, Response<KingdomInfo> response) {
                selectedKingdomInfo = response.body();
                ArrayList<QuestInfo> temp = selectedKingdomInfo.getQuests();
                for(QuestInfo q : temp){
                    questList.add(q);
                }
                viewPager.getAdapter().notifyDataSetChanged();
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
        kingdoms.setHasOptionsMenu(!hasBackArrow);
        getSupportActionBar().show();
    }

    private void slideView(final int position){
        fragmentContainer.animate().translationX(position).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(position >= 0)
                    fragmentContainer.setVisibility(View.VISIBLE);
                else
                    fragmentContainer.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(!questList.isEmpty()){
            fragmentContainer.setVisibility(View.VISIBLE);
            questList.clear();
            viewPager.getAdapter().notifyDataSetChanged();
            selectedKingdom.clearRecyclerViewListeners();
            slideView(0);

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
    private class CustomPageAdapter extends FragmentStatePagerAdapter{

        private ArrayList<QuestInfo> quests;

        public CustomPageAdapter(FragmentManager manager, ArrayList<QuestInfo> quests){
            super(manager);
            this.quests = quests;
        }

        @Override
        public Fragment getItem(int position) {
            if(position > 0){
                QuestInfo  quest = quests.get(position - 1);
                int id = quest.getID();
                String name = quest.getName();
                String img = quest.getImage();
                String desc = quest.getDescription();
                int giverID = quest.getQuestGiverID();
                return QuestInfoFragment.newInstance(id, name, desc, img, giverID);
            }
            else {
                return selectedKingdom;
            }
        }

        @Override
        public int getCount() {
            if(quests.size() == 0)
                return 1;
            else
                return quests.size() + 1;
        }
    }
}