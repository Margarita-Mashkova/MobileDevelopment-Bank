package com.example.bank.fragments;

import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.ClientDto;
import com.example.bank.api.models.DepositDto;
import com.example.bank.api.models.ReplenishmentDto;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class DeleteDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    int id;
    String adapterType;
    CompositeDisposable disposable = new CompositeDisposable();
    App app;
    DepositDto depositDto;
    ClientDto clientDto;
    ReplenishmentDto replenishmentDto;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = (App) getActivity().getApplication();
        id = getArguments().getInt("id");
        adapterType = getArguments().getString("adapterType");
        getItemToDelete();
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Предупреждение").setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.cancel, this)
                .setMessage(R.string.message_text_delete_dialog);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                switch (adapterType) {
                    case "deposits":
                        deleteDeposit(depositDto);
                        readyDelete();
                        break;
                    case "clients":
                        deleteClient(clientDto);
                        readyDelete();
                        break;
                    case "replenishments":
                        deleteReplenishment(replenishmentDto);
                        readyDelete();
                        break;
                }
                break;
            case Dialog.BUTTON_NEGATIVE:
                super.onCancel(dialog);
                break;
        }
    }

    public void getItemToDelete(){
        switch (adapterType) {
            case "deposits":
                getDeposit();
                break;
            case "clients":
                getClient();
                break;
            case "replenishments":
                getReplenishment();
                break;
        }
    }

    public void deleteClient(ClientDto client) {
        disposable.add(app.getBankService().getApi().deleteClient(client)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ClientDto, Throwable>() {
                    @Override
                    public void accept(ClientDto clientDto, Throwable throwable) throws Exception {
                        if (throwable != null & clientDto!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "Данные клиента удалены", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void deleteDeposit(DepositDto deposit) {
        disposable.add(app.getBankService().getApi().deleteDeposit(deposit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<DepositDto, Throwable>() {
                    @Override
                    public void accept(DepositDto depositDto, Throwable throwable) throws Exception {
                        if (throwable != null & depositDto!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "Данные вклада удалены", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void deleteReplenishment(ReplenishmentDto replenishment) {
        disposable.add(app.getBankService().getApi().deleteReplenishment(replenishment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ReplenishmentDto, Throwable>() {
                    @Override
                    public void accept(ReplenishmentDto replenishmentDto, Throwable throwable) throws Exception {
                        if (throwable != null & replenishmentDto!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "Данные пополнения удалены", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void getDeposit(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getDeposit(id)
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
                            depositDto = new DepositDto();
                            depositDto.setId(id);
                            depositDto.setDepositName(deposit.getDepositName());
                            depositDto.setDepositInterest(deposit.getDepositInterest());
                            depositDto.setDepositClients(deposit.getDepositClients());
                            depositDto.setClerkId(deposit.getClerkId());
                        }
                    }
                }));
    }

    public void getClient() {
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getClient(id)
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
                            clientDto = new ClientDto();
                            clientDto.setId(id);
                            clientDto.setClientFIO(client.getClientFIO());
                            clientDto.setTelephoneNumber(client.getTelephoneNumber());
                            clientDto.setPassportData(client.getPassportData());
                            clientDto.setDateVisit(client.getDateVisit());
                            clientDto.setClientLoanPrograms(client.getClientLoanPrograms());
                            clientDto.setClerkId(client.getClerkId());
                        }
                    }
                }));
    }

    public void getReplenishment(){
        App app = (App) getActivity().getApplication();
        disposable.add(app.getBankService().getApi().getReplenishment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ReplenishmentDto, Throwable>() {
                    @Override
                    public void accept(ReplenishmentDto replenishment, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            if (replenishment == null) {
                                Toast.makeText(getActivity(), "Пополнение не найдено", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        } else {
                            replenishmentDto = new ReplenishmentDto();
                            replenishmentDto.setId(id);
                            replenishmentDto.setAmount(replenishment.getAmount());
                            replenishmentDto.setDateReplenishment(replenishment.getDateReplenishment());
                            replenishmentDto.setDepositId(replenishment.getDepositId());
                            replenishmentDto.setDepositName(replenishment.getDepositName());
                            replenishmentDto.setClerkId(replenishment.getClerkId());
                        }
                    }
                }));
    }

    public void readyDelete() {
        switch (adapterType) {
            case "deposits":
                replaceFragment(new DepositsFragment());
                break;
            case "clients":
                replaceFragment(new ClientsFragment());
                break;
            case "replenishments":
                replaceFragment(new ReplenishmentsFragment());
                break;
        }
    }

    private void replaceFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
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
