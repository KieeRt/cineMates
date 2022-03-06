package com.example.appandroid.ui.filmInComune;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class FilmInComuneViewModel extends ViewModel {

	private MutableLiveData<List<Film>> filmInComune ;
	private RepositoryService repository;
	private String emailAltroUtente;

	public MutableLiveData<List<Film>> getFilmInComune(){
		if(filmInComune==null){
			return new MutableLiveData<>(new ArrayList<>());
		}

		return filmInComune;
	}

	public void init(String emailAltroUtente){
		if(filmInComune!=null){
			return;
		}

		this.emailAltroUtente=emailAltroUtente;
		repository= RepositoryFactory.getRepositoryConcrete();
		filmInComune=new MutableLiveData<>();
		filmInComune.setValue(new ArrayList<>());


	}

	public void recuperaFilmInComune() throws TimeoutException, JSONException {
		List<Film> listaFilm = filmInComune.getValue();
		List<Film> listaRecuperata = repository.getFilmInComune(repository.getUser().getEmail(),emailAltroUtente);
		listaFilm.clear();
		listaFilm.addAll(listaRecuperata);
		filmInComune.postValue(listaFilm);

	}

}