package com.example.bank.fragments;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.AddClientsDto;
import com.example.bank.api.models.ClientDto;
import com.example.bank.api.models.DepositDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class BindingClientsFragment extends Fragment {
    Spinner spinnerDeposit;
    CompositeDisposable disposable = new CompositeDisposable();
    int depositId;
    ArrayList<String> listDeposits;
    ListView lvClients;
    ArrayList<String> listClients;
    ArrayList<ClientDto> listClientsDto;
    ArrayAdapter<String> adapterClients;
    App app;

    public BindingClientsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_binding_clients, null);

        Button btnBind = v.findViewById(R.id.buttonBind);
        btnBind.setOnClickListener(this::onClick);

        spinnerDeposit = v.findViewById(R.id.spinnerDeposit);
        lvClients = v.findViewById(R.id.clientsList);
        listClientsDto = new ArrayList<>();
        listDeposits = new ArrayList<>();
        getDeposits();
        getClients();

        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonBind:
                AddClientsDto bindings = makeBind();
                addDepositClients(bindings);
                break;
            default:
                break;
        }
    }

    private AddClientsDto makeBind(){
        AddClientsDto bind = new AddClientsDto();
        bind.setDepositId(depositId);
        List<Integer> clientsId = new ArrayList<>();
        for (int i = 0; i < lvClients.getCount(); i++) {
            if (lvClients.isItemChecked(i)) {
                lvClients.setItemChecked(i, false);
                for (ClientDto client:listClientsDto) {
                    if(adapterClients.getItem(i).equals(client.getClientFIO())){
                        clientsId.add(client.getId());
                    }
                }
            }
        }
        bind.setClientsId(clientsId);
        return bind;
    }

    public void addDepositClients(AddClientsDto bind) {
        disposable.add(app.getBankService().getApi().addDepositClients(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AddClientsDto, Throwable>() {
                    @Override
                    public void accept(AddClientsDto bind, Throwable throwable) throws Exception {
                        if (throwable != null & bind!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "Клиенты привязаны", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void getClients(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getClientList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<ClientDto>, Throwable>() {
                    @Override
                    public void accept(List<ClientDto> clients, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            listClients = StaticList.arrayList;
                            listClients.clear();
                            for (ClientDto cl:clients) {
                                listClients.add(cl.getClientFIO());
                                listClientsDto.add(cl);
                            }
                            adapterClients = new ArrayAdapter<>(getActivity(), R.layout.list_item, listClients);
                            lvClients.setAdapter(adapterClients);
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
                            spinnerDeposit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                                    //Toast.makeText(getActivity().getBaseContext(), "Вклад: " + spinnerDeposit.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                                    for (DepositDto dep:deposits) {
                                        if(spinnerDeposit.getSelectedItem().toString().equals(dep.getDepositName())){
                                            depositId = dep.getId();
                                        }
                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> arg0){

                                }
                            });
                        }
                    }
                }));
    }
}