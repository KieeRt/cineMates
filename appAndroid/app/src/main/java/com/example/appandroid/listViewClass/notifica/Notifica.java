package com.example.appandroid.listViewClass.notifica;

import java.util.Random;

/**
 * classe usata per generare le possibili notifiche, durante aggiunga/riufiuto/accettazione o richiesta di amicizia
 *
 */
public class Notifica {
	private String messaggio;
	private final int idNotifica ;



	/**
	 * Utilizzare nel caso di costruzione standart del messaggio, fa riferimento a {@link Build_message}
	 * @param username_soggetto il soggeto della frase che compie azione
	 * @param tipo_notifica addatta il testo in base al tipo di notifica, controllare  {@link Build_message}
	 */
	public Notifica(String username_soggetto, int tipo_notifica) {
		// TODO: risolvere il dilema con immagine notifica
		this.messaggio = new Build_message(tipo_notifica, username_soggetto).getMessaggio();
		this.idNotifica = (int)( System.nanoTime() ^ new Random().nextInt() );
	}
	/**
	*Utilizzare quando il messaggio e' stato gia' costruito
	*
	 */
	public Notifica(int idNotifica, String messaggio) {
		// TODO: risolvere il dilema con immagine notifica
		this.messaggio = messaggio;
		this.idNotifica = idNotifica;
	}



	public int getIdNotifica() {
		return idNotifica;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio){
		this.messaggio = messaggio;
	}




	public static class Build_message {
		public int tipo_notifica;
		public String soggetto;
		public static final int UTENTE_HA_ACCETTATO_RICHIESTA = 0;
		public static final int UTENTE_HA_RIFIUTATO_RICHIESTA = 1;
		public static final int UTENTE_HA_INVIATO_RICHIESTA = 2;
		public String messaggio;

		public Build_message(int tipo_notifica, String username_soggetto) throws IllegalArgumentException {
			if(tipo_notifica < 0 || tipo_notifica > 2){
				throw new IllegalArgumentException();
			}
			this.tipo_notifica = tipo_notifica;
			this.soggetto = username_soggetto;
			set_messaggio();
		}

		private void set_messaggio(){
			if(tipo_notifica == UTENTE_HA_ACCETTATO_RICHIESTA){
				messaggio = "Utente " + soggetto + " ha accettato la tua richiesta di amicizia.";
			}
			if(tipo_notifica == UTENTE_HA_RIFIUTATO_RICHIESTA){
				messaggio = "Utente " + soggetto + " ha rifiutato la tua richiesta di amicizia.";
			}
			if(tipo_notifica == UTENTE_HA_INVIATO_RICHIESTA){
				messaggio = "Utente " + soggetto + " ti ha inviato la richiesta di amicizia.";
			}
		}

		public String getMessaggio() {
			return messaggio;
		}
	}


}
