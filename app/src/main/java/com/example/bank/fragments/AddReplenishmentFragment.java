package com.example.bank.fragments;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.ClientDto;
import com.example.bank.api.models.DepositDto;
import com.example.bank.api.models.LoanProgramDto;
import com.example.bank.api.models.ReplenishmentDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class AddReplenishmentFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    App app;
    ArrayList<String> listDeposits;
    EditText editTextReplenishmentAmount;
    TextView textViewCreateReplenishment;
    Spinner spinnerDeposit;
    Button btnAdd;
    List<ReplenishmentDto> replenishmentsList;
    Integer replenishmentId;
    int depositId;

    public AddReplenishmentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_replenishment, null);

        listDeposits = new ArrayList<>();
        editTextReplenishmentAmount = v.findViewById(R.id.editTextReplenishmentAmount);
        spinnerDeposit = v.findViewById(R.id.spinnerDeposit);
        textViewCreateReplenishment = v.findViewById(R.id.textViewCreateReplenishment);

        getDeposits();

        btnAdd = v.findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(this::onClick);

        Button btnBack = v.findViewById(R.id.buttonCancel);
        btnBack.setOnClickListener(this::onClick);

        if(getArguments() != null) {
            replenishmentId = getArguments().getInt("id");
            replenishmentsList = new ArrayList<>();
        }

        return  v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                ReplenishmentDto replenishment = makeReplenishment();
                if (replenishment != null) {
                    createOrUpdateReplenishment(replenishment);
                }
                break;
            case R.id.buttonCancel:
                getActivity().getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    private void makeReplenishmentChange(ReplenishmentDto replenishmentChange) {
        textViewCreateReplenishment.setText("Изменение пополнения");
        btnAdd.setText("Изменить");
        editTextReplenishmentAmount.setText(String.format("%s", replenishmentChange.getAmount()));
        for (int i = 0; i < listDeposits.size(); i++) {
            if(listDeposits.get(i).equals(replenishmentChange.getDepositName())){
                spinnerDeposit.setSelection(i);
                break;
            }
        }
    }

    private ReplenishmentDto makeReplenishment(){
        ReplenishmentDto replenishment = new ReplenishmentDto();
        if(replenishmentId != null){
            replenishment.setId(replenishmentId);
        }
        int amount;
        try{
            amount = Integer.parseInt(editTextReplenishmentAmount.getText().toString());
        }
        catch (Exception ex){
            Toast.makeText(getActivity(), "Сумма должна быть числом", Toast.LENGTH_SHORT).show();
            return null;
        }
        replenishment.setAmount(amount);
        replenishment.setClerkId(app.getClerk().getId());
        replenishment.setDepositId(depositId);
        return replenishment;
    }

    public void createOrUpdateReplenishment(ReplenishmentDto replenishment) {
        disposable.add(app.getBankService().getApi().createOrUpdateReplenishment(replenishment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ReplenishmentDto, Throwable>() {
                    @Override
                    public void accept(ReplenishmentDto replenishment, Throwable throwable) throws Exception {
                        if (throwable != null & replenishment!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                            Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void getReplenishment(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getReplenishment(replenishmentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ReplenishmentDto, Throwable>() {
                    @Override
                    public void accept(ReplenishmentDto replenishment, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            if (replenishment == null) {
                                Toast.makeText(getActivity(), "Пополнение не найден", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        } else {
                            makeReplenishmentChange(replenishment);
                        }
                    }
                }));
    }

    public void getDeposits(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getDepositList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<DepositDto>, Throwable>() {
                    @Override
                    public void accept(List<DepositDto> deposits, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            listDeposits.clear();
                            for (DepositDto dep:deposits) {
                                listDeposits.add(dep.getDepositName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listDeposits);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinnerDeposit.setAdapter(adapter);
                            spinnerDeposit.setPrompt("Вклад");
                            spinnerDeposit.setSelection(0);
                            if (replenishmentId != null)
                            {
                                getReplenishment();
                            }
                            spinnerDeposit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                                    for (DepositDto dep:deposits) {
                                        if(spinnerDeposit.getSelectedItem().toString().equals(dep.getDepositName())){
                                            depositId = dep.getId();
                                        }
                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> arg0){}
                            });
                        }
                    }
                }));
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}