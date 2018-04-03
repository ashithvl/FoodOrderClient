package com.forzo.foodorderclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myRef = FirebaseDatabase.getInstance().getReference().child("Item");
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MenuActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };

        Query query = myRef.limitToLast(50);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.setName(model.getName());
                holder.setDesc(model.getDesc());
                holder.setPrice(model.getPrice());
                holder.setImage(model.getImage());
                final String key = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleIntent = new Intent(MenuActivity.this, SingleFoodActivity.class);
                        singleIntent.putExtra("id", key);
                        startActivity(singleIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            public void onError(DatabaseError e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        mAuth.addAuthStateListener(mAuthStateListener);
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
        firebaseRecyclerAdapter.stopListening();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        View mView;

        FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setName(String name) {
            TextView nameTextView = mView.findViewById(R.id.item_name);
            nameTextView.setText(name);
        }

        void setDesc(String desc) {
            TextView descTextView = mView.findViewById(R.id.item_desc);
            descTextView.setText(desc);
        }

        void setPrice(String price) {
            TextView priceTextView = mView.findViewById(R.id.item_price);
            priceTextView.setText("$ " + price);
        }

        void setImage(String image) {
            ImageView imageView = mView.findViewById(R.id.item_image);
            Picasso.get()
                    .load(image)
                    .into(imageView);
        }

    }

}
