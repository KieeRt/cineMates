package repository;

public abstract class RepositoryFactory {

        public static RepositoryService getRepository(){
            return Repository.getIstance();
        }

}
