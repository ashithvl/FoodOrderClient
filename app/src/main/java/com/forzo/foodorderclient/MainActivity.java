package com.forzo.foodorderclient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("users");

    }

    @OnClick({R.id.sign_in_btn, R.id.register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.register:

                final AlertDialog dialog = new SpotsDialog(MainActivity.this);
                dialog.show();
                final String email_text = email.getText().toString().trim();
                String password_text = password.getText().toString().trim();

                if (!TextUtils.isEmpty(email_text) && !TextUtils.isEmpty(password_text)) {
                    mAuth.createUserWithEmailAndPassword(email_text, password_text)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String user_id = mAuth.getCurrentUser().getUid();
                                        DatabaseReference databaseReference = myRef.child(user_id);
                                        databaseReference.child("Name").setValue(email_text);
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Sign In Failed, Password must be of minimum length 6",
                                                Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter all the field", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                break;
        }
    }
}
