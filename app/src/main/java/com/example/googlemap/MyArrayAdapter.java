package com.example.googlemap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyArrayAdapter extends ArrayAdapter {
    public MyArrayAdapter(@NonNull Context context, List<RegionData> mData) {
        super(context, 0, mData);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, false);
    }

    @SuppressLint("SetTextI18n")
    private View createView(int position, View convertView
            , ViewGroup parent, Boolean ageDisplay) {
        convertView = LayoutInflater.from(getContext()).inflate(//綁定介面
                R.layout.spinner_item, parent, false);
        TextView tvName = convertView.findViewById(R.id.textViewTitle);//控制介面元件
        TextView tvAge = convertView.findViewById(R.id.textViewAge);
        RegionData item = (RegionData) getItem(position);//取得每一筆的資料內容
        if (item != null) {
            tvName.setText(item.getName());
            tvAge.setText(item.getArea());
            if (ageDisplay) tvAge.setVisibility(View.VISIBLE);
            else tvAge.setVisibility(View.GONE);

        }
        return convertView;
    }//複寫介面
}
