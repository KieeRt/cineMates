package com.example.appandroid.ui.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.notifiche.NotificheService;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.jetbrains.annotations.NotNull;

public class MainActivityViewModel extends AndroidViewModel {

    RepositoryService repository = RepositoryFactory.getRepositoryConcrete();

    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void init(){
        //new NotificheService().initProviderNotifiche(getApplication().getApplicationContext());
        NotificheService.initProviderNotifiche(getApplication().getApplicationContext());
    }

    public Utente getUserLocale(){
        return repository.getUser();
    }

    public void logout(Context context){
        repository.logout(context);
    }
}
