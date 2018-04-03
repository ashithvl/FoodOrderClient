package com.forzo.foodorderclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleFoodActivity extends AppCompatActivity {

    @BindView(R.id.item_image)
    ImageView itemImage;
    @BindView(R.id.item_name)
    TextView itemName;
    @BindView(R.id.item_desc)
    TextView itemDesc;
    @BindView(R.id.item_price)
    TextView itemPrice;
    @BindView(R.id.item_count)
    EditText itemCount;

    private String key = null;
    private DatabaseReference mDatabase, userDatabaseReference, orderDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private String name;
    private String desc;
    private String price;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_food);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Item");
        key = getIntent().getExtras().getString("id");

        currentFirebaseUser = mFirebaseAuth.getCurrentUser();
        userDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(currentFirebaseUser.getUid());
        orderDatabaseReference = FirebaseDatabase.getInstance().getReference().child("order");

        mDatabase.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("name").getValue();
                desc = (String) dataSnapshot.child("desc").getValue();
                price = (String) dataSnapshot.child("price").getValue();
                image = (String) dataSnapshot.child("image").getValue();
                itemName.setText(name);
                itemDesc.setText(desc);
                itemPrice.setText("Price: $" + price);
                Picasso.get()
                        .load(image)
                        .into(itemImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick(R.id.order)
    public void onViewClicked() {
        final DatabaseReference reference = orderDatabaseReference.push();
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reference.child("itemname").setValue(name);
                reference.child("username").setValue(dataSnapshot.child("Name").getValue());
                reference.child("count").setValue(itemCount.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(SingleFoodActivity.this, MenuActivity.class));
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
