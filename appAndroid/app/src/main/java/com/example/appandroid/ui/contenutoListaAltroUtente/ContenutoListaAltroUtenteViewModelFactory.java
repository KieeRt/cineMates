package com.example.appandroid.ui.contenutoListaAltroUtente;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class ContenutoListaAltroUtenteViewModelFactory  implements ViewModelProvider.Factory {
    int mParam;
    public ContenutoListaAltroUtenteViewModelFactory(int param) {
        mParam = param;
    }


    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new ContenutoListaAltroUtenteViewModel(mParam);
    }
}
