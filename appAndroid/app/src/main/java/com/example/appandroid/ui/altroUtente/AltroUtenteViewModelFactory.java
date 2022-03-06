package com.example.appandroid.ui.altroUtente;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class AltroUtenteViewModelFactory implements ViewModelProvider.Factory{

    String emailUtente;
    Application application;
    public AltroUtenteViewModelFactory(String emailUtente, Application application) {
        this.emailUtente = emailUtente;
        this.application = application;
    }


    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AltroUtenteViewModel(emailUtente,application);
    }


}
