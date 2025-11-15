package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveOrder;
import com.dailin.api_posventa.dto.response.GetOrder;
import com.dailin.api_posventa.persistence.entity.Order;

public interface OrderService {

   Page<GetOrder> findAll(Pageable pageable);

   GetOrder findOneById(Long id);

   Order findOneEntityById(Long id);

   GetOrder findOrderByTableId(Long tableId);

   GetOrder createOne(SaveOrder saveDto);

   GetOrder updatedOneById(SaveOrder saveDto, Long id);

   void deleteOneById(Long id);
}