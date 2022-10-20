package com.ittalens.gag.services;

public interface UserSessionService {

    boolean isLogged();

    Long currentUserId();

    CurrentUserModel addCurrentUserToSession(CurrentUserModel currentUserModel);
}
