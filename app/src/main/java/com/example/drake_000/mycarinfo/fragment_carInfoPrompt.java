package com.example.drake_000.mycarinfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by drake_000 on 12/2/2017.
 */

public class fragment_carInfoPrompt extends Fragment {
    TextView vinField;

    public fragment_carInfoPrompt(){
        //handler = new Handler();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_carinfoprompt, container, false);

        rootView.findViewById(R.id.edit_vin_number).setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View view) {
                EditText vin = (EditText) rootView.findViewById(R.id.edit_vin_number);
                vin.setText("");
            }
        });

        rootView.findViewById(R.id.submit_vin_number).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText vin = (EditText) rootView.findViewById(R.id.edit_vin_number);
            String vinText = vin.getText().toString();
            Fragment fragment = new fragment_carInfo();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putString("vin", vinText);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, "carInfoTag");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    });

        return rootView;
    }

}
