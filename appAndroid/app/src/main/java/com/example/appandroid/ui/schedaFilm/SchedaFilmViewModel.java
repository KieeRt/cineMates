package com.example.appandroid.ui.schedaFilm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

public class SchedaFilmViewModel extends ViewModel {

	private RepositoryService repository;
	private MutableLiveData<Film> film ;

	public void init(){
		if(film!=null)
			return;

		repository= RepositoryFactory.getRepositoryConcrete();
		film=new MutableLiveData<>();

	}

	public void recuperaFilm(String idFilm){
		Film filmCercato=repository.getFilmById(idFilm);
		film.postValue(filmCercato);
	}

	public MutableLiveData<Film> getFilm(){
		if(film==null)
			return new MutableLiveData<>();

		return film;
	}
}