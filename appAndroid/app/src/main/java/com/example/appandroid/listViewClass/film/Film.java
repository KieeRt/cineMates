package com.example.appandroid.listViewClass.film;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Film implements Serializable {
	private final String nome;
	private final String trama;
	private final String regista;
	private final String durata;
	private final String genere;
	private final String valutazione;
	private final String immagineCopertinaURL;
	private final String idFilm;


	public Film(String nome, String trama, String regista, String durata, String valutazione, String immagineCopertinaURL,  String genere, String idFilm) {
		this.nome = nome;
		this.trama = trama;
		this.regista = regista;
		this.durata = durata;
		this.valutazione = valutazione;
		this.immagineCopertinaURL = immagineCopertinaURL;
		this.genere = genere;
		this.idFilm = idFilm;
	}

	public Film(String nome, String idFilm, String immagineCopertinaURL){
		this(nome,null,null,null,null,immagineCopertinaURL,null,idFilm);
	}

	public String getIdFilm() {
		return idFilm;
	}

	public String getNome() {
		return nome;
	}

	public String getTrama() {
		return trama;
	}

	public String getRegista() {
		return regista;
	}

	public String getDurata() {
		return durata;
	}

	public String getValutazione() {
		return valutazione;
	}

	public String getImmagineCopertina() {
		return immagineCopertinaURL;
	}

	public String getGenere() {
		return genere;
	}

	public static void stampaFilm(Film film){
		System.out.println("idFilm:" + film.getIdFilm());
		System.out.println("Titolo: " + film.getNome());
		System.out.println("Trama:" + film.getTrama());
		System.out.println("Duranta: "+ film.getDurata());
		System.out.println("Valutatione: " + film.getValutazione());
		System.out.println("ImmagineCopertina:" + film.getImmagineCopertina());
		System.out.println("Genere: " + film.getGenere());

	}



	@Override
	public boolean equals(@Nullable Object obj) {
		if(obj==null)
			return false;

		if(!(obj instanceof Film))
			return false;


		return ((Film)obj).getIdFilm().equals(idFilm);
	}
}
