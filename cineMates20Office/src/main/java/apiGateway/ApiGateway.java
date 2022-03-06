package apiGateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Notifica;
import model.Segnalazione;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import utils.defaultResponse;

public class ApiGateway {

    private final String url = "https://onsftlm3v1.execute-api.eu-central-1.amazonaws.com";
    private String apiKey;
    private Retrofit retrofit;
    private CineMatesClient cineMatesAPI;
    private Gson gson;
    private static ApiGateway apiGateway;

    private ApiGateway(){
        System.out.println("Costruisco ApiGateway");

        gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://4cef1e5qt3.execute-api.eu-central-1.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        cineMatesAPI = retrofit.create(CineMatesClient.class);

        JSONParser parser = new JSONParser();
        JSONObject obj;
        try {
            obj = (JSONObject)parser.parse(new FileReader("src/main/java/utils/credential.json"));
            apiKey = obj.get("x-api-key").toString();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Costruisco ApiGateway FINE");

    }

    public static ApiGateway getIstance(){
        if( apiGateway == null)
            apiGateway = new ApiGateway();
        return apiGateway;
    }

    public List<Segnalazione> recuperaSegnalazioni() throws IOException {
        Call<List<Segnalazione>> call = cineMatesAPI.getSegnalazioni(apiKey);
        Response<List<Segnalazione>> risposta = call.execute();
        return risposta.body();
    }

    public Segnalazione getSegnalazione(int idSegnalazione) throws IOException {
        Call<Segnalazione> call = cineMatesAPI.getSegnalazione(idSegnalazione, apiKey);
        Response<Segnalazione> risposta = call.execute();
        return risposta.body();
    }

    public boolean deleteSegnalazione(int idsegnalazione) throws IOException {
        Call<defaultResponse> call = cineMatesAPI.removeSegnalazione(idsegnalazione, apiKey);
        Response<defaultResponse> risposta = call.execute();
        if(risposta.body() == null)
            return false;
        return  risposta.body().getResponse().equals("true");
    }

    public boolean setListaCensored(int idlista) throws IOException {
        Call<defaultResponse> call = cineMatesAPI.setListaCensured(idlista, apiKey);
        Response<defaultResponse> risposta = call.execute();
        if(risposta.body() == null)
            return false;
        return risposta.body().getResponse().equals("true");

    }

    public boolean deleteLista(int idlista) throws IOException {
        Call<defaultResponse> call = cineMatesAPI.removeLista(idlista, apiKey);
        Response<defaultResponse> risposta = call.execute();
        if(risposta.body() == null)
            return false;
        return  risposta.body().getResponse().equals("true");
    }

    public boolean addNotifica(Notifica notifica, String emailRicevente) throws IOException {
        Call<defaultResponse> call = cineMatesAPI.sendNotifica(notifica.getMessaggio(), notifica.getIdNotifica(), emailRicevente, apiKey);
        Response<defaultResponse> risposta = call.execute();
        if(risposta.body() == null)
            return false;
        return  risposta.body().getResponse().equals("true");
    }





}
