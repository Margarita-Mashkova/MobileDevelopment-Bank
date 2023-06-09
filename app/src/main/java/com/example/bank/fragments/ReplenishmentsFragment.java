package com.example.bank.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.ReplenishmentDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ReplenishmentsFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    int idDelete;
    ArrayList<Integer> ids;
    ArrayList<Integer> replenishmentAmount;
    ArrayList<String> replenishmentDate;
    ArrayList<String> depositNames;
    RecyclerView rv;

    public ReplenishmentsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_replenishments, null);

        rv = v.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        rv.setLayoutManager(llm);

        ids = new ArrayList<>();
        replenishmentAmount = new ArrayList<>();
        replenishmentDate = new ArrayList<>();
        depositNames = new ArrayList<>();
        if(getArguments() != null) {
            idDelete = getArguments().getInt("id");
        }

        getReplenishments();

        Button btnAdd = v.findViewById(R.id.buttonAddReplenishment);
        btnAdd.setOnClickListener(this::onClick);

        return v;
    }

    public void getReplenishments(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getClerkReplenishmentList(app.getClerk().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<ReplenishmentDto>, Throwable>() {
                    @Override
                    public void accept(List<ReplenishmentDto> replenishments, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            for (ReplenishmentDto replenishment:replenishments) {
                                if(replenishment.getId()!=idDelete) {
                                    ids.add(replenishment.getId());
                                    replenishmentAmount.add(replenishment.getAmount());
                                    replenishmentDate.add(replenishment.getDateReplenishment());
                                    depositNames.add(replenishment.getDepositName());
                                }
                            }
                            RVAdapter adapter = new RVAdapter("replenishments", ids, replenishmentAmount,
                                    replenishmentDate, depositNames, getFragmentManager());
                            rv.setAdapter(adapter);
                            if(replenishments.isEmpty()){
                                Toast.makeText(getActivity(), "Вы еще не создали ни одного пополнения", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddReplenishment:
                replaceFragment(new AddReplenishmentFragment());
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

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}