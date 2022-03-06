package com.example.appandroid.ui.elencoListePropie;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ElencoListeProprieViewModel extends ViewModel {
	private MutableLiveData<List<ListaPersonalizzata>> liste;
	private RepositoryService repository;

	public MutableLiveData<List<ListaPersonalizzata>> getListe(){
		if( liste == null)
			liste = new MutableLiveData<>(new ArrayList<>());
		return liste;
	}

	public void init(){
		if(liste != null){
			return;
		}

		repository= RepositoryFactory.getRepositoryConcrete();

		liste = new MutableLiveData<>();
		liste.setValue(new ArrayList<>());

	}

	public void recuperaElencoListe() throws TimeoutException, JSONException {
		List<ListaPersonalizzata> elencoListe = liste.getValue();
		elencoListe.clear();
		elencoListe.addAll(repository.getListe(repository.getUser().getEmail()));
		liste.postValue(elencoListe);
	}



	public boolean removeLista(int idLista) throws TimeoutException, JSONException {
		repository.removeLista(idLista);

		List<ListaPersonalizzata> listeUtenteCorrenti = liste.getValue();
		for(ListaPersonalizzata lista : listeUtenteCorrenti){
			if(lista.getIdLista()==idLista){
				listeUtenteCorrenti.remove(lista);
				liste.postValue(listeUtenteCorrenti);
				break;
			}
		}

		return true;

	}

	public boolean addLista(String nuovoTitolo, String nuovaDescrizione) throws TimeoutException, JSONException {
		ListaPersonalizzata listaCreata = new ListaPersonalizzata(nuovoTitolo,nuovaDescrizione);
		repository.addLista(listaCreata,repository.getUser().getEmail());

		List<ListaPersonalizzata> listeUtenteCorrenti = liste.getValue();
		listeUtenteCorrenti.add(listaCreata);
		liste.postValue(listeUtenteCorrenti);

		return true;
	}
}