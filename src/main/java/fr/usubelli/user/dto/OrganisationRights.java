package fr.usubelli.user.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganisationRights {

    public enum Right {
        UPDATE_ORGANISATION,
        GRANT_RIGHT,
        REDUCE_RIGHT,
    }

    private String siren;
    private List<Right> rights;

    public OrganisationRights() {
        this.rights = new ArrayList<>();
    }

    public String getSiren() {
        return siren;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

}
