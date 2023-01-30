package com.example.a012autospa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class MenusFragment extends Fragment {
    CardView wash, valet;
    ImageView back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_menus, container, false);
        wash = rootView.findViewById(R.id.washCard);
        valet = rootView.findViewById(R.id.valetCard);
        back = rootView.findViewById(R.id.homeBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new HomeFragment()).commit();
            }
        });

         wash.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                         new WashMenuFragment()).commit();
             }
         });

        return rootView;
    }
}