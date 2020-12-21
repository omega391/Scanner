package com.example.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ClipboardManager myClipboard;
    private ClipData myClip;

    Button nxt;
    Button Reset;
    CodeScanner codeScanner;
    CodeScannerView scannview;
    TextView resultData;
    TextView t2;
    FusedLocationProviderClient fusedLocationProviderClient;
    AlertDialog.Builder builder;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannview = findViewById(R.id.scannerview);
        codeScanner = new CodeScanner(this, scannview);
        resultData = findViewById(R.id.resultOfQr);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        Reset = findViewById(R.id.Resetbtn);
        nxt = findViewById(R.id.info);
        Context context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        builder = new AlertDialog.Builder(MainActivity.this);


        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                        resultData.setMovementMethod(LinkMovementMethod.getInstance());


                    }
                });

            }

        });
        nxt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick( View v) {
               builder.setMessage("Is it Correctly Scanned?")
                       .setCancelable(true)
                       .setTitle("Confirmation")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               String Value = resultData.getText().toString();
                               Intent intent = new Intent(MainActivity.this, Info.class);
                               intent.putExtra("key", Value);
                               startActivity(intent);
                           }

                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                               Toast.makeText(getApplicationContext(),"Press Reset to undo Scan.", Toast.LENGTH_SHORT).show();
                           }
                       });
               AlertDialog alert = builder.create();
               alert.setTitle("Confirm Scan?");
               alert.show();

           }

       });

                Reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        codeScanner.startPreview();
                        resultData.setText(null);

                    }

                });

        resultData.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text;
                text = resultData.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }



    @Override
        protected void onResume() {
            super.onResume();
            requestForCamera();
    }


    private void requestForCamera()     {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {

            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
               codeScanner.startPreview();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }
    public void opAct(){
        Intent intent = new Intent(this, Info.class);
        startActivity(intent);
    }


}