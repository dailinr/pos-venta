package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveTable;
import com.dailin.api_posventa.dto.response.GetTable;
import com.dailin.api_posventa.persistence.entity.DiningTable;
import com.dailin.api_posventa.utils.ServiceType;
import com.dailin.api_posventa.utils.TableState;

public interface TableService {

    Page<GetTable> findAll(ServiceType serviceType, TableState state, Pageable pageable);

    GetTable findOneById(Long id);

    DiningTable findOneEntityById(Long id);

    DiningTable save(DiningTable entity);

    GetTable createOne(SaveTable saveDto);

    GetTable updatedById(Long id, SaveTable saveDto);

    void deleteById(Long id);
}