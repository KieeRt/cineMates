package com.example.appandroid.ui.contenutoLista;

import android.util.Log;

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

public class ContenutoListaViewModel extends ViewModel {
	private MutableLiveData<List<Film>> listaDiFilm;
	private MutableLiveData<String> descrizione;
	private MutableLiveData<String> titolo;
	private MutableLiveData<Boolean> censored;
	private RepositoryService repository;
	private int idLista;

	public MutableLiveData<String> getDescrizione() {
		if (descrizione == null)
			descrizione = new MutableLiveData<>();

		return descrizione;
	}

	public MutableLiveData<Boolean> getCensored() {
		if (censored == null)
			censored = new MutableLiveData<>();

		return censored;
	}

	public MutableLiveData<String> getTitolo() {
		if (titolo == null)
			titolo = new MutableLiveData<>();

		return titolo;

	}

	public MutableLiveData<List<Film>> getListaFilm() {
		if (listaDiFilm == null)
			listaDiFilm = new MutableLiveData<>(new ArrayList<>());

		return listaDiFilm;
	}

	public void init(int idLista) {

		if (listaDiFilm != null) {
			return;
		}

		this.idLista = idLista;
		repository= RepositoryFactory.getRepositoryConcrete();


		listaDiFilm = new MutableLiveData<>();
		titolo = new MutableLiveData<>();
		descrizione = new MutableLiveData<>();

		listaDiFilm.setValue(new ArrayList<>());
		descrizione.setValue("");
		titolo.setValue("");


	}

	public void recuperaContenutoLista() throws TimeoutException, JSONException {
		List<Film> listaFilm = listaDiFilm.getValue();
		listaFilm.clear();
		listaFilm.addAll(repository.getFilmInLista(idLista));
		listaDiFilm.postValue(listaFilm);
	}


	public void recuperaInfoLista() throws TimeoutException, JSONException {
		String descrizioneCorrente = null;
		String titoloCorrente = null;
		Boolean censoredCorrente = null;

		List<ListaPersonalizzata> elencoListe = repository.getListe(repository.getUser().getEmail());
		for (ListaPersonalizzata lista : elencoListe) {
			if (lista.getIdLista() == idLista) {
				descrizioneCorrente = lista.getDescrizione();
				titoloCorrente = lista.getNome();
				censoredCorrente = lista.isCensored();
				break;
			}
		}

		titolo.postValue(titoloCorrente);
		descrizione.postValue(descrizioneCorrente);
		censored.postValue(censoredCorrente);

	}

	public boolean updateDescrizione(String nuovaDescrizione, int idLista) throws TimeoutException, JSONException {
		repository.cambiaDescrizioneLista(idLista, nuovaDescrizione);
		descrizione.postValue(nuovaDescrizione);
		return true;
	}


	public boolean aggiornaCopertinaLista(int idLista, String immagineCopertina) throws TimeoutException, JSONException {
		return repository.cambiaImmagineLista(idLista, immagineCopertina);
	}

	public void flowRimozioneFilm(int idLista, Film filmInteressato) throws TimeoutException, JSONException {
		rimuoviFilm(idLista, filmInteressato);
		aggiornaCopertinaSuFilmRimosso(idLista, filmInteressato);
	}

	public void aggiornaCopertinaSuFilmRimosso(int idLista, Film filmRimosso) throws TimeoutException, JSONException {

		if (listaDiFilm.getValue().size() > 0) { //LISTA CON 2 ELEMENTI
			aggiornaCopertinaLista(idLista, listaDiFilm.getValue().get(listaDiFilm.getValue().size() - 1).getImmagineCopertina());
			Log.d("AGGIORNO COPERTINA", "Aggiorno..." + listaDiFilm.getValue().get(listaDiFilm.getValue().size() - 1).getImmagineCopertina());
		} else {
			aggiornaCopertinaLista(idLista, "null");
			Log.d("AGGIORNO COPERTINA", "Aggiorno..." + idLista);
		}

	}

	public void rimuoviFilm(int idLista, Film filmInteressato) throws TimeoutException, JSONException {
		repository.removeFilmInLista(idLista, filmInteressato.getIdFilm());

		if (this.idLista == idLista) {
			List<Film> listaAggiornata = listaDiFilm.getValue();
			listaAggiornata.remove(filmInteressato);
			listaDiFilm.postValue(listaAggiornata);
		}
	}


	public List<ListaPersonalizzata> getListeUtente() {
		try {
			return repository.getListe(repository.getUser().getEmail());
		} catch (JSONException | TimeoutException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}


	public void aggiornaListeUtente(Film filmInteressato, List<ListaPersonalizzata> listeChecked, List<ListaPersonalizzata> listeUnchecked) throws TimeoutException, JSONException {

		for (ListaPersonalizzata listaSpuntata : listeChecked) {
			String idFilm = filmInteressato.getIdFilm();
			int idLista = listaSpuntata.getIdLista();
			repository.addFilm(idFilm);
			repository.addFilmInLista(idLista, idFilm);
			repository.cambiaImmagineLista(idLista, filmInteressato.getImmagineCopertina());
		}

		for (ListaPersonalizzata listaNonSpuntata : listeUnchecked) {
			int idLista = listaNonSpuntata.getIdLista();
			flowRimozioneFilm(idLista, filmInteressato);

		}
	}


	public void creaNuovaLista(String titolo, String descrizione) throws TimeoutException, JSONException {
		repository.addLista(new ListaPersonalizzata(titolo, descrizione), repository.getUser().getEmail());

	}


}