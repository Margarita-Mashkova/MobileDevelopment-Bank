package com.example.bank.fragments;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bank.R;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder>{
    private String adapterType;
    private ArrayList<Integer> ids;
    private ArrayList<String> names;
    private ArrayList<Double> depositsInterests;
    private ArrayList<String> clientPassports;
    private ArrayList<String> clientVisits;
    private ArrayList<String> clientTelephones;
    private ArrayList<Integer> replenishmentAmount;
    private ArrayList<String> replenishmentDate;
    private FragmentManager fragmentManager;
    Bundle bundle;

    //deposit
    RVAdapter(String adapterType, ArrayList<Integer> ids, ArrayList<String> names, ArrayList<Double> depositsInterests, FragmentManager fragmentManager){
        this.ids = ids;
        this.names = names;
        this.depositsInterests = depositsInterests;
        this.adapterType = adapterType;
        this.fragmentManager = fragmentManager;
    }

    //client
    RVAdapter(String adapterType, ArrayList<Integer> ids, ArrayList<String> names, ArrayList<String> clientPassports,
                                ArrayList<String> clientTelephones, ArrayList<String> clientVisits, FragmentManager fragmentManager){
        this.ids = ids;
        this.names = names;
        this.clientPassports = clientPassports;
        this.clientTelephones = clientTelephones;
        this.clientVisits = clientVisits;
        this.adapterType = adapterType;
        this.fragmentManager = fragmentManager;
    }

    //replenishment
    RVAdapter(String adapterType, ArrayList<Integer> ids, ArrayList<Integer> replenishmentAmount,
              ArrayList<String> replenishmentDate, ArrayList<String> names, FragmentManager fragmentManager){
        this.ids = ids;
        this.replenishmentAmount = replenishmentAmount;
        this.replenishmentDate = replenishmentDate;
        //deposit name
        this.names = names;
        this.adapterType = adapterType;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder;
        View v;
        switch (adapterType) {
            case "deposits":
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_card, parent, false);
                myViewHolder = new MyViewHolder(v);
                return myViewHolder;
            case "clients":
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_card, parent, false);
                myViewHolder = new MyViewHolder(v);
                return myViewHolder;
            case "replenishments":
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.replenisment_card, parent, false);
                myViewHolder = new MyViewHolder(v);
                return myViewHolder;
        }
        return null;
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.buttonEdit.setOnClickListener(x -> {
            bundle = new Bundle();
            bundle.putInt("id", ids.get(position));
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (adapterType) {
                case "clients":
                    AddClientFragment addClientFragment = new AddClientFragment();
                    addClientFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frame_layout, addClientFragment);
                    break;
                case "deposits":
                    AddDepositFragment addDepositFragment = new AddDepositFragment();
                    addDepositFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frame_layout, addDepositFragment);
                    break;
                case "replenishments":
                    AddReplenishmentFragment addReplenishmentFragment = new AddReplenishmentFragment();
                    addReplenishmentFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frame_layout, addReplenishmentFragment);
                    break;
            }
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        holder.buttonDelete.setOnClickListener(x -> {
            bundle = new Bundle();
            bundle.putInt("id", ids.get(position));
            bundle.putString("adapterType", adapterType);
            DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
            deleteDialogFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            deleteDialogFragment.show(fragmentManager, "deleteDialogFragment");
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        switch (adapterType) {
            case "deposits":
                holder.textViewDepositId.setText(ids.get(position).toString());
                holder.textViewDepositName.setText(names.get(position));
                holder.textViewDepositInterest.setText(depositsInterests.get(position).toString());
                break;
            case "clients":
                holder.textViewClientId.setText(ids.get(position).toString());
                holder.textViewClientFIO.setText(names.get(position));
                holder.textViewClientPassport.setText(clientPassports.get(position));
                holder.textViewClientTelephone.setText(clientTelephones.get(position));
                holder.textViewClientVisitDate.setText(clientVisits.get(position));
                break;
            case "replenishments":
                holder.textViewReplenishmentId.setText(ids.get(position).toString());
                holder.textViewReplenishmentAmount.setText(replenishmentAmount.get(position).toString());
                holder.textViewReplenishmentDate.setText(replenishmentDate.get(position));
                holder.textViewReplenishmentDeposit.setText(names.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView textViewDepositId;
        TextView textViewDepositName;
        TextView textViewDepositInterest;

        TextView textViewClientId;
        TextView textViewClientFIO;
        TextView textViewClientPassport;
        TextView textViewClientTelephone;
        TextView textViewClientVisitDate;

        TextView textViewReplenishmentId;
        TextView textViewReplenishmentAmount;
        TextView textViewReplenishmentDate;
        TextView textViewReplenishmentDeposit;

        Button buttonEdit;
        Button buttonDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            switch (adapterType) {
                case "deposits":
                    cv = (CardView) itemView.findViewById(R.id.cardDeposit);
                    textViewDepositId = itemView.findViewById(R.id.depositId);
                    textViewDepositName = itemView.findViewById(R.id.depositName);
                    textViewDepositInterest = itemView.findViewById(R.id.depositInterest);
                case "clients":
                    cv = (CardView) itemView.findViewById(R.id.cardClient);
                    textViewClientId = itemView.findViewById(R.id.clientId);
                    textViewClientFIO = itemView.findViewById(R.id.clientFIO);
                    textViewClientPassport = itemView.findViewById(R.id.clientPassportData);
                    textViewClientTelephone = itemView.findViewById(R.id.clientTelephone);
                    textViewClientVisitDate = itemView.findViewById(R.id.clientVisitDate);
                case "replenishments":
                    cv = (CardView) itemView.findViewById(R.id.cardReplenishment);
                    textViewReplenishmentId = itemView.findViewById(R.id.replenishmentId);
                    textViewReplenishmentAmount = itemView.findViewById(R.id.replenishmentAmount);
                    textViewReplenishmentDate = itemView.findViewById(R.id.replenishmentDate);
                    textViewReplenishmentDeposit = itemView.findViewById(R.id.replenishmentDeposit);
            }
        }
    }
}
