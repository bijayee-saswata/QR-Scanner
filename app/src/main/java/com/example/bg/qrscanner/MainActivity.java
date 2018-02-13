package com.example.bg.qrscanner;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button scanBtn;
    TextView txtResult;
    private DatabaseReference mDatabase;
    private DatabaseReference rDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn=(Button)findViewById(R.id.scanBtn);
        txtResult=(TextView)findViewById(R.id.txtResult);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("data");
        final Activity activity=this;
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator=new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        txtResult=(TextView)findViewById(R.id.txtResult);
        if (result!=null)
        {
            if (result.getContents()==null)
            {
                Toast.makeText(this," Cancelled...",Toast.LENGTH_LONG).show();

            }
            else {
                mDatabase.push().setValue(result.getContents());
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for  (DataSnapshot datasnap: dataSnapshot.getChildren()){

                            String val = datasnap.getValue(String.class);
                            txtResult.setText(val);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
