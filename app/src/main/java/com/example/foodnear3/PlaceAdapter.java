package com.example.foodnear3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends BaseAdapter {

    private ArrayList<Place> places;
    private Context context;

    public PlaceAdapter(ArrayList<Place> places, Context context) {
        this.places = places;
        this.context = context;
    }

    private class ViewHolder {

        public ImageView place_img;
        public TextView tv_name, tv_address;

        public ViewHolder(View view) {
            place_img = view.findViewById(R.id.place_img);
            tv_name = view.findViewById(R.id.tv_name);
            tv_address = view.findViewById(R.id.tv_address);
        }
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.place_line, parent, false
            );
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Place place = (Place) getItem(position);

        Picasso
                .get()
                .load(place.getImage_link())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(viewHolder.place_img);
        viewHolder.tv_name.setText(place.getName());
        viewHolder.tv_address.setText(place.getAddress());

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_place_item);
        view.startAnimation(animation);

        return view;
    }
}