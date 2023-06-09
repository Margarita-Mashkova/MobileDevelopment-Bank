package com.example.bank.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.ClientDto;
import com.example.bank.api.models.ReportClientsCurrenciesDto;
import com.example.bank.api.models.ReportDto;
import com.example.bank.reportsLogic.CreateExcelFile;
import com.example.bank.reportsLogic.CreateWordFile;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ClientCurrencyReportFragment extends Fragment {

    ArrayAdapter<String> adapter;
    ListView listViewClients;
    ArrayList<String> listForViewCl;
    ArrayList<ClientDto> clientsFullList;
    ArrayList<ClientDto> clientsSelected;
    Button btnExcel;
    Button btnWord;
    App app;
    CompositeDisposable disposable = new CompositeDisposable();
    int docType;

    public ClientCurrencyReportFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_currency_report, null);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        listViewClients = v.findViewById(R.id.list);
        btnExcel = v.findViewById(R.id.buttonExcel);
        btnExcel.setOnClickListener(this::onClick);
        btnWord = v.findViewById(R.id.buttonWord);
        btnWord.setOnClickListener(this::onClick);

        clientsFullList = new ArrayList<>();
        listForViewCl = StaticList.arrayList;
        getClients();

        return v;
    }

    public void onClick(View view) {
        ReportDto reportDto = makeReportDto();
        if(reportDto!= null){
            getClientCurrency(reportDto);
        }
        switch (view.getId()) {
            case R.id.buttonExcel:
                docType = 1;
                break;
            case R.id.buttonWord:
                docType = 2;
                break;
            default:
                break;
        }
    }

    public ReportDto makeReportDto(){
        ReportDto reportDto = new ReportDto();
        clientsSelected = new ArrayList<>();
        for (int i = 0; i < listViewClients.getCount(); i++) {
            if (listViewClients.isItemChecked(i)) {
                listViewClients.setItemChecked(i, false);
                for (ClientDto client : clientsFullList) {
                    if (client.getClientFIO().equals(adapter.getItem(i))) {
                        clientsSelected.add(client);
                        break;
                    }
                }
            }
        }
        reportDto.setClients(clientsSelected);
        reportDto.setClerkId(app.getClerk().getId());
        if (!reportDto.getClients().isEmpty()) {
            return reportDto;
        } else {
            Toast.makeText(getActivity(), "Выберите хотя бы одного клиента", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void getClientCurrency(ReportDto reportDto){
        disposable.add(app.getBankService().getApi().getClientCurrency(reportDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<ReportClientsCurrenciesDto>, Throwable>() {
                    @Override
                    public void accept(List<ReportClientsCurrenciesDto> clientCurrencies, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            switch (docType){
                                case 1:
                                    CreateExcelFile createExcelFile = new CreateExcelFile(clientCurrencies);
                                    if(createExcelFile.saveExcelFile("ClientCurrency.xlsx")){
                                        Toast.makeText(getActivity(), "Отчёт сохранен в папке Reports", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 2:
                                    CreateWordFile createWordFile = new CreateWordFile(clientCurrencies);
                                    if(createWordFile.saveWordFile("ClientCurrency.docx")){
                                        Toast.makeText(getActivity(), "Отчёт сохранен в папке Reports", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        }
                    }
                }));
    }

    public void getClients(){
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
                            clientsFullList.addAll(clients);
                            listForViewCl.clear();
                            for (ClientDto cl:clients) {
                                listForViewCl.add(cl.getClientFIO());
                            }
                            adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, listForViewCl);
                            listViewClients.setAdapter(adapter);
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