package com.example.appandroid.ui.ricercaUtenti;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class RicercaUtentiViewModelFactory implements ViewModelProvider.Factory {
    Application application;

    public RicercaUtentiViewModelFactory(Application application){
        this.application=application;
    }
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new RicercaUtentiViewModel(application);
    }
}
