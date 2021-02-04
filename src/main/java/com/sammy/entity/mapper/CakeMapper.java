package com.sammy.entity.mapper;

import com.sammy.entity.api.CakeApiDTO;
import com.sammy.entity.business.CakeDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CakeMapper {

    public static CakeDTO toBusiness(CakeApiDTO apiCake){
        return CakeDTO.builder()
                      .cakeId(apiCake.getCakeId())
                      .title(apiCake.getTitle())
                      .desc(apiCake.getDesc())
                      .image(apiCake.getImage())
                      .build();
    }

    public static CakeApiDTO toApi(CakeDTO businessCake){
        return CakeApiDTO.builder()
                         .cakeId(businessCake.getCakeId())
                         .title(businessCake.getTitle())
                         .desc(businessCake.getDesc())
                         .image(businessCake.getImage())
                         .build();
    }
}