package com.example.appandroid.ui.ricerca;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import java.util.ArrayList;
import java.util.List;

public class RicercaViewModel extends ViewModel {
    private MutableLiveData<List<Film>> elencoFilmCercati ;
    private MutableLiveData<Boolean> ricercaFinita ;

    private RepositoryService repository ;

    public MutableLiveData<List<Film>> getRisultati(){
        if(elencoFilmCercati==null)
            return new MutableLiveData<>(new ArrayList<>());

        return elencoFilmCercati;
    }

    public MutableLiveData<Boolean> getRicercaFinita(){
        if(ricercaFinita==null)
            return new MutableLiveData<>();

        return ricercaFinita;
    }

    public void init(){
        if(elencoFilmCercati!=null)
            return;

        elencoFilmCercati=new MutableLiveData<>();
        elencoFilmCercati.setValue(new ArrayList<>());
        ricercaFinita=new MutableLiveData<>();
        ricercaFinita.setValue(false);

        repository= RepositoryFactory.getRepositoryConcrete();

    }



    public void effettuaRicerca(String titolo){
        ricercaFinita.postValue(false);

        if(!titolo.equals("")){
            List<Film> listaRisultati =  repository.cercaFilmByTitolo(titolo);
            List<Film> listaRisultatiVecchi = elencoFilmCercati.getValue();
            listaRisultatiVecchi.clear();
            listaRisultatiVecchi.addAll(listaRisultati);
            elencoFilmCercati.postValue(listaRisultatiVecchi);
            ricercaFinita.postValue(true);
        }
        else{
            List<Film> listaRisultatiVecchi = elencoFilmCercati.getValue();
            listaRisultatiVecchi.clear();
            elencoFilmCercati.postValue(listaRisultatiVecchi);
            ricercaFinita.postValue(true);
        }
    }


}
