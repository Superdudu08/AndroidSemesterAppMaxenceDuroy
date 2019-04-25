package com.example.androidappproject;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    public ArrayList<LocationDBObject> locationList;
    final private OnListItemClickListener myOnListItemClickListener;

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }



    public LocationAdapter(ArrayList<LocationDBObject> list,OnListItemClickListener listener){
        locationList = list;
        myOnListItemClickListener = listener;
    }

    public void add(LocationDBObject location){
        locationList.add(location);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.locationitem,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(locationList.get(i).title);
        viewHolder.description.setText(locationList.get(i).description);
        //viewHolder.latitude.setText("Lat : "+locationList.get(i).latitude);
        //viewHolder.longitude.setText("Long : "+locationList.get(i).longitude);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView longitude;
        TextView latitude;
        TextView description;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.locationItemTitle);
            //longitude = itemView.findViewById(R.id.locationItemLongitude);
            //latitude = itemView.findViewById(R.id.locationItemLatitude);
            description = itemView.findViewById(R.id.locationItemDescription);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
