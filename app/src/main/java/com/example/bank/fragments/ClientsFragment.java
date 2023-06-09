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
import com.example.bank.api.models.ClientDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ClientsFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    int idDelete;
    RecyclerView rv;
    ArrayList<String> names;
    ArrayList<Integer> ids;
    ArrayList<String> clientPassports;
    ArrayList<String> clientTelephones;
    ArrayList<String> clientVisits;

    public ClientsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clients, null);

        rv = v.findViewById(R.id.rv);

        names = new ArrayList<>();
        ids = new ArrayList<>();
        clientPassports = new ArrayList<>();
        clientTelephones = new ArrayList<>();
        clientVisits = new ArrayList<>();

        if(getArguments() != null) {
            idDelete = getArguments().getInt("id");
        }

        getClients();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        rv.setLayoutManager(llm);

        Button btnAdd = v.findViewById(R.id.buttonAddClient);
        btnAdd.setOnClickListener(this::onClick);

        return v;
    }

    public void getClients(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getClerkClientList(app.getClerk().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<ClientDto>, Throwable>() {
                    @Override
                    public void accept(List<ClientDto> clients, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            for (ClientDto client:clients) {
                                if(client.getId()!=idDelete) {
                                    ids.add(client.getId());
                                    names.add(client.getClientFIO());
                                    clientPassports.add(client.getPassportData());
                                    clientTelephones.add(client.getTelephoneNumber());
                                    clientVisits.add(client.getDateVisit());
                                }
                            }
                            RVAdapter adapter = new RVAdapter("clients", ids, names, clientPassports, clientTelephones,
                                    clientVisits, getFragmentManager());
                            rv.setAdapter(adapter);
                            if(clients.isEmpty()){
                                Toast.makeText(getActivity(), "Вы еще не создали ни одного клиента", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddClient:
                replaceFragment(new AddClientFragment());
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