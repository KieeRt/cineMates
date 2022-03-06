package com.example.appandroid.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.segnalazione.Segnalazione;
import com.example.appandroid.listViewClass.utente.Utente;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface RepositoryService {
	 boolean checkKey() throws TimeoutException, JSONException;

	/**
	 *
	 * @return Utente attualmente connesso
	 */
	 Utente getUser();

	//LISTA PERSONALIZZATA
	 boolean addLista(ListaPersonalizzata listaPersonalizzata, String email) throws JSONException, TimeoutException;

	 boolean removeLista(int idLista) throws JSONException, TimeoutException;

	 boolean addFilmInLista(int idLista, String idFilm) throws JSONException, TimeoutException;

	 boolean removeFilmInLista(int idLista, String idFilm) throws JSONException, TimeoutException;

	 boolean cambiaImmagineLista(int idLista,String nuovaImmagineUrl) throws JSONException, TimeoutException;

	 boolean cambiaDescrizioneLista(int idLista, String nuovaDescrizione) throws JSONException, TimeoutException;

	 List<Film> getFilmInLista(int idLista) throws JSONException, TimeoutException;


	/**FILM**/
	  boolean addFilm(String idFilm) throws JSONException, TimeoutException;

	/**NOTIFICA**/
	 boolean removeNotifica(int idNotifica) throws JSONException, TimeoutException;

	 boolean addNotifica(Notifica notifica, String emailProprietario) throws JSONException, TimeoutException;

	 List<Notifica> getNotifiche(String email) throws JSONException, TimeoutException;

	 List<Notifica> getNotificheNonConsegnate(String email) throws JSONException, TimeoutException;

	 boolean setConsegnataNotifica(int idNotifica) throws JSONException, TimeoutException;


	/**SEGNALAZIONE**/
	 boolean addSegnalazione(Segnalazione segnalazione, int idListaSegnalata, String email) throws JSONException, TimeoutException;

	 boolean removeSegnalazione(int idSegnalazione) throws JSONException, TimeoutException;

	/**UTENTE**/
	 boolean insertUtente(Utente utente) throws JSONException, TimeoutException;

	 boolean isUsernameExist(String username) throws JSONException, TimeoutException;

	 Utente getUtente(String email, Context context) throws JSONException, TimeoutException;

	 Utente getUtenteSenzaImmagine(String email) throws JSONException, TimeoutException;

	 List<Utente> cercaUtenti(String email_utente_princiaple, String username_ricercato,Context context) throws JSONException, TimeoutException;

	 List<ListaPersonalizzata> getListe(String email) throws JSONException, TimeoutException;

	 List<Utente> getAmici(String email,Context context) throws JSONException, TimeoutException;

	 List<Utente> getRichiesteAmicizia(String email,Context context) throws JSONException, TimeoutException;

	/**
	 * Restiusce
	 * 0 se sono amici
	 * 1 se non sono amici
	 * 2 se altroUtente ha inviato richiesta amicizia ad utenteLocale
	 * 3 se utenteLocale ha inviato richiesta amicizia ad altroUtente
	 * */
	 int getStatusAmicizia(String emailUtenteLocale, String emailAltroUtente) throws JSONException, TimeoutException;

	/**LEGAME TRA DUE UTENTI**/
	 boolean sonoAmici(String email1 , String email2) throws JSONException, TimeoutException;

	 List<Film> getFilmInComune(String email1, String email2) throws JSONException, TimeoutException;

	 boolean addAmicizia (String email1 , String email2) throws JSONException, TimeoutException;

	 boolean  addRichiestaAmicizia(String emailRichiede,String emailRiceve) throws JSONException, TimeoutException;

	 boolean  removeAmicizia(String email1 , String email2) throws JSONException, TimeoutException;

	 boolean  removeRichiestaAmicizia(String emailRichiede,String emailRiceve) throws JSONException, TimeoutException;

	 Film getFilmById(String idFilm);

	 List<Film> cercaFilmByTitolo(String titolo);

	 Bitmap getBitmapImmagineUtente(Context context);

	 Bitmap getBitmapImmagineUtenteQualsiasi(Context context, String email);

	 Bitmap uplodadBitmapImmagineUtente(Context context, Uri uri);

	 void clearCache();

	 void initUtenteLocaleConApiGateway(String email) throws TimeoutException, JSONException;

	 void initUtenteLocaleSenzaApi(String email, String username) throws TimeoutException, JSONException;

	 boolean effettuaLogin(String email, String password);

	 boolean effettuaRegistrazioneCognito(String email, String username, String password);

	 boolean confermaCodiceRegistrazione(String email, String codice);

	 boolean effettuaRichiestaCambioPassword(String oldPassword, String newPassword);

	 boolean resetPasswordPrimoStep(String email);

	 int resetPasswordSecondoStep(String codice, String newPassword);

	 void logout(Context context);

}
