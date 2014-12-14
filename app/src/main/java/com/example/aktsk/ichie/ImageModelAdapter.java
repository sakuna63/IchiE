package com.example.aktsk.ichie;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ImageModelAdapter extends ArrayAdapter<ImageModel> {

    public ImageModelAdapter(Context context, List<ImageModel> items) {
        super(context, R.layout.list_item_image_model, android.R.id.text1, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageModel item = getItem(position);
        View view = super.getView(position, convertView, parent);
        ImageView stamp = (ImageView) view.findViewById(R.id.icon_stamp);
        Ion.with(stamp)
                .animateIn(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in))
                .load("http://" + item.getUrl());
        return view;
    }
}
