package com.rayer.util.provisioner;

import java.io.IOException;


//TODO:
//先把DAO用ormlite的function實作，然後再拿掉ormlite，決定一個DAO mandatory interface以後，讓外面的人都用這個來implement
//ormlite目前判斷實在不太適合放在utilities....(need confirm) 所以得想辦法拿掉它



/**
 * A DAO for Database, still in progress, and do not use it.
 * @deprecated
 * @author rayer
 *
 * @param <Type>
 * @param <IndexType>
 * @see ResourceProvisioner
 */
public class DatabaseProvisioner<Type, IndexType> implements
		ResourceProvisioner<Type, IndexType> {
	
	interface Dao<Type, IndexType> {
		Type query(IndexType identificator);
		void create(Type resource);
	}
	
	Dao<Type, IndexType> mDAO;
	
	public DatabaseProvisioner(Dao<Type, IndexType> targetDAO) {
		mDAO = targetDAO;
	}

	@Override
	public Type getResource(IndexType identificator) throws IOException {
		mDAO.query(identificator);
		return null;
	}

	@Override
	public boolean setResource(IndexType identificator, Type targetResource) {
		mDAO.create(targetResource);
		return false;
	}

	@Override
	public boolean dereferenceResource(IndexType identificator) {
		return false;
	}

	@Override
	public boolean clearAllCachedResource() {
		return false;
	}

}
