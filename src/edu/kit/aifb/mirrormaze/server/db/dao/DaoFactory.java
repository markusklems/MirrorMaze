package edu.kit.aifb.mirrormaze.server.db.dao;

public class DaoFactory
{

    private DaoFactory()
    {
    }

    public static AmiDao getAmiDao()
    {
        return edu.kit.aifb.mirrormaze.server.db.jdo.AmiDaoJDOImpl.getInstance();
    }
}