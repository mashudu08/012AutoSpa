package com.example.a012autospa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class WashMenuFragment extends Fragment {
    ImageView back;
    TextView back2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_wash_menu, container, false);
         back = rootView.findViewById(R.id.back);
         back2 = rootView.findViewById(R.id.txtBack);

         back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                         new MenusFragment()).commit();
             }
         });

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new MenusFragment()).commit();
            }
        });


        return rootView;
    }
}