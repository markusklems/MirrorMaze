package edu.kit.aifb.mirrormaze.server.db.dao;

import java.io.Serializable;

public interface Dao<T, ID extends Serializable>
{

    @SuppressWarnings( "rawtypes" )
    Class getEntityClass();

    void persist( final T entity );

    void delete( final T entity );

    T get( final ID key );
}