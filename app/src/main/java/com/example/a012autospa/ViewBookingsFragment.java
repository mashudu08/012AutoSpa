package com.example.a012autospa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewBookingsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewBookingsAdapter adapter;
    private DatabaseReference root;
    private Query root2;
    private ArrayList<BookingClass> favorites;
    private FirebaseUser user;
    private ImageView back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_view_bookings, container, false);

        recyclerView = rootView.findViewById(R.id.bookingsRecycler);
        back = rootView.findViewById(R.id.back);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        favorites = new ArrayList<>();

        // database instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseDatabase db = com.google.firebase.database.FirebaseDatabase.getInstance();
        root  = db.getReference().child(userId).child("bookings");
        FirebaseDatabase.getInstance().getReference().keepSynced(true);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //checking if setting is already under userId in firebase
                if(dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        BookingClass fav = postSnapshot.getValue(BookingClass.class);
                        favorites.add(fav);
                    }

                    adapter = new ViewBookingsAdapter(getActivity(), favorites);
                    recyclerView.setAdapter(adapter);
                }
                else {

                    Toast.makeText(getActivity(), "No bookings have been made!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new ProfileFragment()).commit();
            }
        });

        return rootView;
    }

    //comparing dates and only showing upcoming bookings based on current date
    private boolean validateDate(String date) throws ParseException {
        //https://droidbyme.medium.com/compare-date-calendar-or-string-date-c6368d57119e

        //getting current date
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(Calendar.getInstance().getTime());

        //converting strings to date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1= sdf.parse(currentDate);
        Date date2 = sdf.parse(date);

        //validating the date (only current date upwards)
        if(date2.compareTo(date1) < 0){
            return false;
        }
        else {
            return true;
        }
    }
}