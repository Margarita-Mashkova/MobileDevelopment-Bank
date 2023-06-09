package com.example.bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bank.api.models.ClerkDto;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class AuthorizationActivity extends AppCompatActivity {

    CompositeDisposable disposable = new CompositeDisposable();
    EditText etLogin;
    EditText etPassword;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        app = (App) getApplication();
        etLogin = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);

        //Войти
        Button btnLogin = findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(this::onClick);

        //Регистрация
        Button btnRegistration = findViewById(R.id.buttonRegistration);
        btnRegistration.setOnClickListener(this::onClick);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                login(etLogin.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.buttonRegistration:
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void loginSuccess(){
        if(app.getClerk() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void login(String login, String password) {
        disposable.add(app.getBankService().getApi().login(login, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ClerkDto, Throwable>() {
                    @Override
                    public void accept(ClerkDto clerk, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            if (clerk == null) {
                                Toast.makeText(AuthorizationActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AuthorizationActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        } else {
                            app.setClerk(clerk);
                            loginSuccess();
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