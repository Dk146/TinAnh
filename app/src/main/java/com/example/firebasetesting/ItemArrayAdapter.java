package com.example.firebasetesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemArrayAdapter extends ArrayAdapter<UserInfo> {

    public ItemArrayAdapter(Context context, int resource, List<UserInfo> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserInfo userInfo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(userInfo.getName());
        switch (userInfo.getProfileImageUrl()){
            case "default":
                Glide.with(getContext()).load(R.drawable.tinder_app).into(image);
                break;
            default:
                Glide.with(getContext()).load(userInfo.getProfileImageUrl()).into(image);
                break;
        }
        return convertView;
    }


}
