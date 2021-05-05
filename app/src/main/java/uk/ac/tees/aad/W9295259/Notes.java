package uk.ac.tees.aad.W9295259;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Notes extends AppCompatActivity {

    Button btnAddNotes;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ListView listView;
    ArrayList<String> arrayListString;
    Button searchNews;
    private boolean isSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        listView = findViewById(R.id.listView);
        btnAddNotes = findViewById(R.id.addNotes);
        searchNews = findViewById(R.id.searchNews);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        isSearch = false;

        searchNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = true;
                proceedToTakeImage();
            }
        });


        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToTakeImage();
            }
        });

        arrayListString = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_list_item_1,arrayListString);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView k = (TextView) view;
//                Toast.makeText(getApplicationContext(),k.getText().toString(),Toast.LENGTH_LONG).show();
//            }
//        });


        FirebaseDatabase.getInstance().getReference("Notes").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                arrayListString.add(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void proceedToTakeImage()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, 101);
        }else {
            startCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startCamera();
            }else {
                Toast.makeText(this, "Allow Camera to capture Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == Activity.RESULT_OK){
            sendImageDateToGetText(data);
        }
    }


    private void  sendImageDateToGetText( Intent data)
    {
        Bitmap thumb = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumb.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        byte bb[] = bytes.toByteArray();

        InputImage image = InputImage.fromBitmap(thumb,0);



        TextRecognizer recognizer = TextRecognition.getClient();

        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                saveTextToFirebase(visionText.getText());
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

    }

    void saveTextToFirebase(String text){
        if(isSearch){
            Intent intent = new Intent(getApplicationContext(),News.class);
            intent.putExtra("words", text);
            startActivity(intent);
        }else {
        FirebaseDatabase.getInstance().getReference("Notes").child(firebaseUser.getUid()).push().setValue(text);}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finishAffinity();
    }
}

