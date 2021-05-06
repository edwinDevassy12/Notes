package uk.ac.tees.aad.W9295259;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    EditText pass;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);

        Button SignUpButton = findViewById(R.id.loginBtn);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }


    public void register(){
        if (!email.getText().toString().isEmpty()){
            email.setError("Enter Correct Value");
            email.findFocus();
        }
        if(pass.getText().toString().length()<6){
            pass.setError("Enter Correct Value");
            pass.findFocus();
        }

        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(getApplicationContext(),Notes.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Signup Failed Make sure Enter Correct details",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Notes.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
