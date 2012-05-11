package edu.kit.aifb.mirrormaze.server.db;

import javax.jdo.*;

public final class PMF
{
    private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory( "transactions-optional" );


    private PMF()
    {
    }

    public static PersistenceManagerFactory get()
    {
        return pmfInstance;
    }
}