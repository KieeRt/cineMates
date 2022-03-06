package com.example.appandroid.repository;

public abstract class RepositoryFactory {

	public static RepositoryService getRepositoryConcrete(){
		return Repository.getInstance();
	}
}
