package com.example.appandroid.repository;

import static org.junit.Assert.*;

import com.example.appandroid.listViewClass.film.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RepositoryTest {
	Repository repository = Repository.getInstance();

	Film filmTest1 ;
	Film filmTest2 ;
	Film filmTest3 ;
	Film filmTest4 ;

	JSONObject jsonObjectFilm1;
	JSONObject jsonObjectFilm2;
	JSONObject jsonObjectFilm3;
	JSONObject jsonObjectFilm4;
	JSONObject jsonObjectFilmWrongField;


	JSONArray jsonArrayTestClassic ;
	JSONArray jsonArrayTestEmpty ;
	JSONArray jsonArrayTestWithWrongField;
	JSONArray jsonArrayTestNull ;

	List<Film> listaFilmTestClassic ;
	List<Film> listaFilmTestEmpty;
	List<Film> listaFilmTestWithNullReference;
	List<Film> listaFilmTestNull;


	public void configureJsonArray(){
		jsonArrayTestClassic = new JSONArray();

		jsonArrayTestClassic.put(jsonObjectFilm1);
		jsonArrayTestClassic.put(jsonObjectFilm2);
		jsonArrayTestClassic.put(jsonObjectFilm3);
		jsonArrayTestClassic.put(jsonObjectFilm4);

		jsonArrayTestEmpty = new JSONArray();

		jsonArrayTestNull = null ;

		jsonArrayTestWithWrongField =new JSONArray();
		jsonArrayTestWithWrongField.put(jsonObjectFilmWrongField);
	}

	public void configureListFilm(){
		listaFilmTestClassic= new ArrayList<>();
		listaFilmTestClassic.add(filmTest4);
		listaFilmTestClassic.add(filmTest3);
		listaFilmTestClassic.add(filmTest2);
		listaFilmTestClassic.add(filmTest1);

		listaFilmTestEmpty= new ArrayList<>();

		listaFilmTestNull=null;

		listaFilmTestWithNullReference=new ArrayList<>();
		listaFilmTestWithNullReference.add(filmTest1);
		listaFilmTestWithNullReference.add(null);
		listaFilmTestWithNullReference.add(filmTest2);
	}

	public void configureJSONObject() throws JSONException {
		jsonObjectFilm1 = new JSONObject();
		jsonObjectFilm2 = new JSONObject();
		jsonObjectFilm3 = new JSONObject();
		jsonObjectFilm4 = new JSONObject();
		jsonObjectFilmWrongField = new JSONObject();

		jsonObjectFilm1.put("imdbid","100");
		jsonObjectFilm2.put("imdbid","200");
		jsonObjectFilm3.put("imdbid","300");
		jsonObjectFilm4.put("imdbid","400");
		jsonObjectFilmWrongField.put("idWrong","500");
	}

	public void configureFilm(){
		filmTest1 = new Film("NameTest", "100", "urlTest1");
		filmTest2 = new Film("NameTest2", "200", "urlTest2");
		filmTest3 = new Film("NameTest3", "300", "urlTest3");
		filmTest4 = new Film("NameTest4", "400", "urlTest4");
	}

	public void setupTest() throws JSONException {
		configureJSONObject();
		configureFilm();
		configureJsonArray();
		configureListFilm();

	}

	@Test
	public void ordinaListaA1_B1() throws JSONException {
		setupTest();

		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestClassic,listaFilmTestClassic);
		assertEquals("100", listaOttenuta.get(0).getIdFilm());
		assertEquals("200", listaOttenuta.get(1).getIdFilm());
		assertEquals("300", listaOttenuta.get(2).getIdFilm());
		assertEquals("400", listaOttenuta.get(3).getIdFilm());
	}

	@Test
	public void ordinaListaA1_B2() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestClassic, listaFilmTestEmpty);
		assertTrue(listaOttenuta.isEmpty());
	}

	@Test
	public void ordinaListaA1_B3() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestNull, listaFilmTestNull);
		assertTrue(listaOttenuta.isEmpty());
	}

	@Test (expected = NullPointerException.class)
	public void ordinaListaA1_B4() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestClassic, listaFilmTestWithNullReference);
	}

	@Test
	public void ordinaListaA2_B1() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestEmpty, listaFilmTestClassic);
		assertTrue(listaOttenuta.isEmpty());
	}

	@Test
	public void ordinaListaA2_B2() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestEmpty, listaFilmTestEmpty);
		assertTrue(listaOttenuta.isEmpty());
	}

	@Test
	public void ordinaListaA2_B3() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestEmpty, listaFilmTestNull);
		assertTrue(listaOttenuta.isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void ordinaListaA2_B4() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestEmpty, listaFilmTestWithNullReference);
	}

	@Test
	public void ordinaListaA3_B1() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestNull, listaFilmTestClassic);
		assertTrue(listaOttenuta.isEmpty());
	}

	@Test
	public void ordinaListaA3_B2() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestNull, listaFilmTestEmpty);
		assertTrue(listaOttenuta.isEmpty());
	}
	@Test
	public void ordinaListaA3_B3() throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestNull, listaFilmTestNull);
		assertTrue(listaOttenuta.isEmpty());
	}
	@Test (expected = NullPointerException.class)
	public void ordinaListaA3_B4() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestNull, listaFilmTestWithNullReference );
	}

	@Test (expected = JSONException.class)
	public void ordinaListaA4_B1() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestWithWrongField,listaFilmTestClassic);
	}

	@Test (expected = JSONException.class)
	public void ordinaListaA4_B2() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestWithWrongField,listaFilmTestEmpty);
	}

	@Test (expected = JSONException.class)
	public void ordinaListaA4_B3() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestWithWrongField,listaFilmTestNull);
	}

	@Test (expected = JSONException.class)
	public void ordinaListaA4_B4() throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestWithWrongField,listaFilmTestWithNullReference);
	}


	@Test
	public void ordinaLista_branch_3_4_6 () throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestClassic,listaFilmTestNull);
		Assert.assertTrue(listaOttenuta.isEmpty());
	}

	@Test (expected = NullPointerException.class)
	public void ordinaLista_branch_3_4_6_7_8 () throws JSONException {
		setupTest();
		repository.ordinaLista(jsonArrayTestClassic,listaFilmTestWithNullReference);
	}

	@Test
	public void ordinaLista_branch_3_4_6_7_10_11_12 ()throws JSONException {
		setupTest();
		List<Film> listaOttenuta = repository.ordinaLista(jsonArrayTestClassic,listaFilmTestClassic);
		assertEquals("100", listaOttenuta.get(0).getIdFilm());
		assertEquals("200", listaOttenuta.get(1).getIdFilm());
		assertEquals("300", listaOttenuta.get(2).getIdFilm());
		assertEquals("400", listaOttenuta.get(3).getIdFilm());
	}



}