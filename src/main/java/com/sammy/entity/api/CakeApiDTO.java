package com.sammy.entity.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"title", "desc", "image"})
public class CakeApiDTO {
    private Long cakeId;
    private String title;
    private String desc;
    private String image;
}