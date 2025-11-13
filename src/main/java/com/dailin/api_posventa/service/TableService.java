package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveTable;
import com.dailin.api_posventa.dto.response.GetTable;
import com.dailin.api_posventa.persistence.entity.DiningTable;

public interface TableService {

    Page<GetTable> findAll(Pageable pageable);

    GetTable findOneById(Long id);

    DiningTable findOneEntityById(Long id);

    GetTable createOne(SaveTable saveDto);

    GetTable updatedById(Long id, SaveTable saveDto);

    void deleteById(Long id);
}