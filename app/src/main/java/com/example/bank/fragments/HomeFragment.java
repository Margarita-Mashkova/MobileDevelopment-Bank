package com.example.bank.fragments;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bank.App;
import com.example.bank.R;

public class HomeFragment extends Fragment {

    TextView textViewFio;
    App app;

    public HomeFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        textViewFio = v.findViewById(R.id.textViewFio);
        textViewFio.setText(String.format("%s!", app.getClerk().getClerkFIO()));
        return v;
    }

}