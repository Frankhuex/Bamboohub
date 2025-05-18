package com.huex.bamboohub.dto;
import java.util.List;
public class RolesDTO {
    private UserSimpleDTO owner;
    private List<UserSimpleDTO> admins;
    private List<UserSimpleDTO> editors;
    private List<UserSimpleDTO> viewers;
    public UserSimpleDTO getOwner() {
        return owner;
    }
    public void setOwner(UserSimpleDTO owner) {
        this.owner = owner;
    }
    public List<UserSimpleDTO> getAdmins() {
        return admins;
    }
    public void setAdmins(List<UserSimpleDTO> admins) {
        this.admins = admins;
    }
    public List<UserSimpleDTO> getEditors() {
        return editors;
    }
    public void setEditors(List<UserSimpleDTO> editors) {
        this.editors = editors;
    }
    public List<UserSimpleDTO> getViewers() {
        return viewers;
    }
    public void setViewers(List<UserSimpleDTO> viewers) {
        this.viewers = viewers;
    }

    

    
}
