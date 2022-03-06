package com.example.appandroid.ui.contenutoListaAltroUtente;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ContenutoListaAltroUtenteViewModel extends ViewModel {

    private int idListaAltroUtente;
    private MutableLiveData<List<Film>> listaDiFilm ;
    private RepositoryService repository ;

    public ContenutoListaAltroUtenteViewModel(int idlista) {
        this.idListaAltroUtente = idlista;
    }

    public MutableLiveData<List<Film>> getListaFilm () {
        if (listaDiFilm == null)
            listaDiFilm = new MutableLiveData<>(new ArrayList<>());

        return listaDiFilm;
    }

    public void init(){

        if(listaDiFilm != null){
            return;
        }

        repository= RepositoryFactory.getRepositoryConcrete();


        listaDiFilm=new MutableLiveData<>();
        listaDiFilm.setValue(new ArrayList<>());

    }

    public void recuperaContenutoLista() throws TimeoutException, JSONException {
        List<Film> listaFilm = listaDiFilm.getValue();
        listaFilm.clear();
        listaFilm.addAll(repository.getFilmInLista(idListaAltroUtente));
        listaDiFilm.postValue(listaFilm);
    }
}