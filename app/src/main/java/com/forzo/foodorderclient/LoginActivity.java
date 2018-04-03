package com.forzo.foodorderclient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @OnClick(R.id.sign_in_btn)
    public void onViewClicked() {

        final AlertDialog dialog = new SpotsDialog(LoginActivity.this);
        dialog.show();

        final String email_text = email.getText().toString().trim();
        String password_text = password.getText().toString().trim();
        if (!TextUtils.isEmpty(email_text) && !TextUtils.isEmpty(password_text)) {
            mAuth.signInWithEmailAndPassword(email_text, password_text)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final String userId = mAuth.getCurrentUser().getUid();
                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(userId)) {
                                            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "Sign In Failed, Password must be of minimum length 6",
                                                    Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this,
                                                "Sign In Failed, Password must be of minimum length 6",
                                                Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Sign In Failed, Password must be of minimum length 6",
                                        Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

        } else {
            Toast.makeText(LoginActivity.this, "Please Enter all the field", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }
}
