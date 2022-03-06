package com.example.appandroid.aws.apiGateway;

import android.util.Log;

import com.example.appandroid.globalUtils.JSONObjectManipulator;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.segnalazione.Segnalazione;
import com.example.appandroid.listViewClass.utente.Utente;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import cz.msebera.android.httpclient.Header;

public class ApiGateway {
	private final String url = "https://onsftlm3v1.execute-api.eu-central-1.amazonaws.com";

	public static final int MODE_PUT = 1;
	public static final int MODE_GET = 2;
	public static final int MODE_DELETE = 3;
	public static final int MODE_PATCH = 4;

	private static final String PATH_UTENTE = "/stable/utente";
	private static final String PATH_UTENTEUSERNAME = PATH_UTENTE + "/username";
	private static final String PATH_UTENTESEARCH = PATH_UTENTE + "/search";
	private static final String PATH_LISTA = "/stable/listepersonalizzate";
	private static final String PATH_FILM = "/stable/film";
	private static final String PATH_SEGNALAZIONE = "/stable/segnalazione";
	private static final String PATH_AMICIZIA = "/stable/amicizia";
	private static final String PATH_CONTENUTOLISTA = "/stable/contenutolista";
	private static final String PATH_FILMINCOMUNE = "/stable/filmincomune";
	private static final String PATH_DESCRIZIONELISTA = PATH_LISTA + "/descrizione";
	private static final String PATH_IMMAGINELISTA = PATH_LISTA + "/immaginecopertina";
	private static final String PATH_NOTIFICA = "/stable/notifica";
	private static final String PATH_NOTIFICA_NON_CONSEGNATA = "/stable/notifica/nonlette";
	private static final String PATH_RICHIESTAAMICIZIA = "/stable/richiestaamicizia";
	private static final String PATH_CONTROLLOAMICIZIA= PATH_AMICIZIA + "/controllodueutenti";
	private static final String PATH_STATUSAMICIZIA = PATH_AMICIZIA + "/status";

	private static final String PATH_KEY = "/stable/api-key";
	private static final String PATH_MOVIEKEY = "/stable/api-key-film";


	private String key = null;


	private static ApiGateway apiGateway;

	public static ApiGateway getIstance(){
		if(apiGateway ==null){
			apiGateway = new ApiGateway();
		}

		return apiGateway;
	}

	private ApiGateway(){
		/*if(key==null){
			try {
				initKey();
			} catch (TimeoutException | JSONException e) {
				e.printStackTrace();
				Log.e("Errore ApiGateway :",e.getMessage());
			}
		}*/
	}

	private void initKey() throws TimeoutException {
		if(key!=null)
			return;

		String key = null;
		try {
			key = getKey();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setKey(key);
	}

	private void setKey(String key) {
		this.key=key;
	}

	private String getKey() throws TimeoutException, JSONException {
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_KEY);
		JSONObject jsonObject = esegui(urlStringBuilder.build(),MODE_GET);
		return JSONObjectManipulator.getKeyFromJSONObject(jsonObject);
	}


