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
import com.example.bank.api.models.ClientDto;
import com.example.bank.api.models.LoanProgramDto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;


public class AddClientFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    EditText editTextClientFIO;
    EditText editTextPassport;
    EditText editTextTelephone;
    ListView listLoanPrograms;
    TextView textViewCreateClient;
    Button btnAdd;
    Map<Integer, String> clientLoanPrograms = new HashMap<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    Integer clientId;
    App app;

    public AddClientFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_client, null);

        editTextClientFIO = v.findViewById(R.id.editTextClientFIO);
        editTextPassport = v.findViewById(R.id.editTextPassport);
        editTextTelephone = v.findViewById(R.id.editTextTelephone);
        listLoanPrograms = v.findViewById(R.id.listLoanPrograms);
        textViewCreateClient = v.findViewById(R.id.textViewCreateClient);
        getLoanProgramList();

        btnAdd = v.findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(this::onClick);

        Button btnBack = v.findViewById(R.id.buttonCancel);
        btnBack.setOnClickListener(this::onClick);

        if(getArguments() != null) {
            clientId = getArguments().getInt("id");
        }

        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                ClientDto client = makeClient();
                if (client != null) {
                    createOrUpdateClient(client);
                } else {
                    Toast.makeText(getActivity(), "Заполните все поля. Чтобы заполнить телефон, прокрутите верхнюю часть.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonCancel:
                getActivity().getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    private void makeClientChange(ClientDto cl){
        textViewCreateClient.setText("Изменение клиента");
        btnAdd.setText("Изменить");
        editTextClientFIO.setText(cl.getClientFIO());
        editTextPassport.setText(cl.getPassportData());
        editTextTelephone.setText(cl.getTelephoneNumber());
        for (Map.Entry<Integer, String> lp : cl.getClientLoanPrograms().entrySet()) {
            for (int i = 0; i < listLoanPrograms.getCount(); i++) {
                if (adapter.getItem(i).equals(lp.getValue())) {
                    listLoanPrograms.setItemChecked(i, true);
                    break;
                }
            }
        }
    }

    private ClientDto makeClient() {
        String fio = editTextClientFIO.getText().toString();
        String passport = editTextPassport.getText().toString();
        String telephone = editTextTelephone.getText().toString();
        ClientDto client = new ClientDto();
        if (clientId != null) {
            client.setId(clientId);
        }
        client.setClientFIO(fio);
        client.setPassportData(passport);
        client.setTelephoneNumber(telephone);
        client.setClerkId(app.getClerk().getId());
        Map<Integer, String> loanPrograms = new HashMap<>();
        for (int i = 0; i < listLoanPrograms.getCount(); i++) {
            if (listLoanPrograms.isItemChecked(i)) {
                //listLoanPrograms.setItemChecked(i, false);
                for (Map.Entry<Integer, String> lp : clientLoanPrograms.entrySet()) {
                    if (adapter.getItem(i).equals(lp.getValue())) {
                        loanPrograms.put(lp.getKey(), adapter.getItem(i));
                    }
                }
            }
        }
        client.setClientLoanPrograms(loanPrograms);
        //&& !loanPrograms.isEmpty()
        if (!fio.equals("") && !passport.equals("") && !telephone.equals("")) {
            return client;
        }else {
            return null;
        }
    }

    public void createOrUpdateClient(ClientDto client) {
        disposable.add(app.getBankService().getApi().createOrUpdateClient(client)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ClientDto, Throwable>() {
                    @Override
                    public void accept(ClientDto client, Throwable throwable) throws Exception {
                        if (throwable != null & client!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            getActivity().getFragmentManager().popBackStack();
                            Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void getLoanProgramList() {
        disposable.add(app.getBankService().getApi().getLoanProgramList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<LoanProgramDto>, Throwable>() {
                    @Override
                    public void accept(List<LoanProgramDto> loanPrograms, Throwable throwable) throws Exception {
                        if (throwable != null ) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            list = StaticList.arrayList;
                            list.clear();
                            for (LoanProgramDto lp:loanPrograms) {
                                list.add(lp.getLoanProgramName());
                                clientLoanPrograms.put(lp.getId(), lp.getLoanProgramName());
                            }
                            adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, list);
                            listLoanPrograms.setAdapter(adapter);
                            if(clientId != null){
                                getClient();
                            }
                        }
                    }
                }));
    }

    public void getClient() {
        disposable.add(app.getBankService().getApi().getClient(clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ClientDto, Throwable>() {
                    @Override
                    public void accept(ClientDto client, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            if (client == null) {
                                Toast.makeText(getActivity(), "Клиент не найден", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        } else {
                            makeClientChange(client);
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

//        ClientDto clientChange = new ClientDto();
//        clientChange.setId(clientId);
//        clientChange.setClientFIO(cl.getClientFIO());
//        clientChange.setPassportData(cl.getPassportData());
//        clientChange.setTelephoneNumber(cl.getTelephoneNumber());
//        clientChange.setDateVisit(cl.getDateVisit());
//        clientChange.setClerkId(app.getClerk().getId());
//        clientChange.setClientLoanPrograms(cl.getClientLoanPrograms());
