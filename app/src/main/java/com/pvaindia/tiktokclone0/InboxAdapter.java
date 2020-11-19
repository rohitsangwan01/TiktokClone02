package com.pvaindia.tiktokclone0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class InboxAdapter extends ArrayAdapter<InboxItem> {


    public InboxAdapter(@NonNull Context context, List<InboxItem> inboxItems) {
        super(context, 0, inboxItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        InboxItem currentInboxItem = getItem(position);

        CircularImageView profilePicImageView = listView.findViewById(R.id.profile_pic);
        Glide.with(getContext()).load(currentInboxItem.getProfilePicUrl()).placeholder(R.drawable.icon_user_profile).into(profilePicImageView);

        TextView headingTextView = listView.findViewById(R.id.heading);
        headingTextView.setText(currentInboxItem.getHeading());

        TextView timeDiffTextView = listView.findViewById(R.id.time_difference);
        timeDiffTextView.setText(currentInboxItem.getNotificationTimeDifference());

        return listView;
    }
}
