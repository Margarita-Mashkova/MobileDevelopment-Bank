package com.example.bank;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.bank.databinding.ActivityMainBinding;
import com.example.bank.fragments.BindingClientsFragment;
import com.example.bank.fragments.HomeFragment;
import com.example.bank.fragments.ManualsFragment;
import com.example.bank.fragments.ProfileFragment;
import com.example.bank.fragments.ReportsFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.manuals:
                    replaceFragment(new ManualsFragment());
                    break;
                case R.id.reports:
                    replaceFragment(new ReportsFragment());
                    break;
                case R.id.bindingClients:
                    replaceFragment(new BindingClientsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}