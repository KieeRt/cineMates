package CognitoApi;

import org.junit.Assert;
import org.junit.Test;
public class CognitoApiTest {

    /*/
    Cerco di costruire una WECT, BLACKBOCX
     */
    @Test
    public void calculateSecretHash(){
        String userPoolClientId = "dfsdfds";
        String userPoolClientSecret = "dfsdfds";
        String userName = "dfsdfds";
        String risultato = CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
        Assert.assertEquals("KjyJGxmUCWq2q5ZEJw/04scUgaC3DcibYOpBMNpLXMk=", risultato);
    }
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHashCampiVuoti(){
        String userPoolClientId = "";
        String userPoolClientSecret = "";
        String userName = "";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
    }
    // FINO QUI COPRO base choice WECT
    @Test
    public void calculateSecretHashUserNameVuoto(){
        String userPoolClientId = "dfsdfds";
        String userPoolClientSecret = "dfsdfds";
        String userName = "";
        String risultato = CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
        Assert.assertEquals("i5H/zUHs/0c2vQx5oIOs3AkK9BfrNdUbg7j9WeQqLSA=", risultato);
    }
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHashConSecretVuoto(){
        String userPoolClientId = "dfsdfds";
        String userPoolClientSecret = "";
        String userName = "dfsdfds";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);

    }
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHashConUserPoolVuoto(){
        String userPoolClientId = "";
        String userPoolClientSecret = "dfsdfds";
        String userName = "dfsdfds";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
    }
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHashConUserSecretVuotoConUserVuoto(){
        String userPoolClientId = "dfsdfds";
        String userPoolClientSecret = "";
        String userName = "";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
    }
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHashConClientVuoto(){
        String userPoolClientId = "";
        String userPoolClientSecret = "dfsdfds";
        String userName = "dfsdfds";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
    }
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHashConClientVuotoConUserSecretVuoto(){
        String userPoolClientId = "";
        String userPoolClientSecret = "";
        String userName = "dfsdfds";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
    }

    // WHITE BOXE:
    @Test (expected = IllegalArgumentException.class)
    public void calculateSecretHash_branch_1_2(){
        String userPoolClientId = "";
        String userPoolClientSecret = "";
        String userName = "dfsdfds";
        CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
    }
    @Test
    public void calculateSecretHash_branch_1_3(){
        String userPoolClientId = "dfsdfds";
        String userPoolClientSecret = "dfsdfds";
        String userName = "dfsdfds";
        String risultato = CognitoApi.calculateSecretHash(userPoolClientId, userPoolClientSecret, userName);
        Assert.assertEquals("KjyJGxmUCWq2q5ZEJw/04scUgaC3DcibYOpBMNpLXMk=", risultato);
    }





}