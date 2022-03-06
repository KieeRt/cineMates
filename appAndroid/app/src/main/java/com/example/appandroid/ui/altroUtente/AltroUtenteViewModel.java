package com.example.appandroid.ui.altroUtente;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class AltroUtenteViewModel extends AndroidViewModel {

    private final String emailUtente;
    private MutableLiveData<List<ListaPersonalizzata>> listeAltroUtente;
    private MutableLiveData<String> nomeAltroUtente;
    private MutableLiveData<Bitmap> immagineAltroUtente;
    private MutableLiveData<Integer> statusFriend;
    private MutableLiveData<List<Film>> filmInComune;
    private RepositoryService repository;


    public AltroUtenteViewModel(String email, Application application) {
        super(application);
        this.emailUtente=email;
    }

    public MutableLiveData<Integer> getStatusFriend() {
        if(statusFriend==null) {
            return new MutableLiveData<>();
        }

        return statusFriend;
    }

    public MutableLiveData<List<ListaPersonalizzata>> getListeAltroUtente() {
        if(listeAltroUtente==null){
            return new MutableLiveData<>(new ArrayList<>());
        }

        return listeAltroUtente;
    }

    public MutableLiveData<String> getNomeAltroUtente() {
        if(nomeAltroUtente==null){
            return new MutableLiveData<>();
        }

        return nomeAltroUtente;
    }

    public MutableLiveData<Bitmap> getImmagineAltroUtente() {
        if(immagineAltroUtente==null){
            return new MutableLiveData<>();
        }

        return immagineAltroUtente;
    }

    public MutableLiveData<List<Film>> getFilmInComune() {
        if(filmInComune==null){
            return new MutableLiveData<>(new ArrayList<>());
        }

        return filmInComune;
    }

    public void init() throws RuntimeException{
        if(listeAltroUtente != null || nomeAltroUtente != null || immagineAltroUtente != null){
            return;
        }

        repository= RepositoryFactory.getRepositoryConcrete();
        listeAltroUtente = new MutableLiveData<>();
        nomeAltroUtente = new MutableLiveData<>();
        immagineAltroUtente = new MutableLiveData<>();
        statusFriend=new MutableLiveData<>();
        filmInComune=new MutableLiveData<>();

        listeAltroUtente.setValue(new ArrayList<>());
        filmInComune.setValue(new ArrayList<>());


    }

    public void recuperaListeUtente() throws TimeoutException, JSONException {
        List<ListaPersonalizzata> listeRepo = repository.getListe(emailUtente);
        List<ListaPersonalizzata> listeLocali = listeAltroUtente.getValue();
        listeLocali.clear();
        listeLocali.addAll(listeRepo);
        listeAltroUtente.postValue(listeLocali);
    }

    public void recuperaStatusAmicizia()throws TimeoutException, JSONException {
        statusFriend.postValue(repository.getStatusAmicizia(repository.getUser().getEmail(),emailUtente));
    }

    public void recuperaBitmapImmagineUtente(){
        Bitmap bitmap = repository.getBitmapImmagineUtenteQualsiasi(getApplication().getApplicationContext(),emailUtente);
        immagineAltroUtente.postValue(bitmap);
    }

    public void recuperaUsernameUtente() throws TimeoutException, JSONException {
        String username_utente_ricercato = repository.getUtente(emailUtente, getApplication().getApplicationContext()).getUsername();
        nomeAltroUtente.postValue(username_utente_ricercato);
    }


    public void rimuoviAmicizia() throws TimeoutException, JSONException {
        repository.removeAmicizia(emailUtente,repository.getUser().getEmail());
        statusFriend.postValue(1);//NON AMICO
    }

    public void inviaRichiestaAmicizia() throws TimeoutException, JSONException {
        repository.addRichiestaAmicizia(repository.getUser().getEmail(),emailUtente);

        statusFriend.postValue(3);//Amicizia inviata

    }

    public void rifiutaRichiestaAmicizia() throws TimeoutException, JSONException {
        repository.removeRichiestaAmicizia(emailUtente,repository.getUser().getEmail());

        statusFriend.postValue(1);//NON AMICO
    }

    public void accettaRichiestaAmicizia() throws TimeoutException, JSONException {
        repository.removeRichiestaAmicizia(emailUtente,repository.getUser().getEmail());
        repository.addAmicizia(emailUtente,repository.getUser().getEmail());

        statusFriend.postValue(0);//AMICO
    }

    public void rimuoviRichiestaAmiciziaInviata() throws TimeoutException, JSONException {
        repository.removeRichiestaAmicizia(repository.getUser().getEmail(),emailUtente);

        statusFriend.postValue(1);//Non amico
    }

    public void caricaFilmInComune() throws TimeoutException, JSONException {
        List<Film> filmInComuneRepo = repository.getFilmInComune(repository.getUser().getEmail(),emailUtente);
        List<Film> filmInComuneLocali = filmInComune.getValue();
        filmInComuneLocali.clear();
        filmInComuneLocali.addAll(filmInComuneRepo);
        filmInComune.postValue(filmInComuneLocali);
    }


}