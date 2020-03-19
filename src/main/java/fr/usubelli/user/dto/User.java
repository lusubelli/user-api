package fr.usubelli.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;

public class User {

    public enum UserState {
        CREATED
    }

    @JsonIgnore
    private ObjectId id;
    private String email;
    private String password;
    private OrganisationRights rights;
    private UserState state;

    public User() {
    }

    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public OrganisationRights getRights() {
        return rights;
    }

    public UserState getState() {
        return state;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRights(OrganisationRights rights) {
        this.rights = rights;
    }

    public void setState(UserState state) {
        this.state = state;
    }

}
