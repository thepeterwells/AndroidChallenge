package com.paandw.peter.androidchallenge;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KingdomInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KingdomInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KingdomInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "kingdomID";
    private static final String ARG_PARAM2 = "kingdomNAME";
    private static final String ARG_PARAM3 = "kingdomIMAGE";

    // TODO: Rename and change types of parameters
    private int kingdomID;
    private String kingdomNAME;
    private String kingdomIMAGE;

    private ImageView img;
    private TextView climateText, popText;
    private ArrayList<QuestInfo> questList;
    private RecyclerView recyclerView;
    private QuestListAdapter adapter;

    private KingdomInfo kingdomInfo;

    private OnFragmentInteractionListener mListener;

    public KingdomInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param kingdomID Parameter 1.
     * @return A new instance of fragment KingdomInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KingdomInfoFragment newInstance(int kingdomID, String kingdomNAME, String kingdomIMAGE) {
        KingdomInfoFragment fragment = new KingdomInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, kingdomID);
        args.putString(ARG_PARAM2, kingdomNAME);
        args.putString(ARG_PARAM3, kingdomIMAGE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kingdomID = getArguments().getInt(ARG_PARAM1);
            kingdomNAME = getArguments().getString(ARG_PARAM2);
            kingdomIMAGE = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kingdom_info, container, false);
        if (getArguments() != null) {
            kingdomID = getArguments().getInt(ARG_PARAM1);
            kingdomNAME = getArguments().getString(ARG_PARAM2);
            kingdomIMAGE = getArguments().getString(ARG_PARAM3);
        }
        img = (ImageView)v.findViewById(R.id.kingdom_info_image);
        climateText = (TextView)v.findViewById(R.id.climate_text);
        popText = (TextView)v.findViewById(R.id.population_text);
        recyclerView = (RecyclerView)v.findViewById(R.id.quest_list);

        kingdomInfo = new KingdomInfo();
        questList = new ArrayList<>();
        adapter = new QuestListAdapter(questList);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://challenge2015.myriadapps.com/api/v1/kingdoms/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KingdomInfoGrabber infoGrabber = retrofit.create(KingdomInfoGrabber.class);

        retrieveData(infoGrabber, v, 2);

        return v;
    }

    private void retrieveData(final KingdomInfoGrabber infoGrabber, final View v, final int recursions){
        infoGrabber.getKingdomInfo(kingdomID).enqueue(new Callback<KingdomInfo>() {
            @Override
            public void onResponse(Call<KingdomInfo> call, Response<KingdomInfo> response) {
                kingdomInfo = response.body();
                for(QuestInfo questInfo : kingdomInfo.getQuests()){
                    questList.add(questInfo);
                }
                adapter.notifyDataSetChanged();
                Glide.with(v.getContext()).load(kingdomIMAGE).into(img);
                climateText.setText(kingdomInfo.getClimate());
                String populationAsString = kingdomInfo.getPopulation() + "";
                popText.setText(populationAsString);
            }

            @Override
            public void onFailure(Call<KingdomInfo> call, Throwable t) {
                if(recursions >= 1) {
                    int temp = recursions;
                    retrieveData(infoGrabber, v, temp - 1);
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface KingdomInfoGrabber{
        @GET("{id}")
        Call<KingdomInfo> getKingdomInfo(@Path("id") int id);
    }

    //Private class defining adapter for quest list RecyclerView
    private class QuestListAdapter extends RecyclerView.Adapter<QuestListAdapter.MyViewHolder>{

        private ArrayList<QuestInfo> questInfoList;

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView questText;
            public MyViewHolder(View itemView) {
                super(itemView);
                questText = (TextView)itemView.findViewById(R.id.quest_list_item_text);
            }
        }

        public QuestListAdapter(ArrayList<QuestInfo> questInfoList){
            this.questInfoList = questInfoList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_list_item, parent, false);
            return new QuestListAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            QuestInfo quest = questInfoList.get(position);
            holder.questText.setText(quest.getName());
        }

        @Override
        public int getItemCount() {
            return questInfoList.size();
        }

    }
}
