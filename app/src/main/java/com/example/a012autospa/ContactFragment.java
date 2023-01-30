package com.example.a012autospa;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {
    private Button directions;
    private ImageView instagram;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_contact, container, false);
        directions = rootView.findViewById(R.id.btnDirections);
        instagram = rootView.findViewById(R.id.instagram);
        String address = "331 Theron St, Erasmia";

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens google maps with the car wash address
                //https://stackoverflow.com/questions/22704451/open-google-maps-through-intent-for-specific-location-in-android
                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening 012 AutoSpa instagram page via google browser
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.instagram.com/012_autospa/"));
                startActivity(intent);
            }
        });

        return rootView;
    }
}