package login;

import org.jetbrains.annotations.NotNull;
import repository.RepositoryFactory;
import repository.RepositoryService;

public class LoginViewModel  {
    RepositoryService repositoryService;
    public void init(){
        repositoryService = RepositoryFactory.getRepository();
    }

    public boolean efettuaLogin(String userName, String password){
        return repositoryService.effettuaLogin(userName, password);
    }


    public boolean checkCampiValidi(@NotNull String username, @NotNull String password) throws IllegalArgumentException{
        return username.length() >= 4  && username.length() <= 32 && password.length() >= 8 && password.length() <= 32;
    }
}
