package com.example.appandroid.ui.ricercaUtenti;

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

public class RicercaUtentiViewModel extends AndroidViewModel {
    private MutableLiveData<List<Utente>> risultatiRicerca;
    private MutableLiveData<Boolean> ricercaFinita;
    private RepositoryService repository;

    public RicercaUtentiViewModel(Application application) {
        super(application);
    }

    public MutableLiveData<List<Utente>> getRisultatiRicerca() {
        if(risultatiRicerca==null)
            return new MutableLiveData<>(new ArrayList<>());

        return risultatiRicerca;
    }

    public MutableLiveData<Boolean> getRicercaFinita(){
        if(ricercaFinita==null)
            return new MutableLiveData<>();

        return ricercaFinita;
    }


    public void init(){
        if(risultatiRicerca != null) {
            ricercaFinita.setValue(false);
            risultatiRicerca.getValue().clear();
            return;
        }

        repository= RepositoryFactory.getRepositoryConcrete();


        risultatiRicerca = new MutableLiveData<>();
        ricercaFinita = new MutableLiveData<>();

        risultatiRicerca.setValue(new ArrayList<>());
        ricercaFinita.setValue(false);

    }


    public void effettuaRicerca(String queryRicerca) throws TimeoutException, JSONException {
        ricercaFinita.postValue(false);
        List<Utente> listaRisultatiRepo = null;
        listaRisultatiRepo = repository.cercaUtenti(repository.getUser().getEmail(),queryRicerca, getApplication().getApplicationContext());
        List<Utente> listaRisultatiLocali = risultatiRicerca.getValue();
        listaRisultatiLocali.clear();
        listaRisultatiLocali.addAll(listaRisultatiRepo);
        risultatiRicerca.postValue(listaRisultatiLocali);
        ricercaFinita.postValue(true);

    }


    public void accettaRichiestaAmicizia(String emailAltroUtente) throws TimeoutException, JSONException {
        repository.addAmicizia(repository.getUser().getEmail(), emailAltroUtente);
        repository.removeRichiestaAmicizia(emailAltroUtente,repository.getUser().getEmail());
        inviaNotificaAccettazioneRichiestaAmicizia(emailAltroUtente);
        updateStatusUser(Utente.UTENTE_AMICO,emailAltroUtente);

    }

    public void rifiutaRichiestaAmicizia(String email_utente_richiede) throws TimeoutException, JSONException {
        repository.removeRichiestaAmicizia(email_utente_richiede, repository.getUser().getEmail());
        inviaNotificaRifiutoRichiestaAmicizia(email_utente_richiede);
        updateStatusUser(Utente.UTENTE_NONAMICO,email_utente_richiede);

    }

    public void rimuoviAmicizia( String emailAltroUtente) throws TimeoutException, JSONException {
        repository.removeAmicizia(repository.getUser().getEmail(), emailAltroUtente);
        updateStatusUser(Utente.UTENTE_NONAMICO,emailAltroUtente);
    }

    public void cancellaRichiestaAmicizia(String emailRiceve) throws TimeoutException, JSONException {
        repository.removeRichiestaAmicizia(repository.getUser().getEmail(), emailRiceve);
        updateStatusUser(Utente.UTENTE_NONAMICO,emailRiceve);

    }

    public void addRichiestaAmicizia(String emailRiceve) throws TimeoutException, JSONException {
        repository.addRichiestaAmicizia(repository.getUser().getEmail(),emailRiceve);
        inviaNotificaRichiestaAmicizia(emailRiceve);
        updateStatusUser(Utente.UTENTE_AMICIZIA_INVIATA,emailRiceve);
    }

    public void inviaNotificaRichiestaAmicizia(String emailRicevente) throws TimeoutException, JSONException {
        Notifica notifica = new Notifica(repository.getUser().getUsername(),Notifica.Build_message.UTENTE_HA_INVIATO_RICHIESTA);
        repository.addNotifica(notifica,emailRicevente);
    }

    public void inviaNotificaRifiutoRichiestaAmicizia(String emailRicevente) throws TimeoutException, JSONException {
        Notifica notifica = new Notifica(repository.getUser().getUsername(),Notifica.Build_message.UTENTE_HA_RIFIUTATO_RICHIESTA);
        repository.addNotifica(notifica,emailRicevente);
    }

    public void inviaNotificaAccettazioneRichiestaAmicizia(String emailRicevente) throws TimeoutException, JSONException {
        Notifica notifica = new Notifica(repository.getUser().getUsername(),Notifica.Build_message.UTENTE_HA_ACCETTATO_RICHIESTA);
        repository.addNotifica(notifica,emailRicevente);
    }

    public void updateStatusUser(int newStatus, String emailUtenteDaAggiornare){
        List<Utente> utenti = risultatiRicerca.getValue();
        Utente utente = utenti.stream()
                .filter(o -> o.getEmail().equals(emailUtenteDaAggiornare))
                .findAny()
                .orElse(null);

        if(utente!=null)
            utente.setCURRENT_STATE(newStatus);

        if(Thread.currentThread().getName().equals("main"))
            risultatiRicerca.setValue(utenti);
        else
            risultatiRicerca.postValue(utenti);


    }



}