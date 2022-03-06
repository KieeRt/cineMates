package com.example.appandroid.ui.amici;

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

public class AmiciViewModel extends AndroidViewModel {

    private MutableLiveData<List<Utente>>  utenti_amici;
    private MutableLiveData<List<Utente>> utenti_richieste;
    private RepositoryService repository ;

    public AmiciViewModel(Application application){
        super(application);
    }
    public void init(){
        if(utenti_amici != null && utenti_richieste != null){
            return;
        }

        repository= RepositoryFactory.getRepositoryConcrete();

        //Inizializzo perche' prima che  ritorna il risultato Adapter prendera in riferimento la lista, se la lista e' null Adapter non si aggiornera' succesivamente
        utenti_amici = new MutableLiveData<>();
        utenti_amici.setValue(new ArrayList<>());

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

    public void recuperaAmici() throws TimeoutException, JSONException {
        List<Utente> lista = utenti_amici.getValue();
        List<Utente> tmp = repository.getAmici(repository.getUser().getEmail(), getApplication().getApplicationContext());
        lista.clear();
        lista.addAll(tmp);
        if(Thread.currentThread().getName().equals("main"))
            utenti_amici.setValue(lista);
        else
            utenti_amici.postValue(lista);
    }

    public MutableLiveData<List<Utente>> getAmici(){
        return utenti_amici;
    }

    public MutableLiveData<List<Utente>> getRichiesteAmicizia(){
        return utenti_richieste;
    }

    public void accettaRichiestaAmicizia(Utente utente_riceve, Utente utente_richiede)  throws TimeoutException, JSONException{
        repository.addAmicizia(utente_riceve.getEmail(), utente_richiede.getEmail());
        addToList(utenti_amici, utente_richiede);
    }


    public boolean removeRichiestaAmicizia( Utente utente_richiede)  throws TimeoutException, JSONException{
        repository.removeRichiestaAmicizia(utente_richiede.getEmail(), repository.getUser().getEmail());
        removeItemFromList(utenti_richieste, utente_richiede);
        return true;
    }

    public void flowAccettazioneRichiestaAmicizia( Utente utente_richiede) throws TimeoutException, JSONException {
        removeRichiestaAmicizia(utente_richiede);
        utente_richiede.setCURRENT_STATE(Utente.UTENTE_AMICO);
        accettaRichiestaAmicizia(repository.getUser(), utente_richiede);
    }


    public void rimuoviAmicizia( Utente utente_amico) throws TimeoutException, JSONException{
        repository.removeAmicizia(repository.getUser().getEmail(), utente_amico.getEmail());
        removeItemFromList(utenti_amici, utente_amico);
    }



    private <T> void addToList(MutableLiveData<List<T>> livedata, T item){
        if(livedata == null || livedata.getValue() == null || item == null)
            return;
        List<T> list = livedata.getValue();
        list.add(item);
        if(Thread.currentThread().getName().equals("main"))
            livedata.setValue(list);
        else
            livedata.postValue(list);
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

}


