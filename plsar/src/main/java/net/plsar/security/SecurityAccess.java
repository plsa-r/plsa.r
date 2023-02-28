package net.plsar.security;

import net.plsar.Dao;

import java.util.Set;

public interface SecurityAccess {
    /**
     * Intended to return the user's password based
     * on the username
     *
     * @param user
     * @return returns hashed password
     */
    public String getPassword(String user);


    /**
     * takes a username
     *
     * @param user
     * @return returns a unique set of role strings
     */
    public Set<String> getRoles(String user);


    /**
     *
     * @param user
     * @return returns a unique set of user permissions
     * net.plsar.example permission user:maintenance:(id) (id)
     * replaced with actual id of user
     */
    public Set<String> getPermissions(String user);

    public void setDao(Dao dao);
}

