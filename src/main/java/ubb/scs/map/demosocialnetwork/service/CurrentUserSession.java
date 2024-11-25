package ubb.scs.map.demosocialnetwork.service;

import ubb.scs.map.demosocialnetwork.domain.User;

public class CurrentUserSession {
    private static CurrentUserSession instance;
    User currentUser;

    private CurrentUserSession() {}

    public static CurrentUserSession getInstance() {
        if(instance == null) {
            instance = new CurrentUserSession();
        }
        return instance;
    }

    public static void setInstance(CurrentUserSession instance) {
        CurrentUserSession.instance = instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void currentUser(){
        currentUser = null;
    }
}
