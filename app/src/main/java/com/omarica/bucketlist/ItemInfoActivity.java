package com.omarica.bucketlist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ItemInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    EditText nameEditText, descriptionEditText;
    Button addButton;
    Calendar calendar;
    DateFormat dateFormat;
    TextView dueDateTextView;
    LatLng position;
    Toolbar mToolbar;
    BucketItem item;
    Marker marker;
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            dueDateTextView.setText("Due Date: " + dateFormat.format(calendar.getTime()));
        }
    };
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleMap mMap;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Going back to previous activity
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        if (id == R.id.log_out) {

            mAuth.signOut();
            finish();
            Intent intent = new Intent(ItemInfoActivity.this,LoginActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        nameEditText = findViewById(R.id.itemNameEditText);
        descriptionEditText = findViewById(R.id.itemDescriptionEditText);
        addButton = findViewById(R.id.addButton);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        mToolbar = findViewById(R.id.toolbar);



        setActionBar(mToolbar);


        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance();
        item = getIntent().getParcelableExtra("item");
        myRef = database.getReference("users").child(user.getUid()).child(item.getKey());

        calendar.setTimeInMillis(item.getDueDate());


        position = new LatLng(Double.parseDouble(item.getLocation().split(",")[0]),
                Double.parseDouble(item.getLocation().split(",")[1]));

        nameEditText.setText(item.getName());
        descriptionEditText.setText(item.getDescription());
        dueDateTextView.setText("Due Date: "+dateFormat.format(new Date(item.getDueDate())));

        dueDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ItemInfoActivity.this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!nameEditText.getText().toString().equals("") &&
                        !descriptionEditText.getText().toString().equals("")) {
                    myRef.child("name").setValue(nameEditText.getText().toString());
                    myRef.child("description").setValue(descriptionEditText.getText().toString());
                   // myRef.child("status").setValue(false);
                    myRef.child("location").setValue(position.latitude + "," + position.longitude);
                    myRef.child("imgUrl").setValue("https://cdn.onlinewebfonts.com/svg/img_255634.png");
                    myRef.child("dueDate").setValue(calendar.getTimeInMillis());
                    finish();
                } else {
                    Toast.makeText(ItemInfoActivity.this, "Please enter the item's information", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                position = marker.getPosition();

            }
        });

        mMap.addMarker(new MarkerOptions().position(position).title("Selected Location")).setDraggable(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));


    }


}
