package com.cookos.dao;

import com.cookos.model.User;

public class UserDao extends GenericDao<User> {

    public UserDao() {
        super(User.class);
    }

    public UserDao(Class<User> type) {
        super(type);
    }
    
}
