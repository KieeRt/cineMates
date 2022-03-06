package regolamento;

import repository.RepositoryFactory;
import repository.RepositoryService;

public class RegolamentoViewModel {
    RepositoryService repository ;

    public void init(){
        repository = RepositoryFactory.getRepository();
    }

    public String recuperaRegolamento(){
        return  repository.recuperaRegolamento();
    }
}
