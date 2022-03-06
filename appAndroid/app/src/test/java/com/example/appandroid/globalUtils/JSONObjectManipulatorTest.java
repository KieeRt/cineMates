package com.example.appandroid.globalUtils;

import static org.junit.Assert.*;

import com.example.appandroid.listViewClass.utente.Utente;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JSONObjectManipulatorTest {
	JSONObject jsonObjectClassic;
	JSONObject jsonObjectEmpty;
	JSONObject jsonObjectNull;
	JSONObject jsonObjectWithWrongField;
	JSONObject jsonObjectWithWrongFieldUsername;
	JSONObject jsonObjectWithWrongFieldEmail;
	JSONObject jsonObjectWithWrongFieldCurrentStatus;
	String usernameExpected = "usernameTest";
	String emailExpected = "emailTest";
	int currentStatusExpected = Utente.UTENTE_AMICO;


	public void configureJSONObject() throws JSONException {
		jsonObjectClassic = new JSONObject();
		jsonObjectClassic.put("username",usernameExpected);
		jsonObjectClassic.put("email",emailExpected);
		jsonObjectClassic.put("CURRENT_STATE",currentStatusExpected);


		jsonObjectEmpty = new JSONObject();

		jsonObjectNull = null;

		jsonObjectWithWrongField = new JSONObject();
		jsonObjectWithWrongField.put("usernmaleleko",usernameExpected);
		jsonObjectWithWrongField.put("emailefofeh",emailExpected);
		jsonObjectWithWrongField.put("oeoeoeoeoe",currentStatusExpected);



	}

	public void configureWrongJSONObject() throws JSONException {
		jsonObjectWithWrongFieldEmail = new JSONObject();
		jsonObjectWithWrongFieldEmail.put("username",usernameExpected);
		jsonObjectWithWrongFieldEmail.put("emailWRONG",emailExpected);
		jsonObjectWithWrongFieldEmail.put("CURRENT_STATE",currentStatusExpected);


		jsonObjectWithWrongFieldCurrentStatus = new JSONObject();
		jsonObjectWithWrongFieldCurrentStatus.put("username",usernameExpected);
		jsonObjectWithWrongFieldCurrentStatus.put("email",emailExpected);
		jsonObjectWithWrongFieldCurrentStatus.put("CURRENT_STATE_WRONG",currentStatusExpected);

		jsonObjectWithWrongFieldUsername = new JSONObject();
		jsonObjectWithWrongFieldUsername.put("usernameWrong",usernameExpected);
		jsonObjectWithWrongFieldUsername.put("email",emailExpected);
		jsonObjectWithWrongFieldUsername.put("CURRENT_STATE",currentStatusExpected);

	}



	/**
	 * INPUT : jsonObject contenente le informazioni di un utente con il legame di amicizia che ha con l'utente
	 * attuale.
	 * OUTPUT : Un oggetto di tipo Utente corrispondente alle informazioni ricavate dal jsonObject in input
	 * nel caso in cui il jsonObject sia null o vuoto viene restituto null.
	 * THROW : JSONException nel caso in cui il json object non abbia la struttura corretta
	 * */

	@Test
	public void getUtenteDaJSONObjectConCurrentStatusA1() throws JSONException {
		configureJSONObject();
		Utente utente = JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectClassic);

		Assert.assertEquals(usernameExpected,utente.getUsername());
		Assert.assertEquals(emailExpected,utente.getEmail());
		Assert.assertEquals(currentStatusExpected,utente.getCURRENT_STATE());
	}

	@Test
	public void getUtenteDaJSONObjectConCurrentStatusA2() throws JSONException {
		configureJSONObject();
		Assert.assertNull(JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectEmpty));
	}

	@Test
	public void getUtenteDaJSONObjectConCurrentStatusA3() throws JSONException {
		configureJSONObject();
		Assert.assertNull(JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectNull));
	}

	@Test(expected = JSONException.class)
	public void getUtenteDaJSONObjectConCurrentStatusA4() throws JSONException {
		configureJSONObject();
		Assert.assertNull(JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectWithWrongField));
	}


	@Test
	public void getUtenteDaJSON_branch_3_5() throws JSONException {
		configureJSONObject();
		Assert.assertNull(JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectNull));
	}

	@Test(expected = JSONException.class)
	public void getUtenteDaJSON_branch_3_5_6() throws JSONException {
		configureWrongJSONObject();
		JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectWithWrongFieldUsername);
	}

	@Test(expected = JSONException.class)
	public void getUtenteDaJSON_branch_3_5_6_7() throws JSONException {
		configureWrongJSONObject();
		JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectWithWrongFieldEmail);
	}

	@Test(expected = JSONException.class)
	public void getUtenteDaJSON_branch_3_5_6_7_8() throws JSONException {
		configureWrongJSONObject();
		JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectWithWrongFieldCurrentStatus);
	}


	@Test
	public void getUtenteDaJSON_branch_3_5_6_7_10() throws JSONException {
		configureJSONObject();
		Utente utente = JSONObjectManipulator.getUtenteDaJSONObjectConCurrentStatus(jsonObjectClassic);

		Assert.assertEquals(usernameExpected,utente.getUsername());
		Assert.assertEquals(emailExpected,utente.getEmail());
		Assert.assertEquals(currentStatusExpected,utente.getCURRENT_STATE());
	}


}