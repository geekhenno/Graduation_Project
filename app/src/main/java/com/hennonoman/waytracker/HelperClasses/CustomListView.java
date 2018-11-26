package com.hennonoman.waytracker.HelperClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hennonoman.waytracker.R;

import java.util.ArrayList;

public class CustomListView extends ArrayAdapter{

    public CustomListView(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater l1=LayoutInflater.from(getContext());

        View mView=l1.inflate(R.layout.customlistview, parent,false);

        TextView mtext=(TextView) mView.findViewById(R.id.namegroup);
        ImageView mImageView=(ImageView) mView.findViewById(R.id.imagegroup);

        mtext.setText(getItem(position).toString());
        mImageView.setImageResource(R.drawable.favorite);





        return mView;
    }
}
