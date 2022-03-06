 package com.example.appandroid.globalUtils.utils;


import android.util.LruCache;

import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.listViewClass.utente.Utente;

import java.util.ArrayList;
import java.util.List;

/**
 * LruCache for caching background bitmaps for {@link DecodeBitmapTask}.
 */
public class CacheLocale {
	private LruCache<Integer, ListaPersonalizzata> cacheLocaleListe;



	private static CacheLocale instance;


	public static CacheLocale getInstance() {
		if (instance == null) {
			instance = new CacheLocale();
			instance.init();
		}
		return instance;
	}

	private void init() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 5;

		cacheLocaleListe = new LruCache<>(cacheSize);

	}

	/*LISTE UTENTE*/
	public List<Integer> getIdListe(){
		return new ArrayList<>(cacheLocaleListe.snapshot().keySet());
	}

	public void addListaCache(Integer key, ListaPersonalizzata listaPersonalizzata) {
		if (getListaCache(key) == null && key != null && listaPersonalizzata != null) {
			cacheLocaleListe.put(key, listaPersonalizzata);
		}
	}

	public void updateListaCache(Integer key, ListaPersonalizzata listaPersonalizzata){
		if ( key != null && listaPersonalizzata != null && getListaCache(key) != null)
			cacheLocaleListe.put(key, listaPersonalizzata);
	}

	public ListaPersonalizzata getListaCache(Integer key) {
		return cacheLocaleListe.get(key);
	}

	public void removeListaCache(Integer key){
		cacheLocaleListe.remove(key);
	}

	public void clearCacheList(){
		List<Integer> idList = getIdListe();
		for(Integer id : idList){
			removeListaCache(id);
		}
	}
}
