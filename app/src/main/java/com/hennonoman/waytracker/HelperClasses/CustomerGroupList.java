package com.hennonoman.waytracker.HelperClasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hennonoman.waytracker.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Seotoolzz on 13/6/17.
 */
public class CustomerGroupList extends ArrayAdapter<list> {

    Activity context;
    List<list> items;
    Integer[] imageId = {
            R.drawable.man_icon
    };


    public CustomerGroupList(Activity mainActivity, ArrayList<list> dataArrayList) {
        super(mainActivity, 0, dataArrayList);

        this.context = mainActivity;
        this.items = dataArrayList;
    }


    private class ViewHolder {

        TextView idGroup, title;
        ImageView image;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomerGroupList.ViewHolder holder = null;

        if (convertView == null)
        {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(
                    R.layout.list_xml, parent, false);

            holder = new CustomerGroupList.ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.name);
            holder.idGroup = (TextView) convertView.findViewById(R.id.message);
            holder.image = (ImageView) convertView.findViewById(R.id.image_group);

            convertView.setTag(holder);

        } else {
            holder = (CustomerGroupList.ViewHolder) convertView.getTag();
        }

        list productItems = items.get(position);


        holder.title.setText(productItems.getTitle());
        holder.idGroup.setText(productItems.getIdGroup());

        holder.image.setImageResource(productItems.getImageId());

        return convertView;

    }


}