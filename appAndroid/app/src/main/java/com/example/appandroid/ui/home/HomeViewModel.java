package com.example.appandroid.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class HomeViewModel extends ViewModel {

	private MutableLiveData<List<ListaPersonalizzata>> listeUtente;
	private MutableLiveData<List<Film>> listFilmDaGuardare;

	private RepositoryService repository;

	public MutableLiveData<List<ListaPersonalizzata>> getListeUtenteMutable () {
		if (listeUtente == null)
			listeUtente = new MutableLiveData<>(new ArrayList<>());

		return listeUtente;
	}



	public MutableLiveData<List<Film>> getListFilmDaGuardare() {
		if (listFilmDaGuardare == null)
			listFilmDaGuardare = new MutableLiveData<>(new ArrayList<>());

		return listFilmDaGuardare;
	}

	public void init(){

		if(listeUtente!=null && listFilmDaGuardare!=null){
			return;
		}

		repository= RepositoryFactory.getRepositoryConcrete();


		listeUtente=new MutableLiveData<>();
		listFilmDaGuardare =new MutableLiveData<>();

		listeUtente.setValue(new ArrayList<>());
		listFilmDaGuardare.setValue(new ArrayList<>());



	}

	public void recuperaListeProprieEFilmDaGuardare() throws TimeoutException, JSONException {
		List<ListaPersonalizzata> elencoListe = repository.getListe(repository.getUser().getEmail());
		List<ListaPersonalizzata> elencoListeLocali = listeUtente.getValue();
		elencoListeLocali.clear();
		elencoListeLocali.addAll(elencoListe);
		listeUtente.postValue(elencoListeLocali);

		for(ListaPersonalizzata lista : elencoListe){
			if(lista.getNome().equals("Film da guardare")){
				List<Film> films = repository.getFilmInLista(lista.getIdLista());
				List<Film> filmLocali = listFilmDaGuardare.getValue();
				filmLocali.clear();
				filmLocali.addAll(films);
				listFilmDaGuardare.postValue(filmLocali);
			}
		}
	}

	public boolean addLista(String nuovoTitolo, String nuovaDescrizione) throws TimeoutException, JSONException {
		ListaPersonalizzata listaCreata = new ListaPersonalizzata(nuovoTitolo,nuovaDescrizione);
		repository.addLista(listaCreata,repository.getUser().getEmail());


		//List<ListaPersonalizzata> listeUtenteCorrenti = listeUtente.getValue();
		//listeUtenteCorrenti.add(listaCreata);
		//listeUtente.setValue(listeUtenteCorrenti);

		return true;
	}


}