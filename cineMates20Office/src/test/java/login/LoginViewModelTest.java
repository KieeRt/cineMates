package login;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewModelTest {


    @Test
    public void checkCampiValidiCampiLungezza35() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabcdabcdabcdabcdabcdabcd";
        String password = "abcdabcdabcdabcdabcdabcdabcdabcdZ";
        Assert.assertFalse(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidiLunghezzaNulla() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "";
        String password = "";
        Assert.assertFalse(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidiLunghezzaMinima() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcd";
        String password = "qawsedrf";
        Assert.assertTrue(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidiLunghezzaMinimaPlus() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdx";
        String password = "qawsedrfx";
        Assert.assertTrue(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidiLunghezzaMassima() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabcdabcdabcdabcdabcdabcd";
        String password = "abcdabcdabcdabcdabcdabcdabcdabcd";
        Assert.assertTrue(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidiLunghezzaMassimaMinus() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabcdabcdabcdabcdabcdabc";
        String password = "abcdabcdabcdabcdabcdabcdabcdabc";
        Assert.assertTrue(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidiLunghezzaNormal() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabc";
        String password = "abcdabcdabcdabcdab";
        Assert.assertTrue(viewModel.checkCampiValidi(username, password));
    }

    @Test
    public void checkCampiValidi_branch_1_3() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "ab";
        String password = "abcdabcdabcdabcdab";
        Assert.assertFalse(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidi_branch_1_2() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabcdabcdabcdabcdabcdabcdX";
        String password = "abcdabcdabcdabcdab";
        Assert.assertFalse(viewModel.checkCampiValidi(username, password));
    }

    @Test
    public void checkCampiValidi_branch_1_2_4() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabc";
        String password = "abcd";
        Assert.assertFalse(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidi_branch_1_2_4_5() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcdabc";
        String password = "abcdabcdabcdabcdabcdabcdabcdabcdX";
        Assert.assertFalse(viewModel.checkCampiValidi(username, password));
    }
    @Test
    public void checkCampiValidi_branch_1_2_4_5_6() {
        LoginViewModel viewModel = new LoginViewModel();
        String username = "abcdabcd";
        String password = "abcdabcdabcdabcdab";
        Assert.assertTrue(viewModel.checkCampiValidi(username, password));
    }


}