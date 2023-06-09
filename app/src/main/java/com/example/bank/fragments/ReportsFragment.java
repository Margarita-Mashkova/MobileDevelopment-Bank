package com.example.bank.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.example.bank.R;

public class ReportsFragment extends Fragment {

    public ReportsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reports, null);

        CardView currenciesCard = v.findViewById(R.id.cardCurrencyClientsReport);
        currenciesCard.setOnClickListener(this::onClick);

        CardView clientsCard = v.findViewById(R.id.cardClientsReport);
        clientsCard.setOnClickListener(this::onClick);
        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardCurrencyClientsReport:
                replaceFragment(new ClientCurrencyReportFragment());
                break;
            case R.id.cardClientsReport:
                replaceFragment(new ClientsReportFragment());
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