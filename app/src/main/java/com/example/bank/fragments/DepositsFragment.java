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
import com.example.bank.api.models.DepositDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class DepositsFragment extends Fragment {

    RecyclerView rv;
    ArrayList<Integer> ids;
    ArrayList<String> names;
    ArrayList<Double> depositsInterests;
    int idDelete;
    CompositeDisposable disposable = new CompositeDisposable();

    public DepositsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deposits, null);

        rv = v.findViewById(R.id.rv);
        ids = new ArrayList<>();
        names = new ArrayList<>();
        depositsInterests = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        rv.setLayoutManager(llm);
        if(getArguments() != null) {
            idDelete = getArguments().getInt("id");
        }
        getDeposits();
        Button btnAdd = v.findViewById(R.id.buttonAddDeposit);
        btnAdd.setOnClickListener(this::onClick);

        return v;
    }

    public void getDeposits(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getClerkDepositList(app.getClerk().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<DepositDto>, Throwable>() {
                    @Override
                    public void accept(List<DepositDto> deposits, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            for (DepositDto deposit:deposits) {
                                if(deposit.getId()!=idDelete) {
                                    ids.add(deposit.getId());
                                    names.add(deposit.getDepositName());
                                    depositsInterests.add(deposit.getDepositInterest());
                                }
                            }
                            RVAdapter adapter = new RVAdapter("deposits", ids, names, depositsInterests, getFragmentManager());
                            rv.setAdapter(adapter);
                            if(deposits.isEmpty()){
                                Toast.makeText(getActivity(), "Вы еще не создали ни одного вклада", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddDeposit:
                replaceFragment(new AddDepositFragment());
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