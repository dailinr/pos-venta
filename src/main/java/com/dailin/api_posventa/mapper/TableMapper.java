package com.dailin.api_posventa.mapper;

import com.dailin.api_posventa.dto.request.SaveTable;
import com.dailin.api_posventa.dto.response.GetTable;
import com.dailin.api_posventa.persistence.entity.DiningTable;

public class TableMapper {

    public static GetTable toGetDto(DiningTable entity){

        if(entity == null) return null;

        return new GetTable(
            entity.getId(), 
            entity.getNumber(), 
            entity.getState(), 
            entity.getServiceType()
        );
    }

    public static DiningTable toEntity(SaveTable dto){
        
        if(dto == null) return null;

        DiningTable newTable = new DiningTable();

        newTable.setNumber(dto.number());
        // newTable.setState(dto.state());
        newTable.setServiceType(dto.serviceType());

        return newTable;
    }

    public static void updateEntity(DiningTable oldTable, SaveTable saveDto){

        if(oldTable == null || saveDto == null) return;

        oldTable.setNumber(saveDto.number());
        // oldTable.setState(saveDto.state());
        oldTable.setServiceType(saveDto.serviceType());
    }
}
