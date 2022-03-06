package com.example.appandroid.movieApi;

import org.junit.Assert;
import org.junit.Test;

public class MovieApiTest {
	String urlBase = "http://www.omdbapi.com/?&type=movie";
	MovieApi movieApi = new MovieApi("");

	/**
	 * Input titoloDaRicercare è una stringa che rappresenta il parametro "title" da inserire
	 * nella richiesta da effettuare alla classe MovieApi
	 * paginaDaVisualizzare è riferito al numero di pagina che si vuole visualizzare tra i risultati
	 * output ritorna un oggetto String che rappresenta l'url normalizzato
	 * (Senza spazi prima e dopo il titolo, al posto degli spazi %20) da passare al client per effettuare la richiesta
	 * Ritorna NULL nel caso in cui titolo da ricercare == NULL
	 * THROW IllegalArgumentException nel caso in cui paginaDaVisualizzare<0
	 */

	@Test
	public void generaUrlByTitoloPulitoSenzaSpaziWithMax() {
		String titoloTest = "titolo1";
		int pagina = Integer.MAX_VALUE;

		String urlExcpected = urlBase.concat("&s=" + titoloTest);
		urlExcpected = urlExcpected.concat("&page=" + pagina);

		String urlCostruito = movieApi.generaUrlByTitolo("titolo1", Integer.MAX_VALUE);

		Assert.assertEquals(urlExcpected, urlCostruito);
	}

	@Test
	public void generaUrlByTitoloPulitoSenzaSpaziWith0() {
		String titoloTest = "titolo1";
		int pagina = 0;

		String urlExcpected = urlBase.concat("&s=" + titoloTest);
		urlExcpected = urlExcpected.concat("&page=" + pagina);

		String urlCostruito = movieApi.generaUrlByTitolo("titolo1", 0);

		Assert.assertEquals(urlExcpected, urlCostruito);
	}

	@Test
	public void generaUrlByTitoloPulitoConSpazi() {
		String titoloTest = "tito%20lo1";
		int pagina = 10;

		String urlExcpected = urlBase.concat("&s=" + titoloTest);
		urlExcpected = urlExcpected.concat("&page=" + pagina);

		String urlCostruito = movieApi.generaUrlByTitolo("  tito lo1 ", 10);

		Assert.assertEquals(urlExcpected, urlCostruito);
	}


	@Test
	public void generaUrlByTitoloTitoloNull() {
		int pagina = 10;
		Assert.assertNull(movieApi.generaUrlByTitolo(null, pagina));
	}
}