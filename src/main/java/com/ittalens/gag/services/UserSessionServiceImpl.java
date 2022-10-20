package com.ittalens.gag.services;

import com.ittalens.gag.model.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@AllArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    public static final String CURRENT_USER = "CURRENT_USER";
    private final HttpSession httpSession;

    @Override
    public boolean isLogged() {
        if (httpSession.getAttribute(CURRENT_USER) != null){
            return true;
        }
        throw new UnauthorizedException("User is not logged");
    }

    @Override
    public Long currentUserId(){
        CurrentUserModel user = (CurrentUserModel) httpSession.getAttribute(CURRENT_USER);
        return Long.parseLong(user.getId().toString());
    }

    @Override
    public CurrentUserModel addCurrentUserToSession(CurrentUserModel currentUserModel) {
        httpSession.setAttribute(CURRENT_USER, currentUserModel);
        if(httpSession.getAttribute(CURRENT_USER) != null){
            return currentUserModel;
        }
        return null;
    }

}
