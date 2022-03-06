package com.example.appandroid.repository;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.appandroid.aws.apiGateway.ApiGateway;
import com.example.appandroid.aws.s3.S3Class;
import com.example.appandroid.globalUtils.JSONObjectManipulator;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.globalUtils.utils.BackgroundBitmapCache;
import com.example.appandroid.globalUtils.utils.BackgroundFilmCache;
import com.example.appandroid.globalUtils.utils.CacheLocale;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.segnalazione.Segnalazione;
import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.movieApi.MovieApi;
import com.example.appandroid.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Repository implements RepositoryService{
	private static Repository repositoryIstance ;
	private final ApiGateway apiGateway;
	private CacheLocale cache;
	private BackgroundFilmCache cacheFilm ;
	private String apiMovieDbKey;

	public static Repository getInstance(){
		if(repositoryIstance==null)
			repositoryIstance = new Repository();

		return repositoryIstance;
	}

	private Repository(){
		apiGateway = ApiGateway.getIstance();
		cache=CacheLocale.getInstance();
		cacheFilm = BackgroundFilmCache.getInstance();

		try {
			apiMovieDbKey = apiGateway.getMovieApiKey();
		} catch (JSONException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public boolean checkKey() throws TimeoutException, JSONException {
		if(apiMovieDbKey!=null)
			return true;
		apiMovieDbKey = apiGateway.getMovieApiKey();
		return apiMovieDbKey!=null;
	}

	/**
	 *
	 * @return Utente attualmente connesso
	 */
	public Utente getUser(){
		return Utente.getUtente();
	}

	//LISTA PERSONALIZZATA
	public boolean addLista(ListaPersonalizzata listaPersonalizzata, String email) throws JSONException, TimeoutException {
		if(email.equals(Utente.getUtente().getEmail())){
			cache.addListaCache(listaPersonalizzata.getIdLista(),listaPersonalizzata);
		}
		JSONObject jsonObject = apiGateway.addLista(listaPersonalizzata,email);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean removeLista(int idLista) throws JSONException, TimeoutException {
		cache.removeListaCache(idLista);
		JSONObject jsonObject = apiGateway.removeLista(idLista);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean addFilmInLista(int idLista, String idFilm) throws JSONException, TimeoutException {
		//AGGIORNAMENTO CACHE
		ListaPersonalizzata listaInCache = cache.getListaCache(idLista);
		List<String> elencoFilmInLista = listaInCache.getListaDiFilm();
		if(!elencoFilmInLista.contains(idFilm)) {
			elencoFilmInLista.add(idFilm);
			listaInCache.setFilmInLista(elencoFilmInLista);
			listaInCache.setnFilmContenuti(listaInCache.getnFilmContenuti() + 1);
			cache.updateListaCache(idLista, listaInCache);
		}

		JSONObject jsonObject = apiGateway.addFilmInLista(idLista,idFilm);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean removeFilmInLista(int idLista, String idFilm) throws JSONException, TimeoutException {

		//AGGIORNAMENTO CACHE
		ListaPersonalizzata listaInCache = cache.getListaCache(idLista);
		List<String> elencoFilmInLista = listaInCache.getListaDiFilm();
		elencoFilmInLista.remove(idFilm);
		listaInCache.setFilmInLista(elencoFilmInLista);
		listaInCache.setnFilmContenuti(listaInCache.getnFilmContenuti()-1);
		cache.updateListaCache(idLista,listaInCache);

		JSONObject jsonObject = apiGateway.removeFilmInLista(idLista,idFilm);
		return JSONObjectManipulator.getResponse(jsonObject);
	}
	public boolean cambiaImmagineLista(int idLista,String nuovaImmagineUrl) throws JSONException, TimeoutException {

		//AGGIORNAMENTO CACHE
		ListaPersonalizzata listaInCache = cache.getListaCache(idLista);
		listaInCache.setImmagineCopertina(nuovaImmagineUrl);
		cache.updateListaCache(idLista,listaInCache);

		JSONObject jsonObject = apiGateway.cambiaImmagineLista(idLista,nuovaImmagineUrl);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean cambiaDescrizioneLista(int idLista, String nuovaDescrizione) throws JSONException, TimeoutException {

		//AGGIORNAMENTO CACHE
		ListaPersonalizzata listaInCache = cache.getListaCache(idLista);
		listaInCache.setDescrizione(nuovaDescrizione);
		cache.updateListaCache(idLista,listaInCache);

		JSONObject jsonObject = apiGateway.cambiaDescrizioneLista(idLista,nuovaDescrizione);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public List<Film> getFilmInLista(int idLista) throws JSONException, TimeoutException {
		ListaPersonalizzata listaCache = cache.getListaCache(idLista);
		List<Film> filmList;

		if(listaCache != null){
			Log.d("getFilmInListaApiGatewa", "RECUPERO DALLA CACHE");

			JSONObject jsonObject = JSONObjectManipulator.createJSONOBjectByIdFilm(listaCache.getListaDiFilm());
			filmList = JSONObjectManipulator.getFilmDaJSONArray(jsonObject.getJSONArray("film"));
			filmList = ordinaLista(jsonObject.getJSONArray("film"),filmList);

		}
		else{
			Log.d("getFilmInListaApiGatewa", "CACHE E' VUOTA");
			JSONObject jsonObject = apiGateway.getFilmInLista(idLista);

			filmList = JSONObjectManipulator.getFilmDaJSONArray(jsonObject.getJSONArray("film"));
			for( Film film: filmList){
				cacheFilm.addFilmToCache(film.getIdFilm(), film);
			}

			filmList = ordinaLista(jsonObject.getJSONArray("film"),filmList);

			// Salvare nella cache lista
			// Ho id della lista ma ne' il proprietario ne' la descrizione non posso salvare nella cache informazioni parziali
			// se ho nella cache il film devo avere anche la lista che lo contiene

		}


		return filmList;

	}



	/**FILM**/
	public  boolean addFilm(String idFilm) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.addFilm(idFilm);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	/**NOTIFICA**/
	public boolean removeNotifica(int idNotifica) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.removeNotifica(idNotifica);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean addNotifica(Notifica notifica, String emailProprietario) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.addNotifica(notifica,emailProprietario);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public List<Notifica> getNotifiche(String email) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getNotificheUtente(email);
		List<Notifica> notifiche = JSONObjectManipulator.getNotificaDaJSONArray(jsonObject.getJSONArray("notifiche"));

		return notifiche;
	}

	public List<Notifica> getNotificheNonConsegnate(String email) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getNotificheUtenteNonConsegnate(email);
		List<Notifica> notifiche = JSONObjectManipulator.getNotificaDaJSONArray(jsonObject.getJSONArray("notifiche"));


		return notifiche;
	}

	public boolean setConsegnataNotifica(int idNotifica) throws JSONException, TimeoutException{
		JSONObject jsonObject = apiGateway.setConsegnataNotifica(idNotifica);
		return JSONObjectManipulator.getResponse(jsonObject);
	}


	/**SEGNALAZIONE**/
	public boolean addSegnalazione(Segnalazione segnalazione, int idListaSegnalata, String email) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.addSegnalazione(segnalazione,idListaSegnalata,email);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean removeSegnalazione(int idSegnalazione) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.removeSegnalazione(idSegnalazione);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	/**UTENTE**/
	public boolean insertUtente(Utente utente) throws JSONException, TimeoutException {
		JSONObject jsonObjectAddUtente = apiGateway.addUtente(utente);
		JSONObject jsonObjectInsertFilmDaGuardare = apiGateway.addLista(new ListaPersonalizzata("Film da guardare","Film da guardare"),utente.getEmail());

		return JSONObjectManipulator.getResponse(jsonObjectAddUtente) && JSONObjectManipulator.getResponse(jsonObjectInsertFilmDaGuardare);
	}

	public boolean isUsernameExist(String username) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.checkUsernameExist(username);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public Utente getUtente(String email,Context context) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getUtente(email);
		Utente utente = JSONObjectManipulator.getUtenteDaJSONObject(jsonObject.getJSONObject("utente"));

		utente.setBitmapImmagine(getBitmapImmagineUtenteQualsiasi(context,email));
		return utente;
	}

	public Utente getUtenteSenzaImmagine(String email) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getUtente(email);
		return JSONObjectManipulator.getUtenteDaJSONObject(jsonObject.getJSONObject("utente"));

	}

	public List<Utente> cercaUtenti(String email_utente_princiaple, String username_ricercato,Context context) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.cercaUtenti(email_utente_princiaple,username_ricercato);

		List<Utente> utenti = JSONObjectManipulator.getUtentiDaJSONArrayConCurrentStatus(jsonObject.getJSONArray("utenti"));

		return cercaImmaginiUtenti(utenti,context);

	}


	public List<Utente> cercaImmaginiUtenti(List<Utente> utenti,Context context){
		int size = utenti.size();
		Object sync = new Object();

		ThreadCercaImmaginiUtenti[] arrayThread = new ThreadCercaImmaginiUtenti[size];

		for(int i = 0 ; i < size ; i++){
			arrayThread[i] = new ThreadCercaImmaginiUtenti(utenti.get(i),sync,context);
		}

		for(int i = 0 ; i < size ; i++){
			arrayThread[i].start();
		}

		for(int i = 0; i < size; i++){
			try {
				arrayThread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}



		return utenti;
	}

	public List<ListaPersonalizzata> getListe(String email) throws JSONException, TimeoutException {
		List<ListaPersonalizzata> elencoListe = new ArrayList<>();

		/*if(email.equals(Utente.getUtente().getEmail()) && cache.getIdListe().size() > 0 ){
			long start = System.currentTimeMillis();
			List<Integer> listID = cache.getIdListe();
			for( int i = 0; i < listID.size(); i++){

				elencoListe.add(cache.getListaCache(listID.get(i)));

			}
			long end = System.currentTimeMillis();
			Log.d("performance", "tempo ms: " + (end-start));

		}
		else{*/
			JSONObject jsonObject = apiGateway.getListeDiUtente(email);
			elencoListe = JSONObjectManipulator.getListeDaJSONArray(jsonObject.getJSONArray("liste"));

			if(email.equals(Utente.getUtente().getEmail())) {
				for (ListaPersonalizzata lista : elencoListe) {
					cache.addListaCache(lista.getIdLista(), lista);
				}
			}
		//}


		Collections.sort(elencoListe, new Comparator<ListaPersonalizzata>() {
			@Override
			public int compare(ListaPersonalizzata o1, ListaPersonalizzata o2) {
				if(o1.getNome().equals("Film da guardare"))
					return -1;
				if(o2.getNome().equals("Film da guardare"))
					return 1;

				return o1.getNome().compareTo(o2.getNome());
			}
		});

		return elencoListe;
	}




	public List<Utente> getAmici(String email,Context context) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getAmici(email);
		List<Utente> amici = JSONObjectManipulator.getUtentiDaJSONArrayConCurrentStatus(jsonObject.getJSONArray("utenti"));

		return cercaImmaginiUtenti(amici,context);
	}

	public List<Utente> getRichiesteAmicizia(String email,Context context) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getRichiesteAmicizia(email);
		List<Utente> richiesteAmicizia = JSONObjectManipulator.getUtentiDaJSONArrayConCurrentStatus(jsonObject.getJSONArray("utenti"));

		return cercaImmaginiUtenti(richiesteAmicizia,context);
	}

	/**
	 * Restiusce
	 * 0 se sono amici
	 * 1 se non sono amici
	 * 2 se altroUtente ha inviato richiesta amicizia ad utenteLocale
	 * 3 se utenteLocale ha inviato richiesta amicizia ad altroUtente
	 * */
	public int getStatusAmicizia(String emailUtenteLocale, String emailAltroUtente) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getStatusAmicizia(emailUtenteLocale, emailAltroUtente);
		return jsonObject.getInt("response");
	}




	/**LEGAME TRA DUE UTENTI**/
	public boolean sonoAmici(String email1 , String email2) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.sonoAmici(email1,email2);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public List<Film> getFilmInComune(String email1, String email2) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.getFilmInComune(email1,email2);
		List<Film> filmInComune = JSONObjectManipulator.getFilmDaJSONArray(jsonObject.getJSONArray("Film"));
		filmInComune=ordinaLista(jsonObject.getJSONArray("Film"),filmInComune);
		return filmInComune;
	}

	public boolean addAmicizia (String email1 , String email2) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.addAmicizia(email1,email2);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean  addRichiestaAmicizia(String emailRichiede,String emailRiceve) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.addRichiestaAmicizia(emailRichiede,emailRiceve);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean  removeAmicizia(String email1 , String email2) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.removeAmicizia(email1,email2);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	public boolean  removeRichiestaAmicizia(String emailRichiede,String emailRiceve) throws JSONException, TimeoutException {
		JSONObject jsonObject = apiGateway.removeRichiestaAmicizia(emailRichiede,emailRiceve);
		return JSONObjectManipulator.getResponse(jsonObject);
	}

	/**Controlla se il film è presente nella cache
	 * Altrimenti lo cerca con le API
	 * */
	public Film getFilmById(String idFilm){
		Film film = cacheFilm.getFilmFromCache(idFilm);

		if(film!=null){
			return  film;
		}

		MovieApi ricercatore = new MovieApi(apiMovieDbKey);
		film = ricercatore.recuperaFilmEstesoById(idFilm);
		cacheFilm.addFilmToCache(idFilm,film);
		return film;

	}

	public List<Film> cercaFilmByTitolo(String titolo){

		MovieApi ricercatore = new MovieApi(apiMovieDbKey);
		return ricercatore.effettuaRicercaCompatta(titolo);

	}

	public Bitmap getBitmapImmagineUtente(Context context){
		BackgroundBitmapCache bitmapCache = BackgroundBitmapCache.getInstance();
		Bitmap bitmap =  bitmapCache.getBitmapFromBgMemCache(Utente.getUtente().getEmail());

		if(bitmap != null)
			return  bitmap;

		Bitmap bitmapUtente = getBitmapImmagineUtenteQualsiasi(context,Utente.getUtente().getEmail());
		bitmapCache.addBitmapToBgMemoryCache(Utente.getUtente().getEmail(), bitmapUtente);
		return bitmapUtente;
	}

	public Bitmap getBitmapImmagineUtenteQualsiasi(Context context, String email){
		S3Class s3Class = new S3Class(context);
		File mSaveBit = s3Class.getFile(email,email);
		String filePath	 = mSaveBit.getPath();
		Bitmap bitmapScaricato = BitmapFactory.decodeFile(filePath);

		return bitmapScaricato;
	}


	public Bitmap uplodadBitmapImmagineUtente(Context context, Uri uri){
		S3Class s3Class = new S3Class(context);

		new Thread(()->{s3Class.uploadFile(uri, Utente.getUtente().getEmail());}).start();

		Bitmap bitmap = null;
		try {

			bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			BackgroundBitmapCache backgroundBitmapCache = BackgroundBitmapCache.getInstance();
			backgroundBitmapCache.updateCache(Utente.getUtente().getEmail(),bitmap);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}


	/**
	 * @param  jsonArray contenente gli id dei film nell'ordine desiderato
	 * @param  listaNonOrdinata la lista di film che si desidera riordinare.
	 * @return un nuovo oggetto di tipo List<Film> contenente i film ordinati.
	 *
	 * Basandosi sugli id recuperati dal jsonArray ordina la listaNonOrdinata
	 * SE il jsonArray è null o vuoto viene restituita una lista vuota.
	 * SE listaNonOrdinata è null o vuoto viene restituita una lista vuota.
	 *
	 * @throws NullPointerException se listaNonOrdinata contiene riferimenti a null
	 * @throws JSONException se i jsonObject all'interno del jsonArray non contengono il campo "imdbid"
	 * */

	public List<Film> ordinaLista(JSONArray jsonArray, List<Film> listaNonOrdinata) throws JSONException {
		List<String> listaIdFilmOrdinati = JSONObjectManipulator.getListeIdFilmByJSONArray(jsonArray);
		List<Film> listaFilmOrdinata = new ArrayList<>();

		if(listaNonOrdinata != null && !listaNonOrdinata.isEmpty()){
			if(listaNonOrdinata.contains(null))
				throw new NullPointerException();

			for(String id : listaIdFilmOrdinati){
				Film film = listaNonOrdinata.stream().filter(o -> o.getIdFilm().equals(id)).findAny().orElse(null);
				listaFilmOrdinata.add(film);
			}
		}

		return listaFilmOrdinata;
	}



	public void clearCache(){
		cache.clearCacheList();
	}



	private class ThreadCercaImmaginiUtenti extends Thread {
		final Utente utente ;

		final Object sync ;
		final Context context;

		public ThreadCercaImmaginiUtenti(Utente utente, Object sync, Context context) {
			this.utente=utente;

			this.sync=sync;
			this.context = context;
		}

		@Override
		public void run() {
			utente.setBitmapImmagine(getBitmapImmagineUtenteQualsiasi(context,utente.getEmail()));
		}

	}

	public void initUtenteLocaleConApiGateway(String email) throws TimeoutException, JSONException {
		Utente utente = getUtenteSenzaImmagine(email);
		Utente.initUtente(utente.getUsername(), utente.getEmail());
	}

	public void initUtenteLocaleSenzaApi(String email, String username) throws TimeoutException, JSONException {
		Utente.initUtente(username,email);
	}

	public boolean effettuaLogin(String email, String password){
		AtomicBoolean risposta = new AtomicBoolean(false);
		AtomicBoolean flag = new AtomicBoolean(false);

		Amplify.Auth.signIn(email, password,
				result -> {
					if (result.isSignInComplete()) {
						synchronized (flag){
							risposta.set(true);
							flag.set(true);
							flag.notify();

						}
					} else {
						System.out.println("Errore AWS COGNITO LOGIN");
						synchronized (flag){
							risposta.set(false);
							flag.set(true);
							flag.notify();
						}
					}
				},
				error -> {
					Log.e("AuthQuickstart", error.toString());
					synchronized (flag) {
						risposta.set(false);
						flag.set(true);
						flag.notify();
					}
				}
		);

		Thread td = new Thread(() -> {
			synchronized(flag){
				while(flag.get() == false ){
					try {
						flag.wait();
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		});

		td.start();

		try {
			td.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return risposta.get();
	}

	public boolean effettuaRegistrazioneCognito(String email, String username, String password){
		AtomicBoolean risposta = new AtomicBoolean(false);
		AtomicBoolean flag = new AtomicBoolean(false);

		AuthSignUpOptions options = AuthSignUpOptions.builder()
				.userAttribute(AuthUserAttributeKey.email(), email)
				.userAttribute(AuthUserAttributeKey.nickname(), username)
				.build();

		Amplify.Auth.signUp(email, password, options,
				result -> {
					synchronized (flag){
						risposta.set(true);
						flag.set(true);
						flag.notify();
					}
					Log.i("SUCCESSO SIGNUP COGNITO",result.toString());
				},
				error -> {
					synchronized (flag){
						risposta.set(false);
						flag.set(true);
						flag.notify();

					}
					Log.e("Error signUp",error.getMessage());
				}
		);

		Thread td = new Thread(() -> {
			synchronized(flag){
				while(flag.get() == false ){
					try {
						flag.wait();
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		});

		td.start();

		try {
			td.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return risposta.get();

	}

	public boolean confermaCodiceRegistrazione(String email, String codice) {
		AtomicBoolean risposta = new AtomicBoolean(false);
		AtomicBoolean flag = new AtomicBoolean(false);
		Amplify.Auth.confirmSignUp(
				email,
				codice,
				result -> {
					synchronized (flag){
						risposta.set(true);
						flag.set(true);
						flag.notify();
					}
					Log.i("SUCCESSO SIGNUP COGNITO",result.toString());
				},
				error -> {
					synchronized (flag){
						risposta.set(false);
						flag.set(true);
						flag.notify();

					}
					Log.e("Error confirmSignup",error.getMessage());
				}
		);

		Thread td = new Thread(() -> {
			synchronized(flag){
				while(flag.get() == false ){
					try {
						flag.wait();
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		});

		td.start();

		try {
			td.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return risposta.get();
	}

	public boolean effettuaRichiestaCambioPassword(String oldPassword, String newPassword){
		AtomicBoolean risposta = new AtomicBoolean(false);
		AtomicBoolean flag = new AtomicBoolean(false);
		Amplify.Auth.updatePassword(
				oldPassword,
				newPassword,
				() -> {
					synchronized (flag){
						risposta.set(true);
						flag.set(true);
						flag.notify();
					}
					Log.i("Amplify updatePassword", "Updated password successfully");
				},
				error -> {
					synchronized (flag) {
						risposta.set(false);
						flag.set(true);
						flag.notify();
					}
					Log.e("Amplify updatePassword", error.toString());

				}
		);


		Thread td = new Thread(() -> {
			synchronized(flag){
				while(flag.get() == false ){
					try {
						flag.wait();
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		});

		td.start();

		try {
			td.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return risposta.get();
	}

	public boolean resetPasswordPrimoStep(String email){
		AtomicBoolean risposta = new AtomicBoolean(false);
		AtomicBoolean flag = new AtomicBoolean(false);



		Thread authThread = new Thread(()->{

			Amplify.Auth.resetPassword(
					email,
					result -> {
						synchronized (flag) {
							risposta.set(true);
							flag.set(true);
							flag.notify();

						}
						Log.i("AuthQuickstart", result.toString());
					},
					error -> {
						synchronized (flag) {
							risposta.set(false);
							flag.set(true);
							flag.notify();

						}
						Log.e("AuthQuickstart", error.toString());
					}
			);

		});

		authThread.start();

		synchronized(flag){
			while(flag.get() == false ){
				try {

					flag.wait();


				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return risposta.get();
	}

	public int resetPasswordSecondoStep(String codice, String newPassword) {
		int OK = 0;
		int CODICE_ERRATO = 1;
		int MANCANTI_NON_RISPETTATI = 2;


		AtomicInteger risposta = new AtomicInteger(OK);
		AtomicBoolean flag = new AtomicBoolean(false);

		Thread authThread = new Thread(()->{
			Amplify.Auth.confirmResetPassword(
					newPassword,
					codice,
					() -> {
						Log.i("AuthQuickstart", "New password confirmed");
						synchronized (flag){
							risposta.set(OK);
							flag.set(true);
							flag.notify();
						}
					},
					error -> {
						if(newPassword.equals("") || codice.equals("") || newPassword.length()>30 || newPassword.length()<8){
							synchronized (flag){
								risposta.set(MANCANTI_NON_RISPETTATI);
								flag.set(true);
								flag.notify();
							}
						}
						else{
							synchronized (flag){
								risposta.set(CODICE_ERRATO);
								flag.set(true);
								flag.notify();
							}
						}
						Log.e("AuthQuickstart", error.toString());

					}
			);
		});

		authThread.start();

		synchronized(flag){
			while(flag.get() == false ){
				try {
					flag.wait();
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return risposta.get();
	}

	public void logout(Context context){
		clearCache();
		Utente.logout(context);
	}

}
