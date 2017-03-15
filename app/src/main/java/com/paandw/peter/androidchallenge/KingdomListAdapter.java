package com.paandw.peter.androidchallenge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * This is the adapter for the RecyclerView listing all the kingdoms
 * Created by Peter Wells on 3/14/2017.
 */

public class KingdomListAdapter extends RecyclerView.Adapter<KingdomListAdapter.MyViewHolder> {

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
        return new MyViewHolder(item);
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
