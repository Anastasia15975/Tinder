package com.example.tinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArrayAdapter extends android.widget.ArrayAdapter<Cards> {
    Context context;

    public ArrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Cards cardItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(cardItem.getName());
        switch (cardItem.getProfileImageUrl()) {
            case "default":
                Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:
                Glide.with(convertView.getContext()).clear(image);
                Glide.with(convertView.getContext()).load(cardItem.getProfileImageUrl()).into(image);
                break;
        }


        return convertView;
    }
}
