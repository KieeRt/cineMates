package com.example.appandroid.movieApi;

import android.util.Log;

import com.example.appandroid.listViewClass.film.Film;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MovieApi {
	private final SyncHttpClient client;
	private final List<Film> filmTrovati ;
	private final String apikey ;

	public MovieApi(String apikey){
		this.apikey = apikey;
		client = new SyncHttpClient();
		filmTrovati=new ArrayList<>();
	}

	// INPUT  : TITOLO DEL FILM
	// OUTPUT : LISTA DI FILM CON CARATTERISTICHE COMPATTE(TITOLO, ANNO, ID, COPERTINA)
	public List<Film> effettuaRicercaCompatta(String titolo){
		int i = 1;
		JSONArray jsonArrayFilmTrovatiPagine;

		do{
			String urlFilmPagine = generaUrlByTitolo(titolo,i++);
			jsonArrayFilmTrovatiPagine = recuperaJSONArrayFilmDaUrl(urlFilmPagine);
			if(jsonArrayFilmTrovatiPagine!=null)
				filmTrovati.addAll(estrapolaElencoFilmDaJsonArray(jsonArrayFilmTrovatiPagine));
		}while (jsonArrayFilmTrovatiPagine!=null && i<5);

		return filmTrovati;
	}

	// INPUT  : TITOLO DEL FILM
	// OUTPUT : LISTA DI FILM CON CARATTERISTICHE ESTESE(TITOLO, ANNO, ID, COPERTINA, GENERE, DURATA, VALUTAZIONE, REGISTA, TRAMA)
	public List<Film> effettuaRicercaEstesa(String titolo){
		List<Film> filmTrovatiCompatti = effettuaRicercaCompatta(titolo);
		List<Film> elencoFilmEstesi = new ArrayList<>() ;

		for (Film film : filmTrovatiCompatti){
			elencoFilmEstesi.add(recuperaFilmEstesoById(film.getIdFilm()));
		}

		return elencoFilmEstesi;
	}

	// INPUT  : IMBDID DEL FILM
	// OUTPUT : FILM CON CARATTERISTICHE ESTESE(TITOLO, ANNO, ID, COPERTINA, GENERE, DURATA, VALUTAZIONE, REGISTA, TRAMA)
	// NOTA   : SE ID NON VALIDO RESTITUISCE NULL
	public Film recuperaFilmEstesoById (String id){
		JSONObject jsonObjectFilm = recuperaJSONObjectFilmEsteso(id);
		return estrapolaFilmEstesoDaJsonObject(jsonObjectFilm);
	}

	// INPUT  : URL DI RICERCA
	// OUTPUT : JSON ARRAY CONTENENTE TUTTI I FILM TROVATI
	// NOTA   : JSONARRAY DI OUTPUT CONTERRA FILM IN FORMA DI JSONOBJECT
	private JSONArray recuperaJSONArrayFilmDaUrl(String url){
		final JSONArray[] elencoFilmTrovati = new JSONArray[1];

		Thread td1 = new Thread(() -> {
			RequestParams params = new RequestParams();
			params.add("app", "mobile");
			params.add("type", "mobile");
			Log.d("URL IMDB", url );
			client.post(url, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {

						elencoFilmTrovati[0] = response.getJSONArray("Search");

					} catch (JSONException e) {
						elencoFilmTrovati[0] = null;
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					elencoFilmTrovati[0] = null;
				}
			});
		});

		td1.start();

		try {
			td1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return elencoFilmTrovati[0];
	}



	//	INPUT  : IMBDID DEL FILM
	//	OUTPUT : JSONOBJECT ESTESO CORRISPONDENTE ALL'ID IN INPUT
	// 	NOTA   : LA RICERCA VIENE EFFETTUATA TRAMITE RICHIESTA HTTP
	private JSONObject recuperaJSONObjectFilmEsteso(String id){
		String url = generaUrlById(id);
		JSONObject filmTrovato[] = new JSONObject[1];

		Thread td1 = new Thread(() -> {
			RequestParams params = new RequestParams();
			params.add("app", "mobile");
			params.add("type", "mobile");

			System.out.println("URL RICHIESTA "+url);
			client.post(url,params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					filmTrovato[0] = response;
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					filmTrovato[0] = null;
				}
			});
		});

		td1.start();

		try {
			td1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return filmTrovato[0];
	}


	// INPUT  : UN JSON OBJECT
	// OUTPUT : UN OGGETTO DI TIPO FILM CON CARATTERISTICHE ESTESE CORRISPONDENTI AL JSONOBJECT
	// PREREQUISITO : IL JSONOBJECT DEVE CONTENERE LE INFORMAZIONI DI UN SINGOLO FILM
	// 				  ALTRIMENTI VIENE LANCIATO UN IllegalArgumentException
	private Film estrapolaFilmEstesoDaJsonObject(JSONObject jsonObjectFilm){
		Film film = null;

		if(jsonObjectFilm != null){
			try {
				String titolo = jsonObjectFilm.getString("Title");
				String id = jsonObjectFilm.getString("imdbID");
				String durata = jsonObjectFilm.getString("Runtime");
				String genere = jsonObjectFilm.getString("Genre");
				String valutazione = jsonObjectFilm.getString("imdbRating");
				String descrizione = jsonObjectFilm.getString("Plot");
				String immagineCopertina = jsonObjectFilm.getString("Poster");
				String regista = jsonObjectFilm.getString("Director");

				film = new Film(titolo,descrizione,regista,durata,valutazione,immagineCopertina,genere,id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return film;
	}

	// INPUT  : UN JSON OBJECT
	// OUTPUT : UN OGGETTO DI TIPO FILM CON CARATTERISTICHE COMPATTE CORRISPONDENTI AL JSONOBJECT
	// PREREQUISITO : IL JSONOBJECT DEVE CONTENERE LE INFORMAZIONI DI UN SINGOLO FILM
	// 				  ALTRIMENTI VIENE LANCIATO UN IllegalArgumentException
	private Film estrapolaFilmCompattoDaJsonObject(JSONObject jsonObjectFilm){
		Film film = null;

		if(jsonObjectFilm != null){
			try {
				String titolo = jsonObjectFilm.getString("Title");
				String id = jsonObjectFilm.getString("imdbID");
				String immagineCopertina = jsonObjectFilm.getString("Poster");


				film = new Film(titolo,null,null,null,null,immagineCopertina,null,id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return film;
	}

	// INPUT  : UN JSONARRAY
	// OUTPUT : L'ELENCO DI FILM , DOVE OGNI FILM HA CARATTERISTICHE CORRISPONDENTI AI JSONOBJECT
	// 			DI CUI IL JSONARRAY E FORMATO
	private List<Film> estrapolaElencoFilmDaJsonArray(JSONArray jsonArrayElencoFilm){
		List<Film> elencoFilm = null;

		if(jsonArrayElencoFilm != null){
			int numeroRisultati = jsonArrayElencoFilm.length();
			elencoFilm = new ArrayList<>();
			for(int i = 0 ; i < numeroRisultati ; i++){
				try {
					JSONObject jsonObjectFilm = jsonArrayElencoFilm.getJSONObject(i);
					Film film = estrapolaFilmCompattoDaJsonObject(jsonObjectFilm);
					elencoFilm.add(film);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return elencoFilm;
	}

	/**
	 *	Input titoloDaRicercare è una stringa che rappresenta il parametro "title" da inserire
	 *	nella richiesta da effettuare alla classe MovieApi
	 *	paginaDaVisualizzare è riferito al numero di pagina che si vuole visualizzare tra i risultati
	 *	output ritorna un oggetto String che rappresenta l'url normalizzato
	 *	(Senza spazi prima e dopo il titolo, al posto degli spazi %20) da passare al client per effettuare la richiesta
	 *	Ritorna NULL nel caso in cui titolo da ricercare == NULL
	 *  THROW IllegalArgumentException nel caso in cui paginaDaVisualizzare<0
	 * */
	public String generaUrlByTitolo(String titoloDaRicercare, int paginaDaVisualizzare) throws IllegalArgumentException{
		if(titoloDaRicercare==null)
			return null;

		if (paginaDaVisualizzare<0)
			throw new IllegalArgumentException("Page < 0");



		String titoloNormalizzato = normalizzaTitolo(titoloDaRicercare);

		//String apikey = "da34fef";
		String url = "http://www.omdbapi.com/?apikey="+apikey+"&type=movie";

		if(titoloNormalizzato!=null){
			url = url.concat("&s="+titoloNormalizzato);
		}

			url = url.concat("&page="+paginaDaVisualizzare);

		return url;
	}

	public String generaUrlById(String idFilm){
		//String apikey = "da34fef";
		String url = "http://www.omdbapi.com/?apikey="+apikey+"&type=movie";

		if(idFilm!=null){
			url = url.concat("&i="+idFilm);
		}

		return url;
	}

	private String normalizzaTitolo(String string){
		String stringaNormalizzata = null;
		if(string != null){
			//Rimuove spazi bianchi all'inizio e alla fine
			stringaNormalizzata = string.trim();

			//Sostituisce gli spazi bianchi all'interno con %20 che è lo standard per rappresenzare lo space
			stringaNormalizzata = stringaNormalizzata.replace(" ","%20");
		}
		return stringaNormalizzata;
	}

}
