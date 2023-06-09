package com.example.bank.fragments;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.DepositDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class AddDepositFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    EditText editTextDepositName;
    EditText editTextDepositInterest;
    TextView textViewCreateDeposit;
    TextView textViewClients;
    Integer depositId;
    Button btnAdd;
    List<DepositDto> depositsList;
    List<String> clientsList;
    ArrayAdapter<String> adapterClients;
    ListView lvClients;
    App app;

    public AddDepositFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_deposit, null);

        editTextDepositName = v.findViewById(R.id.editTextDepositName);
        editTextDepositInterest = v.findViewById(R.id.editTextDepositInterest);
        textViewCreateDeposit = v.findViewById(R.id.textViewCreateDeposit);
        textViewClients = v.findViewById(R.id.textViewClients);
        textViewClients.setVisibility(View.GONE);
        lvClients = v.findViewById(R.id.listClients);
        lvClients.setVisibility(View.GONE);

        btnAdd = v.findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(this::onClick);

        Button btnBack = v.findViewById(R.id.buttonCancel);
        btnBack.setOnClickListener(this::onClick);

        if(getArguments() != null) {
            depositId = getArguments().getInt("id");
            depositsList = new ArrayList<>();
            getDeposit();
        }

        return  v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                DepositDto deposit = makeDeposit();
                if (deposit != null) {
                    createOrUpdateDeposit(deposit);
                }
                break;
            case R.id.buttonCancel:
                getActivity().getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    private void makeDepositChange(DepositDto depositChange){
        textViewCreateDeposit.setText("Изменение вклада");
        btnAdd.setText("Изменить");
        editTextDepositName.setText(depositChange.getDepositName());
        editTextDepositInterest.setText(String.format("%s", depositChange.getDepositInterest()));
        clientsList = new ArrayList<>();
        for (Map.Entry<Integer, String> cl:depositChange.getDepositClients().entrySet()) {
            clientsList.add(cl.getValue());
        }
        adapterClients = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, clientsList);
        textViewClients.setVisibility(View.VISIBLE);
        lvClients.setAdapter(adapterClients);
        lvClients.setVisibility(View.VISIBLE);
    }

    private DepositDto makeDeposit(){
        DepositDto deposit = new DepositDto();
        String depositName = editTextDepositName.getText().toString();
        if(depositName.equals("")){
            Toast.makeText(getActivity(), "Заполните наименование вклада", Toast.LENGTH_SHORT).show();
            return null;
        }
        deposit.setDepositName(depositName);
        if(depositId != null){
            deposit.setId(depositId);
        }
        double interest;
        try{
            interest = Double.parseDouble(editTextDepositInterest.getText().toString());
        }
        catch (Exception ex){
            Toast.makeText(getActivity(), "Процентная ставка должна быть числом", Toast.LENGTH_SHORT).show();
            return null;
        }
        deposit.setDepositInterest(interest);
        Map<Integer, String> clients = new HashMap<>();
        deposit.setDepositClients(clients);
        deposit.setClerkId(app.getClerk().getId());
        return deposit;
    }

    public void createOrUpdateDeposit(DepositDto deposit) {
        disposable.add(app.getBankService().getApi().createOrUpdateDeposit(deposit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<DepositDto, Throwable>() {
                    @Override
                    public void accept(DepositDto deposit, Throwable throwable) throws Exception {
                        if (throwable != null & deposit!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                            Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void getDeposit(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getDeposit(depositId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<DepositDto, Throwable>() {
                    @Override
                    public void accept(DepositDto deposit, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            if (deposit == null) {
                                Toast.makeText(getActivity(), "Вклад не найден", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        } else {
                            makeDepositChange(deposit);
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