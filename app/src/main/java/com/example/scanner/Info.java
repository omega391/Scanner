package com.example.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Info extends AppCompatActivity {
    TextView fname, mname, lname, contact, add, emailadd, loc;
    DatabaseReference reff;
    private Button qr, additem;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        qr = (Button) findViewById(R.id.qr);
        fname = (TextView)findViewById(R.id.fname);
        mname = (TextView)findViewById(R.id.mname);
        lname = (TextView)findViewById(R.id.lname);
        contact = (TextView)findViewById(R.id.contact);
        add = (TextView)findViewById(R.id.add);
        additem = (Button)findViewById(R.id.additem);
        emailadd = (TextView)findViewById(R.id.email);
        loc = (TextView)findViewById(R.id.loc);
            getData();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        if (ActivityCompat.checkSelfPermission(Info.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


        } else {

            ActivityCompat.requestPermissions(Info.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Info.this);
                alert.setTitle("Confirmation");
                alert.setMessage("Are you sure?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      additem();


                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }


        });
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Context context;

                    try {
                        Geocoder geocoder = new Geocoder(Info.this, Locale.getDefault());
                        Locale.getDefault();

                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        loc.setText(addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getData (){
        String myIntent = super.getIntent().getStringExtra("key");
        reff = FirebaseDatabase.getInstance().getReference().child("Userinfo").child(myIntent);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstname = dataSnapshot.child("fname").getValue().toString();
                String middlename = dataSnapshot.child("mname").getValue().toString();
                String lastname = dataSnapshot.child("lname").getValue().toString();
                String contactnum = dataSnapshot.child("cpnumber").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                emailadd.setText(email);
                fname.setText(firstname);
                mname.setText(middlename);
                lname.setText(lastname);
                contact.setText(contactnum);
                add.setText(address);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

        private void   additem() {

            final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
            final String finame = fname.getText().toString().trim();
            final String midname = mname.getText().toString().trim();
            final String laname= lname.getText().toString().trim();
            final String contactadd = contact.getText().toString().trim();
            final String homeadd = add.getText().toString().trim();
            final String emailad = emailadd.getText().toString().trim();
            final String loca = loc.getText().toString().trim();



            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxHOFYv25Kw3FH6DCmVJFPFVzbmSETH1Nm7Vu9PQcrtpLttMaCM/exec",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            loading.dismiss();
                            Toast.makeText(Info.this, "Entry recorded in Database.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parmas = new HashMap<>();

                    //here we pass params
                    parmas.put("action","addItem");
                    parmas.put("finame",finame);
                    parmas.put("midname",midname);
                    parmas.put("laname",laname);
                    parmas.put("contactadd",contactadd);
                    parmas.put("homeadd",homeadd);
                    parmas.put("emailadd",emailad);
                    parmas.put("loca",loca);

                    return parmas;
                }
            };

            int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);

            RequestQueue queue = Volley.newRequestQueue(this);

            queue.add(stringRequest);


        }
    }
