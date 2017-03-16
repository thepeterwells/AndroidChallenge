package com.paandw.peter.androidchallenge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KingdomListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KingdomListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KingdomListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Kingdom> kingdomList;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private KingdomListAdapter adapter;

    //This will return false until the kingdom list has been created
    private boolean kingdomListPopulated = false;
    private int selectedKingdomIndex;

    public KingdomListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KingdomListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KingdomListFragment newInstance(String param1, String param2) {
        KingdomListFragment fragment = new KingdomListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kingdom_list, container, false);
        final Context context = v.getContext();

        kingdomList = new ArrayList<>();
        recyclerView = (RecyclerView)v.findViewById(R.id.kingdom_list);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListDivider(context));
        adapter = new KingdomListAdapter(kingdomList);
        recyclerView.setAdapter(adapter);

        setHasOptionsMenu(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://challenge2015.myriadapps.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KingdomListGrabber infoGrabber = retrofit.create(KingdomListGrabber.class);

        infoGrabber.getKingdoms().enqueue(new Callback<List<Kingdom>>() {
            @Override
            public void onResponse(Call<List<Kingdom>> call, Response<List<Kingdom>> response) {
                for(Kingdom kingdom : response.body()) {
                    kingdomList.add(kingdom);
                }
                adapter.notifyDataSetChanged();
                recyclerView.addOnItemTouchListener(new CustomTouchListener(context, recyclerView, new CustomClickListener() {
                    @Override
                    public void onClick(View v, int clickPosition) {
                        selectedKingdomIndex = clickPosition;
                    }
                }));
                kingdomListPopulated = true;
            }

            @Override
            public void onFailure(Call<List<Kingdom>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

        return v;
    }

    public ArrayList<Kingdom> getKingdomList(){
        if(kingdomListPopulated)
            return kingdomList;
        else
            return null;
    }
    public int getSelectedKingdomIndex(){
        if(kingdomListPopulated)
            return selectedKingdomIndex;
        else
            return -1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.logout_menu, menu);
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

    public interface KingdomListGrabber{
        @GET("kingdoms")
        Call<List<Kingdom>> getKingdoms();
    }

    //Private class and interface for custom touch listener
    private interface CustomClickListener{
        void onClick(View v, int clickPosition);
    }
    private class CustomTouchListener implements RecyclerView.OnItemTouchListener{

        private CustomClickListener clickListener;
        private GestureDetector gesture;

        public CustomTouchListener(Context context, final RecyclerView rv, final CustomClickListener clickListener){
            this.clickListener = clickListener;
            gesture = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e){
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if(childView != null && gesture.onTouchEvent(e))
                clickListener.onClick(childView, rv.getChildAdapterPosition(childView));
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    //Private class for drawing divider between list items
    private class ListDivider extends RecyclerView.ItemDecoration{
        private Drawable divider;

        public ListDivider(Context context){
            divider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
            int left = parent.getPaddingLeft() + 250;
            int right = parent.getWidth() - parent.getPaddingRight() - 50;

            int children = parent.getChildCount();
            for(int i = 0; i < children; i++){
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
                int top = child.getBottom() - params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    //Private class containing the adapter for the kingdom list RecyclerView
    private class KingdomListAdapter extends RecyclerView.Adapter<KingdomListAdapter.MyViewHolder>{

        private ArrayList<Kingdom> kingdomList;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView kingdomName;
            public ImageView kingdomImage;
            public MyViewHolder(View view) {
                super(view);
                context = view.getContext();
                kingdomName = (TextView)view.findViewById(R.id.kingdom_list_name);
                kingdomImage = (ImageView)view.findViewById(R.id.kingdom_list_image);
            }
        }

        public KingdomListAdapter(ArrayList<Kingdom> kingdomList){
            this.kingdomList = kingdomList;
        }

        @Override
        public KingdomListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.kingdom_list_item, parent, false);
            return new KingdomListAdapter.MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(KingdomListAdapter.MyViewHolder holder, int position) {
            Kingdom kingdom = kingdomList.get(position);
            ImageView img = holder.kingdomImage;
            holder.kingdomName.setText(kingdom.getName());

            Glide.with(context).load(kingdom.getImage()).into(img);

        }

        @Override
        public int getItemCount() {
            return kingdomList.size();
        }

    }
}
