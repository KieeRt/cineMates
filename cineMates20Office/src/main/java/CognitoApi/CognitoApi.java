package CognitoApi;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import repository.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.text.html.parser.Parser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CognitoApi {
    private static CognitoApi cognitoApi;
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private String clientID;
    private String userPoolID;
    private String userPoolClientSecret;
    private AwsBasicCredentials awsCreds;
    private Region region;
    private AdminInitiateAuthRequest authRequest;
    private Map<String, String> authRequestParams;

    public CognitoApi()  {

        JSONParser parser = new JSONParser();

        JSONObject obj;
        try {
            obj = (JSONObject)parser.parse(new FileReader("src/main/java/utils/credential.json"));
            awsCreds = AwsBasicCredentials.create(obj.get("accesskeyId").toString(),  obj.get("secretAccessKey").toString());
            clientID = obj.get("clientId").toString();
            userPoolID = obj.get("clientUserPool").toString();
            userPoolClientSecret = obj.get("clientUserPoolSecret").toString();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        authRequestParams = new HashMap<>();
        region = Region.EU_CENTRAL_1;

        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(region)
                .build();
    }

    public static CognitoApi getIstance(){
        if( cognitoApi == null)
            cognitoApi = new CognitoApi();
        return cognitoApi;
    }
    private void prepareRequest(String email, String password){
        authRequestParams.put("USERNAME", email);
        authRequestParams.put("PASSWORD", password);
        authRequestParams.put("SECRET_HASH", calculateSecretHash(clientID, userPoolClientSecret, email));

        authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .clientId(clientID)
                .userPoolId(userPoolID)
                .authParameters(authRequestParams)
                .build();
    }

    public boolean effettuaLogin(String email, String password){
        boolean accessoRiuscito = false;
        prepareRequest(email, password);
        try{
            AdminInitiateAuthResponse result = cognitoIdentityProviderClient.adminInitiateAuth(authRequest);
            Optional<String> risultato = result.getValueForField("ChallengeName", String.class);
            if(risultato.isPresent() && risultato.get().equals("NEW_PASSWORD_REQUIRED")){
                AdminSetUserPasswordRequest authChangePassRequest = AdminSetUserPasswordRequest.builder()
                    .userPoolId(userPoolID)
                    .password(password)
                    .username(email)
                    .permanent(true)
                    .build();

                AdminSetUserPasswordResponse resultChangePass = cognitoIdentityProviderClient.adminSetUserPassword(authChangePassRequest);
            }
          accessoRiuscito = true;
        }catch (software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException  e){
            System.out.println("EXCEPTION DIO PORCO");
            e.printStackTrace();
        }
        return  accessoRiuscito;
    }

    /**
     *
     * @param userPoolClientId id del client app
     * @param userPoolClientSecret il codice secreto dell'app client
     * @param userName userName usato per l'utente che cerca loggare
     * @return SecretHash che AWS richiede per inviare la richiesta di login tramite Cognito
     * @throws IllegalArgumentException nel caso in cui almeno uno dei parametri e' null, userPool e' vuoto o ClientSecret e' vuoto
     */
    public static String calculateSecretHash(@NotNull String userPoolClientId, @NotNull String userPoolClientSecret, @NotNull String userName) throws IllegalArgumentException {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        if( userPoolClientId.equals("") || userPoolClientSecret.equals("")){
            throw new IllegalArgumentException("Empty key");
        }
        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }


    public boolean logout(){
        //TODO : DA IMPLEMENTARE
        return true ;
    }
}
