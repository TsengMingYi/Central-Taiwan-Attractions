package com.example.googlemap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<SpotManager.Spot> spotList = new ArrayList<>();
    private ArrayList<SpotManager.Spot> spotFilterList = new ArrayList<>();

    public void updateProductData(ArrayList<SpotManager.Spot> spotList) {
        this.spotList = spotList;
        //notifyDataSetChanged();
        updateFilterData1("");
        //notifyDataSetChanged();
    }

    public void updateFilterData1(String filterString) {
        spotFilterList = new ArrayList<>();
        for (SpotManager.Spot spot : spotList) {
            if (spot.getType().contains(filterString)) {
                spotFilterList.add(spot);
                Log.e("hello", spot.getName() + spot.getType());
            }
            notifyDataSetChanged();
        }
        // todo test
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.attractions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SpotManager.Spot spot = spotFilterList.get(position);
        holder.attractions_name.setText(spot.getName());
        holder.attractions_type.setText(spot.getType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(view.getContext(), SpotDetailActivity.class);
//                    intent.putExtra("source", currentSelectName);
                intent.putExtra("regionId", spot.getRegionId());
                intent.putExtra("cityName", spot.getCityName());
                intent.putExtra("place_id", spot.getPlace_id());
                intent.putExtra("place_url", spot.getUrl());
                intent.putExtra("place_type", spot.getType());
                intent.putExtra("attractions_name", spot.getName());
                view.getContext().startActivity(intent);
            }
        });
    }


    /**
     * 0. return => 函數 裡面
     * 1. 傳回資料
     * 2. 函數結束
     */

//    public int a(int b){
//        b = 1;
//        return b;
//    }
    @Override
    public int getItemCount() {
        return spotFilterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView attractions_name;
        TextView attractions_type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attractions_name = itemView.findViewById(R.id.attractions_name);
            attractions_type = itemView.findViewById(R.id.attractions_type);
        }
//        public void updateView(SpotManager.Spot spot){
//            if(spot == null){
//                return;
//            }
//            attractions_name.setText(spot.getName());
//        }
    }
}
