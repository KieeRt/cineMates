package com.example.appandroid.ui.richiesteAmicizia;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RichiesteAmiciziaViewModel extends AndroidViewModel {

    private MutableLiveData<List<Utente>> utenti_richieste;
    private RepositoryService repository;

    public RichiesteAmiciziaViewModel(Application application) {
        super(application);
    }

    public void init(){
        if(utenti_richieste!=null)
            return;
        repository= RepositoryFactory.getRepositoryConcrete();

        utenti_richieste = new MutableLiveData<>();
        utenti_richieste.setValue(new ArrayList<>());
    }

    public void recuperaRichiesteAmicizia() throws TimeoutException, JSONException {
        List<Utente> lista = utenti_richieste.getValue();
        List<Utente> tmp = repository.getRichiesteAmicizia(repository.getUser().getEmail(), getApplication().getApplicationContext());
        lista.clear();
        lista.addAll(tmp);
        if(Thread.currentThread().getName().equals("main"))
            utenti_richieste.setValue(lista);
        else
            utenti_richieste.postValue(lista);

    }

    public MutableLiveData<List<Utente>> getRichiesteAmicizia(){
        return utenti_richieste;
    }


    private <T> void removeItemFromList(MutableLiveData<List<T>> livedata, T item){
        if(livedata == null || livedata.getValue() == null || item == null)
            return;
        List<T> list = livedata.getValue();
        list.remove(item);
        if(Thread.currentThread().getName().equals("main"))
            livedata.setValue(list);
        else
            livedata.postValue(list);
    }

    public void inviaNotificaRifiutoRichiestaAmicizia(Utente ricevente) throws TimeoutException, JSONException {
        Notifica notifica = new Notifica(repository.getUser().getUsername(), Notifica.Build_message.UTENTE_HA_RIFIUTATO_RICHIESTA);
        repository.addNotifica(notifica,ricevente.getEmail());
    }

    public void inviaNotificaAccettazioneRichiestaAmicizia(Utente ricevente) throws TimeoutException, JSONException {
        Notifica notifica = new Notifica(repository.getUser().getUsername(), Notifica.Build_message.UTENTE_HA_ACCETTATO_RICHIESTA);
        repository.addNotifica(notifica,ricevente.getEmail());
    }


    public void rifiutaRichiestaAmicizia(Utente utenteRifiutato) throws TimeoutException, JSONException {
        removeItemFromList(utenti_richieste,utenteRifiutato);
        repository.removeRichiestaAmicizia(utenteRifiutato.getEmail(),repository.getUser().getEmail());
    }

    public void accettaRichiestaAmicizia(Utente utenteAccettato) throws TimeoutException, JSONException {
        removeItemFromList(utenti_richieste,utenteAccettato);
        repository.removeRichiestaAmicizia(utenteAccettato.getEmail(),repository.getUser().getEmail());
        repository.addAmicizia(utenteAccettato.getEmail(), repository.getUser().getEmail());

    }



}