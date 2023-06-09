package com.example.bank.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.app.Fragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bank.App;
import com.example.bank.R;
import com.example.bank.api.models.MailSendInfoDto;
import com.example.bank.api.models.ReportDto;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ClientsReportFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    EditText editTextDateFrom;
    EditText editTextDateTo;
    Button buttonSendMail;
    Date dateTo;
    Date dateFrom;
    int dateWhich;
    App app;
    Calendar dateAndTime=Calendar.getInstance();
    private final String PATH = "F:\\MobileReports\\ReportClientsPdf.pdf";

    public ClientsReportFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateWhich = 0;
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clients_report, null);
        editTextDateFrom = v.findViewById(R.id.editTextDateFrom);
        editTextDateFrom.setFocusable(false);
        editTextDateFrom.setCursorVisible(false);
        editTextDateFrom.setOnClickListener(this::onClick);
        editTextDateTo = v.findViewById(R.id.editTextDateTo);
        editTextDateTo.setFocusable(false);
        editTextDateTo.setCursorVisible(false);
        editTextDateTo.setOnClickListener(this::onClick);
        buttonSendMail = v.findViewById(R.id.buttonSendMail);
        buttonSendMail.setOnClickListener(this::onClick);
        setInitialDateTime();
        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSendMail:
//                dateFrom = editTextDateFrom.getText().toString();
//                dateTo = editTextDateTo.getText().toString();
                ReportDto reportDto = makeReportDto();
                if (reportDto != null) {
                    createReportClientsToPdfFile(reportDto);
                }
                break;
            case R.id.editTextDateFrom:
                dateWhich = 1;
                setDate(view);
                break;
            case R.id.editTextDateTo:
                dateWhich = 2;
                setDate(view);
                break;
            default:
                break;
        }
    }

    public void setDate(View v) {
        new DatePickerDialog(getActivity(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {
        switch (dateWhich){
//            case 0:
//                editTextDateFrom.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(),
//                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
//                editTextDateTo.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(),
//                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
//                break;
            case 1:
                dateFrom = dateAndTime.getTime();
                editTextDateFrom.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                break;
            case 2:
                dateTo = dateAndTime.getTime();
                editTextDateTo.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                break;
        }
    }

    private ReportDto makeReportDto() {
        ReportDto reportDto = new ReportDto();
        reportDto.setFileName(PATH);
//
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        Date startDate = new Date();
//        Date endDate = new Date();
//        try {
//            startDate = df.parse(dateFrom);
//            endDate = df.parse(dateTo);
//            System.out.println(startDate);
//            System.out.println(endDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        reportDto.setDateFrom(dateFrom);
        reportDto.setDateTo(dateTo);
        reportDto.setClerkId(app.getClerk().getId());
        if (reportDto.getDateFrom() != null && reportDto.getDateTo() != null && dateTo.after(dateFrom)) {
            return reportDto;
        } else {
            Toast.makeText(getActivity(), "Дата начала периода не может быть больше даты конца периода", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void createReportClientsToPdfFile(ReportDto reportDto) {
        disposable.add(app.getBankService().getApi().createReportClientsToPdfFile(reportDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ReportDto, Throwable>() {
                    @Override
                    public void accept(ReportDto report, Throwable throwable) throws Exception {
                        if (throwable != null & report!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            String dateFromStr = DateUtils.formatDateTime(getActivity(), dateFrom.getTime(),
                                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                            String dateToStr = DateUtils.formatDateTime(getActivity(), dateTo.getTime(),
                                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                            MailSendInfoDto mailSendInfoDto = new MailSendInfoDto();
                            mailSendInfoDto.setFileName(PATH);
                            mailSendInfoDto.setMailAddress(app.getClerk().getEmail());
                            mailSendInfoDto.setSubject("Отчет по клиентам. Банк \"Вы банкрот\"");
                            mailSendInfoDto.setText(String.format("Отчёт по клиентам с %s по %s" +
                                    "\nРаботник - %s", dateFromStr, dateToStr, app.getClerk().getClerkFIO()));
                            sendReportOnMail(mailSendInfoDto);
                        }
                    }
                }));
    }

    public void sendReportOnMail(MailSendInfoDto mailSendInfoDto) {
        disposable.add(app.getBankService().getApi().sendReportOnMail(mailSendInfoDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<MailSendInfoDto, Throwable>() {
                    @Override
                    public void accept(MailSendInfoDto mailInfo, Throwable throwable) throws Exception {
                        if (throwable != null & mailInfo!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), String.format("Отчёт успешно отправлен на почту %s", app.getClerk().getEmail()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }
}

//    public void getClientsReport(){
//        disposable.add(app.getBankService().getApi().getClientsReport(dateFrom, dateTo)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BiConsumer<ReportClientsCurrenciesDto, Throwable>() {
//                    @Override
//                    public void accept(ReportClientsCurrenciesDto clientsList, Throwable throwable) throws Exception {
//                        if (throwable != null) {
//                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
//                            System.out.println(throwable.getMessage());
//                        } else {
//
//                        }
//                    }
//                }));
//    }