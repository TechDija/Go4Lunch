package com.dija.go4lunch.models;

import com.dija.go4lunch.models.nearbyAPImodels.Result;

import java.util.List;

public class LunchPlace {

    private String resultId;
    private List<String> usersId;
    private Result result;
    private User user;


    public LunchPlace() {
    }

    public LunchPlace(String resultId, List<String> usersId) {
        resultId = resultId;
        usersId = usersId;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        resultId = resultId;
    }

    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<String> usersId) {
        usersId = usersId;
    }

    public User getUserById(String id){
        if (user.getUid().equals(id)){
            return user;
        }
        return null;
    }

    public Result getResultById(String id){
        if (result.getId().equals(id)){
            return result;
        }
        return null;
    }


}