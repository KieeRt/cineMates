package apiGateway;

import model.Segnalazione;
import retrofit2.Call;
import retrofit2.http.*;
import utils.defaultResponse;

import java.util.List;

public interface CineMatesClient {

    @GET("stable/segnalazione")
    Call<List<Segnalazione>> getSegnalazioni(@Header("x-api-key") String apiKey);

    @GET("stable/segnalazione/singleton")
    Call<Segnalazione> getSegnalazione(@Query("idSegnalazione") int idSegnalazione, @Header("x-api-key") String apiKey);

    @DELETE("stable/segnalazione")
    Call<defaultResponse> removeSegnalazione(@Query("idSegnalazione") int idSegnalazione, @Header("x-api-key") String apiKey);

    @PATCH("stable/listapersonalizzata")
    Call<defaultResponse> setListaCensured(@Query("idlista") int idlista, @Header("x-api-key") String apiKey);

    @DELETE("stable/listapersonalizzata")
    Call<defaultResponse> removeLista(@Query("idlista") int idlista, @Header("x-api-key") String apiKey);

    @PUT("stable/notifica")
    Call<defaultResponse> sendNotifica(@Query("messaggio") String messaggio, @Query("idnotifica") int idNotifica, @Query("fk_utente") String emailUtente, @Header("x-api-key") String apiKey);

}


