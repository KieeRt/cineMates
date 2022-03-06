package com.example.appandroid.globalUtils.utils;


import android.util.LruCache;

import com.example.appandroid.listViewClass.film.Film;


public class BackgroundFilmCache {
	private LruCache<String, Film> mBackgroundsCache;

	private static BackgroundFilmCache instance;

	public static BackgroundFilmCache getInstance() {
		if (instance == null) {
			instance = new BackgroundFilmCache();
			instance.init();
		}
		return instance;
	}

	private void init() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 5;

		mBackgroundsCache = new LruCache<>(cacheSize);
	}

	public void addFilmToCache(String key, Film film) {
		if (getFilmFromCache(key) == null && key != null && film != null) {
			mBackgroundsCache.put(key, film);
		}
	}

	public Film getFilmFromCache(String key) {
		return mBackgroundsCache.get(key);
	}

}
