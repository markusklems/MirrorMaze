package edu.kit.aifb.mirrormaze.server.db.jdo;

import java.io.Serializable;

import javax.jdo.PersistenceManager;

import edu.kit.aifb.mirrormaze.server.db.PMF;
import edu.kit.aifb.mirrormaze.server.db.dao.Dao;

public class DaoJDOImpl<T, ID extends Serializable>
    implements Dao<T, ID>
{

    private final Class<? extends T> persistentClass;


    public DaoJDOImpl( final Class<? extends T> persistentClass )
    {
        this.persistentClass = persistentClass;
    }

    @Override
    public void persist( T entity )
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent( entity );
        } finally {
            pm.close();
        }
    }

    @Override
    public void delete( T entity )
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.deletePersistent( entity );
        } finally {
            pm.close();
        }

    }

    @Override
    public T get( ID key )
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            return pm.getObjectById( getEntityClass(), key );
        } finally {
            pm.close();
        }
    }

    @Override
    public Class<? extends T> getEntityClass()
    {
        return persistentClass;
    }

}