package com.example.bank.fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bank.App;
import com.example.bank.AuthorizationActivity;
import com.example.bank.R;
import com.example.bank.api.models.ClerkDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ProfileFragment extends Fragment {

    CompositeDisposable disposable = new CompositeDisposable();
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextFIO;
    App app;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, null);
        editTextEmail = v.findViewById(R.id.editTextEmail);
        editTextPassword = v.findViewById(R.id.editTextPassword);
        editTextFIO = v.findViewById(R.id.editTextFIO);

        editTextEmail.setText(app.getClerk().getEmail());
        editTextPassword.setText(app.getClerk().getPassword());
        editTextFIO.setText(app.getClerk().getClerkFIO());

        Button btnChange = v.findViewById(R.id.buttonChange);
        btnChange.setOnClickListener(this::onClick);

        Button btnStart = v.findViewById(R.id.buttonExit);
        btnStart.setOnClickListener(this::onClick);

        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonChange:
                ClerkDto clerk = makeClerk();
                if (clerk != null) {
                    updateClerk(clerk);
                }
                break;
            case R.id.buttonExit:
                app.setClerk(null);
                clearBackStack();
                Intent intent = new Intent(getActivity(), AuthorizationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
            Toast.makeText(getActivity(), "В качестве логина должна быть указана почта и иметь длинну не более 50 символов", Toast.LENGTH_SHORT).show();
            return null;
        }

        pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{10,30}$");
        matcher = pattern.matcher(password);
        if(!matcher.matches())
        {
            Toast.makeText(getActivity(), "Пароль длиной от 10 до 30 должен состоять из цифр, букв и небуквенных символов", Toast.LENGTH_SHORT).show();
            return null;
        }

        clerk.setId(app.getClerk().getId());
        clerk.setClerkFIO(editTextFIO.getText().toString());
        clerk.setEmail(email);
        clerk.setPassword(password);
        return clerk;
    }

    public void updateClerk(ClerkDto clerkDto) {
        disposable.add(app.getBankService().getApi().updateClerk(clerkDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ClerkDto, Throwable>() {
                    @Override
                    public void accept(ClerkDto clerk, Throwable throwable) throws Exception {
                        if (throwable != null & clerk!=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            app.getClerk().setClerkFIO(clerkDto.getClerkFIO());
                            app.getClerk().setEmail(clerkDto.getEmail());
                            app.getClerk().setPassword(clerkDto.getPassword());
                            Toast.makeText(getActivity(), "Данные профиля изменены", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}