	public String getMovieApiKey() throws JSONException, TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_MOVIEKEY);
		JSONObject jsonObject = esegui(urlStringBuilder.build(),MODE_GET);
		return JSONObjectManipulator.getMovieDbKeyFromJSONObject(jsonObject);

	}

	public JSONObject esegui(String url, int mode) throws TimeoutException {
		ExecuteQueryRunnable executeQueryRunnable = new ExecuteQueryRunnable(url,mode);
		Thread thread = new Thread(executeQueryRunnable);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = executeQueryRunnable.getJsonObject();
		if(jsonObject!=null)
			return jsonObject;
		else
			throw new TimeoutException("Errore con la connessione");
	}

	private class ExecuteQueryRunnable implements Runnable{
		JSONObject jsonObject ;
		final String url;
		final int mode ;
		final SyncHttpClient client;
		final JsonHttpResponseHandler jsonHttpResponseHandler;


		public ExecuteQueryRunnable(String url,int mode){
			this.url = url;
			this.mode=mode;

			client = new SyncHttpClient();

			jsonHttpResponseHandler = new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					jsonObject = response;
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					System.out.println("ERRORE ");

				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					System.out.println("ERRORE  onFailure");
					System.out.println("Status: " + statusCode);
					//System.out.println(errorResponse.toString());

				}
			};
		}

		@Override
		public void run() {
			Log.i("ExecuteQuery", url);

			if(key!=null){
				client.addHeader("x-api-key",key);
			}

			switch (mode){
				case MODE_GET:
					client.get(url, jsonHttpResponseHandler);
					break;
				case MODE_PUT:
					client.put(url, jsonHttpResponseHandler);
					break;
				case MODE_DELETE:
					client.delete(url, jsonHttpResponseHandler);
					break;
				case MODE_PATCH:
					client.patch(url, jsonHttpResponseHandler);
					break;
				default:
					Log.e("Error mod ","La modalità inserita non è valida");
			}
		}

		public JSONObject getJsonObject() {
				return jsonObject;
		}
	}

	//UTENTE
	public JSONObject addUtente(Utente utente) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_UTENTE);
		urlStringBuilder.addStringParameters("utente", utente.getEmail());
		urlStringBuilder.addStringParameters("username", utente.getUsername());

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}

	public JSONObject checkUsernameExist(String username) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_UTENTEUSERNAME);
		urlStringBuilder.addStringParameters("username", username);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}



	public JSONObject getAmici(String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_AMICIZIA);
		urlStringBuilder.addStringParameters("utente", email);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	/**
	 *
	 * @param email del utente che effettua la ricerca
	 * @param username ricercato
	 * @return JSONObject contenente utenti trovati, escluso utente che ha effettuato la ricerca
	 * @throws TimeoutException
	 */
	public JSONObject cercaUtenti(String email, String username) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_UTENTESEARCH);
		urlStringBuilder.addStringParameters("utente", email);
		urlStringBuilder.addStringParameters("username_ricercato", username);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	public JSONObject getUtente(String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_UTENTE);
		urlStringBuilder.addStringParameters("utente", email);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	public JSONObject getRichiesteAmicizia(String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_RICHIESTAAMICIZIA);
		urlStringBuilder.addStringParameters("utente", email);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}


	public JSONObject getStatusAmicizia(String email1, String email2) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_STATUSAMICIZIA);
		urlStringBuilder.addStringParameters("utente1", email1);
		urlStringBuilder.addStringParameters("utente2", email2);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	public JSONObject getListeDiUtente(String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_LISTA);
		urlStringBuilder.addStringParameters("utente", email);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	public JSONObject getNotificheUtente(String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_NOTIFICA);
		urlStringBuilder.addStringParameters("utente", email);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}
	public JSONObject getNotificheUtenteNonConsegnate(String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_NOTIFICA_NON_CONSEGNATA);
		urlStringBuilder.addStringParameters("utente", email);
		return esegui(urlStringBuilder.build(),MODE_GET);
	}
	public JSONObject setConsegnataNotifica(int idNotifica) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_NOTIFICA_NON_CONSEGNATA);
		urlStringBuilder.addIntegerParameters("idnotifica", idNotifica);
		return esegui(urlStringBuilder.build(),MODE_PATCH);
	}

	//LEGAME TRA 2 UTENTI
	public JSONObject addAmicizia(String email1,String email2) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_AMICIZIA);
		urlStringBuilder.addStringParameters("utente1", email1);
		urlStringBuilder.addStringParameters("utente2", email2);

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}

	public JSONObject addRichiestaAmicizia(String emailRichiede,String emailRiceve) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_RICHIESTAAMICIZIA);
		urlStringBuilder.addStringParameters("utente_richiede", emailRichiede);
		urlStringBuilder.addStringParameters("utente_riceve", emailRiceve);

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}

	public JSONObject removeAmicizia(String email1,String email2) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_AMICIZIA);
		urlStringBuilder.addStringParameters("utente1", email1);
		urlStringBuilder.addStringParameters("utente2", email2);

		return esegui(urlStringBuilder.build(),MODE_DELETE);
	}

	public JSONObject removeRichiestaAmicizia(String emailRichiede,String emailRiceve) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_RICHIESTAAMICIZIA);
		urlStringBuilder.addStringParameters("utente_richiede", emailRichiede);
		urlStringBuilder.addStringParameters("utente_riceve", emailRiceve);

		return esegui(urlStringBuilder.build(),MODE_DELETE);
	}

	public JSONObject sonoAmici(String email1, String email2) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_CONTROLLOAMICIZIA);
		urlStringBuilder.addStringParameters("utente1", email1);
		urlStringBuilder.addStringParameters("utente2", email2);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	public JSONObject getFilmInComune(String email1, String email2) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_FILMINCOMUNE);
		urlStringBuilder.addStringParameters("utente1", email1);
		urlStringBuilder.addStringParameters("utente2", email2);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}

	//LISTA PERSONALIZZATA
	public JSONObject addLista(ListaPersonalizzata listaPersonalizzata, String email) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_LISTA);
		urlStringBuilder.addStringParameters("nome", listaPersonalizzata.getNome());
		urlStringBuilder.addStringParameters("descrizione", listaPersonalizzata.getDescrizione());
		urlStringBuilder.addIntegerParameters("idlista", listaPersonalizzata.getIdLista());
		urlStringBuilder.addStringParameters("fk_utente", email);
		urlStringBuilder.addStringParameters("immagineurl", listaPersonalizzata.getImmagineCopertina());

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}

	public JSONObject getFilmInLista(int idLista) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_CONTENUTOLISTA);
		urlStringBuilder.addIntegerParameters("idlista",idLista);

		return esegui(urlStringBuilder.build(),MODE_GET);
	}


	public JSONObject removeLista(int idLista) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_LISTA);
		urlStringBuilder.addIntegerParameters("idlista", idLista);

		return esegui(urlStringBuilder.build(),MODE_DELETE);
	}

	public JSONObject addFilmInLista(int idLista, String idFilm) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_CONTENUTOLISTA);
		urlStringBuilder.addIntegerParameters("fk_lista", idLista);
		urlStringBuilder.addStringParameters("fk_film", idFilm);

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}

	public JSONObject removeFilmInLista(int idLista, String idFilm) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_CONTENUTOLISTA);
		urlStringBuilder.addIntegerParameters("fk_lista", idLista);
		urlStringBuilder.addStringParameters("fk_film", idFilm);

		return esegui(urlStringBuilder.build(),MODE_DELETE);
	}

	public JSONObject cambiaDescrizioneLista(int idLista, String nuovaDescrizione) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_DESCRIZIONELISTA);
		urlStringBuilder.addIntegerParameters("idlista", idLista);
		urlStringBuilder.addStringParameters("descrizione", nuovaDescrizione);

		return esegui(urlStringBuilder.build(),MODE_PATCH);
	}

	public JSONObject cambiaImmagineLista(int idLista,String nuovaImmagineUrl) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_IMMAGINELISTA);
		urlStringBuilder.addIntegerParameters("idlista", idLista);
		urlStringBuilder.addStringParameters("immagineurl", nuovaImmagineUrl);

		return esegui(urlStringBuilder.build(),MODE_PATCH);
	}

	//SEGNALAZIONE
	public JSONObject addSegnalazione(Segnalazione segnalazione, int idListaSegnalata , String emailUtenteCheSegnala) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_SEGNALAZIONE);
		urlStringBuilder.addIntegerParameters("idsegnalazione", segnalazione.getIdSegnalazione());
		urlStringBuilder.addStringParameters("motivisegnalazione", segnalazione.getMotivoSegnalazione());
		urlStringBuilder.addIntegerParameters("fk_lista", idListaSegnalata);
		urlStringBuilder.addStringParameters("fk_utente",emailUtenteCheSegnala);

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}

	public JSONObject removeSegnalazione(int idSegnalazione) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_SEGNALAZIONE);
		urlStringBuilder.addIntegerParameters("idsegnalazione", idSegnalazione);

		return esegui(urlStringBuilder.build(),MODE_DELETE);
	}

	//NOTIFICA
	public JSONObject removeNotifica(int idNotifica) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_NOTIFICA);
		urlStringBuilder.addIntegerParameters("idnotifica", idNotifica);

	 	return esegui(urlStringBuilder.build(),MODE_DELETE);
	}

	public JSONObject addNotifica(Notifica notifica, String emailProprietarioNotifica) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_NOTIFICA);
		urlStringBuilder.addIntegerParameters("idnotifica", notifica.getIdNotifica());
		urlStringBuilder.addStringParameters("fk_utente",emailProprietarioNotifica);
		urlStringBuilder.addStringParameters("messaggio",notifica.getMessaggio());

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}



	//FILM
	public JSONObject addFilm(String idFilm) throws TimeoutException {
		initKey();
		UrlStringBuilder urlStringBuilder= new UrlStringBuilder(url,PATH_FILM);
		urlStringBuilder.addStringParameters("idfilm", idFilm);

		return esegui(urlStringBuilder.build(),MODE_PUT);
	}



	private class UrlStringBuilder{
		private final String path ;
		private final String base ;
		private final Map<String,String> stringParamaters;
		private final Map<String,Integer> intParamaters;

		private String query ;

		public UrlStringBuilder(String base , String path){
			this.path=path;
			this.base=base;
			stringParamaters = new HashMap<>();
			intParamaters = new HashMap<>();
		}

		public void addStringParameters(String key, String value ){
			if(key != null && value != null)
				stringParamaters.put(key,value);
		}
		public void addIntegerParameters(String key, Integer value ){
			if(key != null && value != null)
				intParamaters.put(key,value);
		}

		public String build(){

			query=base+path+"?";
			for(String key : stringParamaters.keySet() ){
				String value = stringParamaters.get(key);
				query = query.concat(key+"="+value+"&");
			}
			for(String key : intParamaters.keySet() ){
				Integer value = intParamaters.get(key);
				query = query.concat(key+"="+value+"&");
			}

			if(!query.equals(base+path+"?"))
				query = query.substring(0,query.length()-1);

			query = query.replace(" ","%20");

			return query;
		}


	}


}

