package com.example.firebasedb_inclass;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //views
    EditText editTextArtistName;
    Spinner spinnerGenre;
    Button buttonAddArtist;

    //views
    TextView textViewArtistName;
    TextView textViewArtistGenre;
    ListView listViewArtists;

    //data objects
    List<Artist> artists;
    DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance().getReference("artists");

        //instantiate our views
        editTextArtistName = findViewById(R.id.editTextArtistName);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        buttonAddArtist = findViewById(R.id.buttonAddToDatabase);

        listViewArtists = findViewById(R.id.listViewArtists);

        artists = new ArrayList<>();

    }

    @Override
    protected void onStart(){
        super.onStart();

        //event listender for the db
        db.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear our list
                artists.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Artist artist = postSnapshot.getValue(Artist.class);
                    artists.add(artist);
                }

                //create adapter & attach it to our listview
                ArtistListAdapter myArtistAdapter =
                        new ArtistListAdapter(MainActivity.this,artists);
                listViewArtists.setAdapter(myArtistAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addArtist(View view) {
        //code that runs when button is clicked
        String name = editTextArtistName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name)){
            //runs if there is a name typed
            String id = db.push().getKey();

            Artist a = new Artist(id,name,genre);
            db.child(id).setValue(a);

            //update our UI
            editTextArtistName.setText("");
            Toast.makeText(this, "Artist Added!", Toast.LENGTH_LONG).show();
        }else{
            //else runs if the edit text IS empty
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }

    }
}
