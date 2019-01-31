package com.example.projetomobiledb1teste.ui;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.example.projetomobiledb1teste.R;

import java.sql.Date;
import java.text.DecimalFormat;

public class ValuesListAdapter extends RecyclerView.Adapter<ValuesListAdapter.MyViewHolder> {
        private int[] date;
        private float[] value;

    public ValuesListAdapter (int[] date, float[] value){
        this.date = date;
        this.value = value;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.values_list_item,viewGroup,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        float temp;
        if (value==null || date == null){
            return;
        }
        int size = value.length;
        int j = size-i-1;

        if (i < size-1) {
            temp = (1 - (value[j-1] / value[j]));

        } else {
            temp = 0;
        }

        final String formatoData = "dd/MM/yyyy";
        String formattedDate = ""+DateFormat.format(formatoData, new Date((long)date[j]*1000));
        myViewHolder.mTextViewDate.setText(formattedDate);

        final String dolarFormat = "US$#.00";
        DecimalFormat df = new DecimalFormat(dolarFormat);
        String formatedValue = df.format(value[j]);
        myViewHolder.mTextViewValue.setText(formatedValue);

        final String variationFormat = "#0.000%";
        df = new DecimalFormat(variationFormat);
        String formatedVariation = df.format(temp);
        myViewHolder.mTextViewVariation.setText(formatedVariation);

        if(temp==0){
             myViewHolder.mImageVariation.setImageResource(R.drawable.ic_neutral);
            myViewHolder.mTextViewVariation.setTextColor(Color.WHITE);
        }else if (temp>0){
            myViewHolder.mImageVariation.setImageResource(R.drawable.ic_up);
            myViewHolder.mTextViewVariation.setTextColor(Color.GREEN);
        }else if (temp<0){
            myViewHolder.mImageVariation.setImageResource(R.drawable.ic_down);
            myViewHolder.mTextViewVariation.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        if (date==null){
            return 0;
        }else {
            return date.length;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView mTextViewDate;
        TextView mTextViewValue;
        TextView mTextViewVariation;
        ImageView mImageVariation;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewDate = itemView.findViewById(R.id.list_item_date);
            mTextViewValue = itemView.findViewById(R.id.list_item_value);
            mTextViewVariation = itemView.findViewById(R.id.list_item_variation);
            mImageVariation = itemView.findViewById(R.id.image_variation);
        }
    }
}
