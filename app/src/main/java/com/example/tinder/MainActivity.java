package com.example.tinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cards cardsData[];
    private ArrayAdapter arrayAdapter;

    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;

    ListView listView;
    List<Cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb
                        .child(userId)
                        .child("connections")
                        .child("nope")
                        .child(currentUId)
                        .setValue(true);
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb
                        .child(userId)
                        .child("connections")
                        .child("yeps")
                        .child(currentUId)
                        .setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb
                .child(currentUId)
                .child("connections")
                .child("yeps")
                .child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();
                    usersDb
                            .child(snapshot.getKey())
                            .child("connections")
                            .child("matches")
                            .child(currentUId)
                            .setValue(true);
                    usersDb
                            .child(currentUId)
                            .child("connections")
                            .child("matches")
                            .child(snapshot.getKey())
                            .setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String userSex;
    private String oppositeUserSex;

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())) {
                    if (snapshot.exists()) {
                        if (snapshot.child("sex") != null) {
                            userSex = snapshot.child("sex").getValue().toString();
                            oppositeUserSex = "Female";

                            switch (userSex) {
                                case "Male":
                                    oppositeUserSex = "Female";
                                    break;
                                case "Female":
                                    oppositeUserSex = "Male";
                                    break;
                            }
                            getOppositeSexUsers();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    public void getOppositeSexUsers() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() &&
                        !snapshot.child("connections").child("nope").hasChild(currentUId) &&
                        !snapshot.child("connections").child("yeps").hasChild(currentUId) &&
                        snapshot.child("connections").child("sex").getValue().toString().equals(oppositeUserSex)) {
                    String profileImageUrl = "default";
                    if (snapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                    }
                    Cards item = new Cards(snapshot.getKey(),
                            snapshot.child("name").getValue().toString(),
                            profileImageUrl);
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLogOrRegActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }
}