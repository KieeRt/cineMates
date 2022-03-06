package com.example.appandroid.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {

    Application application;
    public MainActivityViewModelFactory(Application application){
        this.application = application;
    }
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(application);
    }
}
