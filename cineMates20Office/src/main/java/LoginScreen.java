import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LoginScreen {
    private static CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private static CognitoIdentityClient cognitoIdentityClient;
    public static void main(String[] args) {
        System.out.println("Hello");

        System.out.println("db1");

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("AKIAX7P4A2BZ6DSKBDTU",
                "Jdqr+apvvnmaifJW+x4YpJoYeymjQPv+sNWH2FtK");

        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.EU_CENTRAL_1)
                .build();

        System.out.println("db2");

        cognitoIdentityClient = CognitoIdentityClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.EU_CENTRAL_1)
                .build();
        if(cognitoIdentityProviderClient == null) {


            System.out.println("Client e' null");
        }else {

            System.out.println("Client non e' null");
            login();

        }
    }

    public static void login() {
        new Thread(() -> {
            Map<String, String> parametri = new HashMap<String, String>();
            parametri.put("USERNAME", "dimalozyak1997@gmail.com");
            parametri.put("PASSWORD", "qawsedrf");
            //parametri.put("SECRET_HASH", "1q6cufpvhur37vf9diqvco59ijl82j2hf54etjg188ugkj353f6t");
            parametri.put("SECRET_HASH", calculateSecretHash("68c31h602ukntaavsr4c0ognp8",
                    "1q6cufpvhur37vf9diqvco59ijl82j2hf54etjg188ugkj353f6t", "dimalozyak1997@gmail.com"));
            System.out.println("debug 3");

            final AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .clientId("68c31h602ukntaavsr4c0ognp8")
                    .userPoolId("eu-central-1_z86Uu5yvM")
                    .authParameters(parametri)
                    .build();

            InitiateAuthRequest initiateAuthRequest =
                    InitiateAuthRequest.builder()
                            .clientId("68c31h602ukntaavsr4c0ognp8")
                            .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                            .authParameters(parametri)
                            .build();


            try {
                System.out.println("debug 4.1");
                Consumer<AdminInitiateAuthRequest.Builder> adminInitiateAuthRequest = new Consumer<AdminInitiateAuthRequest.Builder>() {
                    @Override
                    public void accept(AdminInitiateAuthRequest.Builder builder) {

                    }
                };
                System.out.println("debug 4.2");
                AdminInitiateAuthResponse result = cognitoIdentityProviderClient.adminInitiateAuth(authRequest);
                System.out.println("debug 4.3");
                System.out.println(result.authenticationResult().toString());
                System.out.println("debug 4.4");

                System.out.println(result.authenticationResult().accessToken());
                System.out.println("debug 4.5");

                System.out.println(result.authenticationResult().idToken());

                // cognitoIdentityProviderClient.adminInitiateAuth(initiateAuthRequest);
                InitiateAuthResponse initiateAuthResponse = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
                System.out.println("debug 5");
                AuthenticationResultType risposta =  initiateAuthResponse.authenticationResult();
                System.out.println(risposta.toString());
                /*if (initiateAuthResponse.challengeName() != null) {
                    respondToAuthChallenge(initiateAuthResponse, authenticationHandler);
                } else {
                    authenticationHandler.onAuthenticationSuccess(
                            initiateAuthResponse.authenticationResult(),
                            getCredentials(initiateAuthResponse.authenticationResult())
                    );
                }*/
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }



    private static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

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
        }}


}
