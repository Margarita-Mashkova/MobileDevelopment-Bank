package com.example.bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bank.api.models.ClerkDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class RegistrationActivity extends AppCompatActivity {

    CompositeDisposable disposable = new CompositeDisposable();
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextFIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFIO = findViewById(R.id.editTextFIO);

        //Регистрация
        Button btnRegistration = findViewById(R.id.buttonRegistration);
        btnRegistration.setOnClickListener(this::onClick);

        //Назад
        Button btnBack = findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(this::onClick);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegistration:
                ClerkDto clerk = makeClerk();
                if (clerk != null) {
                    registration(clerk);
                }
                break;
            case R.id.buttonBack:
                goBack();
                break;
            default:
                break;
        }
    }

    private ClerkDto makeClerk(){
        ClerkDto clerk = new ClerkDto();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        matcher = pattern.matcher(email);

        if (email.length() > 50 || !matcher.matches())
        {
            Toast.makeText(this, "В качестве логина должна быть указана почта и иметь длинну не более 50 символов", Toast.LENGTH_SHORT).show();
            return null;
        }

        pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{10,30}$");
        matcher = pattern.matcher(password);
        if(!matcher.matches())
        {
            Toast.makeText(this, "Пароль длиной от 10 до 30 должен состоять из цифр, букв и небуквенных символов", Toast.LENGTH_SHORT).show();
            return null;
        }
        clerk.setClerkFIO(editTextFIO.getText().toString());
        clerk.setEmail(email);
        clerk.setPassword(password);
        return clerk;
    }

    public void registration(ClerkDto clerk) {
        App app = (App) getApplication();
        disposable.add(app.getBankService().getApi().registration(clerk)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ClerkDto, Throwable>() {
                    @Override
                    public void accept(ClerkDto clerk, Throwable throwable) throws Exception {
                        if (throwable != null & clerk!=null) {
                            Toast.makeText(RegistrationActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Intent intent = new Intent(RegistrationActivity.this, AuthorizationActivity.class);
                            startActivity(intent);
                            Toast.makeText(RegistrationActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void goBack(){
        Intent intent = new Intent(this, AuthorizationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}