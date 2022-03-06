package com.example.appandroid.listViewClass.listaPersonalizzata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListaPersonalizzata implements Serializable {
	private String nome;
	private String descrizione ;
	private List<String> listaDiFilm ;
	private String immagineCopertina ;
	private int nFilmContenuti;
	private int idLista;
	private boolean censored;

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setImmagineCopertina(String immagineCopertina) {
		this.immagineCopertina = immagineCopertina;
	}

	public void setnFilmContenuti(int nFilmContenuti) {
		this.nFilmContenuti = nFilmContenuti;
	}

	public ListaPersonalizzata(String nome, String descrizione, List<String> listaDiFilm, String immagineCopertina, int nFilmContenuti, int idLista, boolean censored) {
		this.nome = nome;
		this.descrizione = descrizione;
		this.listaDiFilm = listaDiFilm;
		this.immagineCopertina = immagineCopertina;
		this.nFilmContenuti = nFilmContenuti;
		this.idLista = idLista;
		this.censored = censored;
	}


	public ListaPersonalizzata(String nome, String descrizione, List<String> listaDiFilm,String immagineCopertina, int nFilmContenuti, boolean censored) {
		this(nome,descrizione,listaDiFilm,immagineCopertina,nFilmContenuti, (int)(System.nanoTime() ^ new Random().nextInt()),censored);
	}

	public ListaPersonalizzata(String nome,String descrizione){
		this(nome,descrizione,new ArrayList<>(),null,0,false);
	}

	public int getnFilmContenuti() {
		return nFilmContenuti;
	}

	public String getNome() {
		return nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public List<String> getListaDiFilm() {
		return listaDiFilm;
	}

	public String getImmagineCopertina() {
		return immagineCopertina;
	}

	public boolean isCensored() {
		return censored;
	}

	public int getIdLista() {
		return idLista;
	}

	public void setFilmInLista(List<String> elencoFilm) {
		this.listaDiFilm=elencoFilm;
	}
	public boolean filmIsInLista(String idFilm){
		 if(listaDiFilm!=null){
			 for(String id : listaDiFilm){
				 if(id.equals(idFilm))
					 return true;
			 }
		 }
		return false;
	}

	public static void stampaLista(ListaPersonalizzata listaPersonalizzata){
		System.out.println("LISTA");
		System.out.println("id : "+listaPersonalizzata.getIdLista());
		System.out.println("nome : "+listaPersonalizzata.getNome());
		System.out.println("descrizione : "+listaPersonalizzata.getDescrizione());


		if(listaPersonalizzata.getListaDiFilm()!=null){
			List<String> elencoIdFilm = listaPersonalizzata.getListaDiFilm();
			for(String idFilm  : elencoIdFilm){
				System.out.println("FILM : "+idFilm);
			}
		}
	}
}
