package com.example.appandroid.globalUtils;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JSONObjectManipulator {

	public static String getKeyFromJSONObject(JSONObject jsonObject) throws JSONException{
		if(jsonObject != null && jsonObject.length()>0){
			return jsonObject.getJSONArray("key").getJSONArray(0).getString(0);
		}
		return null;
	}


	public static String getMovieDbKeyFromJSONObject(JSONObject jsonObject) throws JSONException{
		if(jsonObject != null && jsonObject.length()>0){
			return jsonObject.getJSONArray("key").getString(0);
		}
		return null;
	}

	public static Utente getUtenteDaJSONObject(JSONObject jsonObject) throws JSONException {
		Utente utente = null;
		System.out.println("JSONObject : "+jsonObject);

		if(jsonObject != null && jsonObject.length()>0){

			String username = jsonObject.getString("username");
			String email = jsonObject.getString("email");
			utente = new Utente(username,email);

		}
		return utente;
	}


	/**
	 * @param jsonObject contenente le informazioni di un utente con il legame di amicizia che ha con l'utente
	 * attuale.
	 * @return Utente corrispondente alle informazioni ricavate dal jsonObject in input
	 * SE il jsonObject sia null o vuoto viene restituto null.
	 * @throws JSONException nel caso in cui il json object non abbia la struttura corretta
	 * */
	public static Utente getUtenteDaJSONObjectConCurrentStatus(JSONObject jsonObject) throws JSONException {
		Utente utente = null;

		if(jsonObject != null && jsonObject.length()>0){

			String username = jsonObject.getString("username");
			String email = jsonObject.getString("email");
			int CURRENT_STATE =  jsonObject.getInt("CURRENT_STATE");

			utente = new Utente(username,email, CURRENT_STATE);
		}
		return utente;
	}

	public static ListaPersonalizzata getListaPersonalizzataDaJSONObject(JSONObject jsonObject) throws JSONException {
		ListaPersonalizzata listaPersonalizzata = null;
		List<String> filmInLista = new ArrayList<>();
		System.out.println("JSONObject : "+jsonObject);

		if(jsonObject != null && jsonObject.length()>0){
			String nome = jsonObject.getString("nome");
			String descrizione = jsonObject.getString("descrizione");
			int idLista = jsonObject.getInt("idlista");
			String immagineUrl = jsonObject.getString("immagineurl");
			String censoredString = jsonObject.getString("censored");
			boolean censored = censoredString.equals("true");

			JSONArray jsonArrayFilm = jsonObject.getJSONArray("film");
			for(int i = 0 ; i<jsonArrayFilm.length() ; i++){
				filmInLista.add(jsonArrayFilm.getJSONObject(i).getString("imdbid"));
			}

			listaPersonalizzata = new ListaPersonalizzata(nome,descrizione,filmInLista,immagineUrl, jsonArrayFilm.length(), idLista,censored);

		}
		return listaPersonalizzata;
	}

	public static Film getFilmDaJSONObject(JSONObject jsonObject) throws JSONException {
		Film film = null;
		RepositoryService repository = RepositoryFactory.getRepositoryConcrete();
		System.out.println("JSONObject : "+jsonObject);

		if(jsonObject != null && jsonObject.length()>0){
			String idFilm = jsonObject.getString("imdbid");
			film = repository.getFilmById(idFilm);
		}


		return film;
	}

	public static List<Utente> getUtentiDaJSONArray(JSONArray jsonArray) throws JSONException {
		List<Utente> elencoUtenti = new ArrayList<>();
		if(jsonArray != null){
			for(int i = 0 ; i < jsonArray.length() ; i++ ){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Utente utente = getUtenteDaJSONObject(jsonObject);
				elencoUtenti.add(utente);
			}
		}

		Collections.sort(elencoUtenti, new Comparator<Utente>() {
			@Override
			public int compare(Utente o1, Utente o2) {
				return o1.getUsername().compareTo(o2.getUsername());
			}
		});

		return elencoUtenti;
	}

	public static List<Utente> getUtentiDaJSONArrayConCurrentStatus (JSONArray jsonArray) throws JSONException {
		List<Utente> elencoUtenti = new ArrayList<>();
		if(jsonArray != null){
			for(int i = 0 ; i < jsonArray.length() ; i++ ){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Utente utente = getUtenteDaJSONObjectConCurrentStatus(jsonObject);
				elencoUtenti.add(utente);
			}
		}
		return elencoUtenti;
	}


	public static List<Film> getFilmDaJSONArray(JSONArray jsonArray) throws JSONException {
		List<Film> elencoFilm = new ArrayList<>();
		Object syncObject = new Object();

		if(jsonArray != null){
			int numeroFilm = jsonArray.length();
			ThreadCercaFilm[] arrayThread = new ThreadCercaFilm[numeroFilm];

			for (int i = 0 ; i < numeroFilm ; i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				arrayThread[i] = new ThreadCercaFilm(jsonObject,elencoFilm,syncObject);
			}

			for(int i = 0; i < numeroFilm; i++){
				arrayThread[i].start();
			}

			for(int i = 0; i < numeroFilm; i++){
				try {
					arrayThread[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		return elencoFilm;
	}

	private static class ThreadCercaFilm extends Thread {
		final JSONObject jsonObject ;
		final List<Film> elencoFilm;
		final Object sync ;

		public ThreadCercaFilm(JSONObject jsonObject, List<Film> elencoFilm,Object sync) {
			this.jsonObject=jsonObject;
			this.elencoFilm=elencoFilm;
			this.sync=sync;
		}

		@Override
		public void run() {
			Film film = null;
			try {
				film = getFilmDaJSONObject(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			synchronized (sync){
				elencoFilm.add(film);
			}
		}

	}

	public static List<ListaPersonalizzata> getListeDaJSONArray(JSONArray jsonArray) throws JSONException {
		List<ListaPersonalizzata> elencoListe = new ArrayList<>();

		if(jsonArray != null){
			for(int i = 0 ; i < jsonArray.length() ; i++ ){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ListaPersonalizzata listaPersonalizzata = getListaPersonalizzataDaJSONObject(jsonObject);
				elencoListe.add(listaPersonalizzata);
			}
		}



		return elencoListe;
	}

	public static List<Notifica> getNotificaDaJSONArray(JSONArray jsonArray) throws JSONException {
		List<Notifica> notifiche = new ArrayList<>();

		if(jsonArray != null){
			for(int i = 0 ; i < jsonArray.length() ; i++ ){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Notifica notifica = getNotificaDaJSONObject(jsonObject);
				notifiche.add(notifica);
			}
		}
		return notifiche;
	}

	public static Notifica getNotificaDaJSONObject(JSONObject jsonObject) throws JSONException {
		Notifica notifica = null;
		System.out.println("JSONObject : "+jsonObject);
		if(jsonObject != null && jsonObject.length()>0){
			int idNotifica = jsonObject.getInt("idnotifica");
			String messaggio = jsonObject.getString("messaggio");
			notifica = new Notifica(idNotifica,messaggio);
		}

		return notifica;
	}

	public static JSONObject createJSONOBjectByIdFilm(List<String> elencoIdFilm){
		JSONArray jsonArrayContenitore = new JSONArray();
		JSONObject jsonObjectFinale = new JSONObject();

		for( String idFilm : elencoIdFilm){
			try {
				JSONObject filmInterno = new JSONObject();
				filmInterno.put("imdbid",idFilm);
				jsonArrayContenitore.put(filmInterno);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			jsonObjectFinale.put("film", jsonArrayContenitore);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObjectFinale;
	}



	public static boolean getResponse(JSONObject jsonObject) throws JSONException {
		boolean esito = false;
		System.out.println("JSONObject : "+jsonObject);
		if(jsonObject != null){
			String risposta = jsonObject.getString("response");
			if(risposta.equals("true")){
				esito = true;
			}
		}
		return esito ;
	}

	public static List<String> getListeIdFilmByJSONArray(JSONArray jsonArray) throws JSONException {
		List<String> listaId = new ArrayList<>();

		if (jsonArray != null ) {
			for(int i = 0 ; i < jsonArray.length() ; i++){

				listaId.add(jsonArray.getJSONObject(i).getString("imdbid"));

			}
		}

		return listaId;
	}
}
