package com.example.leidong.greendaotest.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.app.musicapp.db.Userdb;

import com.example.leidong.greendaotest.gen.UserdbDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userdbDaoConfig;

    private final UserdbDao userdbDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userdbDaoConfig = daoConfigMap.get(UserdbDao.class).clone();
        userdbDaoConfig.initIdentityScope(type);

        userdbDao = new UserdbDao(userdbDaoConfig, this);

        registerDao(Userdb.class, userdbDao);
    }
    
    public void clear() {
        userdbDaoConfig.clearIdentityScope();
    }

    public UserdbDao getUserdbDao() {
        return userdbDao;
    }

}
