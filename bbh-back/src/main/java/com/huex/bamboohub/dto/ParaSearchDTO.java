package com.huex.bamboohub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ParaSearchDTO {
    private List<ParaSearchItem> paraSearchItems;
}
