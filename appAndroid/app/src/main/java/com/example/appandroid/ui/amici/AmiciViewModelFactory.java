package com.example.appandroid.ui.amici;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class AmiciViewModelFactory implements ViewModelProvider.Factory {


    Application application;

    public AmiciViewModelFactory(Application application){
        this.application = application;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AmiciViewModel(application);
    }
}
