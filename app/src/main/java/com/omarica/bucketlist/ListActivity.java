package com.omarica.bucketlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef ;
    FloatingActionButton fab;
    ArrayList<BucketItem> mBucketItems;
    ArrayList<BucketItem> mDoneItems;
    ListAdapter mListAdapter;
    ListView mListView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        //Handling logging out
        if (id == R.id.log_out) {

            mAuth.signOut();
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initializing the fields
        mBucketItems = new ArrayList<>();
        mDoneItems = new ArrayList<>();
        fab = findViewById(R.id.fab);
        mListView = findViewById(R.id.listView);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(user.getUid());
        mListAdapter = new ListAdapter(this, mBucketItems, myRef);
        mListView.setAdapter(mListAdapter);


        //Handling long item clicks to delete an item
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                new AlertDialog.Builder(ListActivity.this)
                        .setTitle("Do you want to delete this item?")
                        .setMessage("Note that deleted items can't be restored.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                myRef.child(mBucketItems.get(i).getKey()).removeValue();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return false;
            }
        });

        // Handling editing an item
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(ListActivity.this, i+"", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ListActivity.this,ItemInfoActivity.class);
                intent.putExtra("item",mBucketItems.get(i));
                startActivity(intent);
            }
        });

        //Getting ordered query from Firebase
        myRef.orderByChild("dueDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getBucketList(dataSnapshot);
                mListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Handling adding a new item

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ListActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });



    }

    private void getBucketList(DataSnapshot dataSnapshot) {


        //Populating an array list from the data obtained from firebase
        mBucketItems.clear();
        mDoneItems.clear();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            BucketItem item = new BucketItem(ds.getValue(BucketItem.class).getName(),
                    ds.getValue(BucketItem.class).getDescription(),
                    ds.getValue(BucketItem.class).getImgUrl(),
                    ds.getValue(BucketItem.class).getLocation(),
                    ds.getValue(BucketItem.class).isStatus(),
                    ds.getKey(),
                    ds.getValue(BucketItem.class).getDueDate());

            if(!ds.getValue(BucketItem.class).isStatus()) {
                mBucketItems.add(item);
            }
            else{
                mDoneItems.add(item);
            }

        }
        mBucketItems.addAll(mDoneItems);

        //Collections.reverse(mBucketItems);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
