package com.example.appandroid.listViewClass.segnalazione;

import java.util.Random;

public class Segnalazione {
	private final int idSegnalazione ;
	private final String motivoSegnalazione ;



	public Segnalazione(int idSegnalazione, String motivoSegnalazione) {
		this.idSegnalazione = idSegnalazione;
		this.motivoSegnalazione = motivoSegnalazione;

	}

	public Segnalazione(String motivoSegnalazione){
		this((int)(System.nanoTime() ^ new Random().nextInt()),motivoSegnalazione);
	}

	public int getIdSegnalazione() {
		return idSegnalazione;
	}

	public String getMotivoSegnalazione() {
		return motivoSegnalazione;
	}
}
