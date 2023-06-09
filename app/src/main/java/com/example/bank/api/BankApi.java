package com.example.bank.api;

import com.example.bank.api.models.AddClientsDto;
import com.example.bank.api.models.ClientDto;
import com.example.bank.api.models.DepositDto;
import com.example.bank.api.models.LoanProgramDto;
import com.example.bank.api.models.MailSendInfoDto;
import com.example.bank.api.models.ReplenishmentDto;
import com.example.bank.api.models.ClerkDto;
import com.example.bank.api.models.ReportClientsCurrenciesDto;
import com.example.bank.api.models.ReportDto;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BankApi {

    @GET("Clerk/Login")
    Single<ClerkDto> login(@Query("login") String login, @Query("password") String password);

    @POST("Clerk/Register")
    Single<ClerkDto> registration(@Body ClerkDto clerk);

    @POST("Clerk/UpdateData")
    Single<ClerkDto> updateClerk(@Body ClerkDto clerk);

    @GET("Clerk/GetClerkClientList")
    Single<List<ClientDto>> getClerkClientList(@Query("clerkId") int clerkId);

    @GET("Clerk/GetClerkDepositList")
    Single<List<DepositDto>> getClerkDepositList(@Query("clerkId") int clerkId);

    @GET("Clerk/GetClerkReplenishmentList")
    Single<List<ReplenishmentDto>> getClerkReplenishmentList(@Query("clerkId") int clerkId);


    @GET("Client/GetClientList")
    Single<List<ClientDto>> getClientList();

    @GET("Client/GetClient")
    Single<ClientDto> getClient(@Query("clientId") int clientId);

    @POST("Client/CreateOrUpdateClient")
    Single<ClientDto> createOrUpdateClient(@Body ClientDto client);

    @GET("Client/GetLoanProgramList")
    Single<List<LoanProgramDto>> getLoanProgramList();

    @POST("Client/DeleteClient")
    Single<ClientDto> deleteClient(@Body ClientDto client);


    @GET("Deposit/GetDepositList")
    Single<List<DepositDto>> getDepositList();

    @GET("Deposit/GetDeposit")
    Single<DepositDto> getDeposit(@Query("depositId") int depositId);

    @POST("Deposit/CreateOrUpdateDeposit")
    Single<DepositDto> createOrUpdateDeposit(@Body DepositDto deposit);

    @POST("Deposit/AddDepositClients")
    Single<AddClientsDto> addDepositClients(@Body AddClientsDto bind);

    @POST("Deposit/DeleteDeposit")
    Single<DepositDto> deleteDeposit(@Body DepositDto deposit);


    @GET("Replenishment/GetReplenishmentList")
    Single<List<ReplenishmentDto>> getReplenishmentList();

    @GET("Replenishment/GetReplenishment")
    Single<ReplenishmentDto> getReplenishment(@Query("replenishmentId") int replenishmentId);

    @POST("Replenishment/CreateOrUpdateReplenishment")
    Single<ReplenishmentDto> createOrUpdateReplenishment(@Body ReplenishmentDto replenishment);

    @POST("Replenishment/DeleteReplenishment")
    Single<ReplenishmentDto> deleteReplenishment(@Body ReplenishmentDto replenishment);


    @POST("Report/CreateReportClientCurrencyToWordFile")
    Single<ReportDto> createReportClientCurrencyToWordFile(@Body ReportDto report);

    @POST("Report/CreateReportClientCurrencyToExcelFile")
    Single<ReportDto> createReportClientCurrencyToExcelFile(@Body ReportDto report);

    @POST("Report/CreateReportClientsToPdfFile")
    Single<ReportDto> createReportClientsToPdfFile(@Body ReportDto report);

    @POST("Report/GetClientCurrency")
    Single <List<ReportClientsCurrenciesDto>> getClientCurrency(@Body ReportDto report);

    @POST("Report/SendReportOnMail")
    Single<MailSendInfoDto> sendReportOnMail(@Body MailSendInfoDto mailSendInfoDto);
}
