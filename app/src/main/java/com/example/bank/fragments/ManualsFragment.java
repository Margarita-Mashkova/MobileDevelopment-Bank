package com.example.bank.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.example.bank.R;

public class ManualsFragment extends Fragment {

    public ManualsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manuals, null);

        CardView cardClients = v.findViewById(R.id.cardClients);
        cardClients.setOnClickListener(this::onClick);

        CardView cardDeposits = v.findViewById(R.id.cardDeposits);
        cardDeposits.setOnClickListener(this::onClick);

        CardView cardReplenishments = v.findViewById(R.id.cardReplenishments);
        cardReplenishments.setOnClickListener(this::onClick);

        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardClients:
                replaceFragment(new ClientsFragment());
                break;
            case R.id.cardDeposits:
                replaceFragment(new DepositsFragment());
                break;
            case R.id.cardReplenishments:
                replaceFragment(new ReplenishmentsFragment());
                break;
            default:
                break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}