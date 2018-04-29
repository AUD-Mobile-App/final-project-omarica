package com.omarica.bucketlist;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by omarica on 3/21/18.
 */

public class ListAdapter extends ArrayAdapter {
    private DateFormat dateFormat;
    private Activity context;
    private ArrayList<BucketItem> mBucketItems;
    private DatabaseReference myRef;
    long currentTime;

    //Constructor
    public ListAdapter(Activity context,
                       ArrayList<BucketItem> mBucketItems, DatabaseReference myRef) {
        super(context, R.layout.list_item, mBucketItems);
        this.context = context;
        this.mBucketItems = mBucketItems;
        this.myRef = myRef;
        dateFormat = DateFormat.getDateInstance();
        currentTime = Calendar.getInstance().getTimeInMillis();
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View row = inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) row.findViewById(R.id.itemTextView);
        TextView txtDate = (TextView) row.findViewById(R.id.itemDateTextView);
        View statusColor = row.findViewById(R.id.statusColor);

        final CheckBox checkBox = row.findViewById(R.id.itemCheckbox);

        // Updating checks

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                myRef.child(mBucketItems.get(position).getKey()).child("status").setValue(b);
                String message = mBucketItems.get(position).getName()+ " is completed.";

                /*
                if(b) {
                    Snackbar.make(context.findViewById(R.id.myCoordinatorLayout), message,
                            Snackbar.LENGTH_SHORT)
                            .show();
                } */


            }
        });

        checkBox.setChecked(mBucketItems.get(position).isStatus());

        // Updating status
        if (mBucketItems.get(position).isStatus()) {

            statusColor.setBackgroundColor(Color.parseColor("#4CAF50"));
            txtDate.setText("Completed");


        } else {
            // statusColor.setBackgroundColor(Color.parseColor("#F44336"));
            txtDate.setText(dateFormat.format(new Date(mBucketItems.get(position).getDueDate())));
            switch (getDeadlineCategory(mBucketItems.get(position).getDueDate()))
            {
                case -1:
                    txtDate.setBackgroundResource(R.drawable.date_over_due);
                    txtDate.setText("Over Due");


                    break;

                case 1:
                    txtDate.setBackgroundResource(R.drawable.date_red);
                    break;

                case 2:
                    txtDate.setBackgroundResource(R.drawable.date_orange);

                    break;

                case 3:
                    txtDate.setBackgroundResource(R.drawable.date_yellow);

                    break;
                case 4:
                    txtDate.setBackgroundResource(R.drawable.date_green);


            }

        }

        txtTitle.setText(mBucketItems.get(position).getName());


        return row;
    }


    // A function to categorize deadlines based on weeks
    private int getDeadlineCategory(long dueDate)
    {
       if(new Date(dueDate).after(new Date(currentTime))) {
           long days = Math.abs(TimeUnit.MILLISECONDS.toDays(dueDate - currentTime)) ;
           //int days = (int) ((dueDate-currentTime) / (1000*60*60*24));

           if (days < 7) {
               return 1;
           } else if (days < 14)
               return 2;
           else if (days < 21)
               return 3;
           else
               return 4;
       }
      else
         return -1;


    }
}
