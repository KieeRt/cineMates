package com.example.appandroid.ui.elencoListeAltroUtente;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class ListeAltroUtenteViewModelFactory implements ViewModelProvider.Factory {


    String mParam;
    public ListeAltroUtenteViewModelFactory(String param) {
        mParam = param;
    }


    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new ListeAltroUtenteViewModel(mParam);
    }
}
