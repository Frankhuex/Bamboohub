package com.huex.bamboohub.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @AllArgsConstructor
public class RolesDTO {
    private UserSimpleDTO owner;
    private List<UserSimpleDTO> admins;
    private List<UserSimpleDTO> editors;
    private List<UserSimpleDTO> viewers;


    
}
