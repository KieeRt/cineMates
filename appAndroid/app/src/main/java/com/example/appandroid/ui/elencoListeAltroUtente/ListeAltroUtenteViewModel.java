package com.example.appandroid.ui.elencoListeAltroUtente;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ListeAltroUtenteViewModel extends ViewModel {
    private final String emailAltroUtente ;
    private MutableLiveData<List<ListaPersonalizzata>> elencoListeAltroUtente;
    private RepositoryService repository;

    public ListeAltroUtenteViewModel(String email) {
        emailAltroUtente = email;
    }

    public MutableLiveData<List<ListaPersonalizzata>> getElencoListeAltroUtente(){
        if(elencoListeAltroUtente==null)
            return new MutableLiveData<>(new ArrayList<>());

        return elencoListeAltroUtente;
    }

    public void init(){
        if(elencoListeAltroUtente!=null){
            return;
        }

        repository= RepositoryFactory.getRepositoryConcrete();
        elencoListeAltroUtente=new MutableLiveData<>(new ArrayList<>());

    }

    public void recuperaListe() throws TimeoutException, JSONException {
        List<ListaPersonalizzata> elencoListe = elencoListeAltroUtente.getValue();
        List<ListaPersonalizzata> elencoListeCercate = repository.getListe(emailAltroUtente);
        elencoListe.clear();
        elencoListe.addAll(elencoListeCercate);
        elencoListeAltroUtente.postValue(elencoListe);
    }

}