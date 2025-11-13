package com.dailin.api_posventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveTable;
import com.dailin.api_posventa.dto.response.GetTable;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.TableMapper;
import com.dailin.api_posventa.persistence.entity.DiningTable;
import com.dailin.api_posventa.persistence.repository.TableCrudRepository;
import com.dailin.api_posventa.service.TableService;

@Transactional
@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableCrudRepository tableCrudRepository;

    @Override
    public GetTable createOne(SaveTable saveDto) {
        
        DiningTable newTable = TableMapper.toEntity(saveDto);
        return TableMapper.toGetDto(tableCrudRepository.save(newTable));
    }

    @Override
    public void deleteById(Long id) {
         
        if(tableCrudRepository.existsById(id)){
            tableCrudRepository.deleteById(id);
            return;
        }

        throw new ObjectNotFoundException("table: " +Long.toString(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetTable> findAll(Pageable pageable) {
        
        Page<DiningTable> entities = tableCrudRepository.findAll(pageable);
        return entities.map(TableMapper::toGetDto);
    }

    @Transactional(readOnly = true)
    @Override
    public GetTable findOneById(Long id) {
        return TableMapper.toGetDto(this.findOneEntityById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public DiningTable findOneEntityById(Long id) {
        return tableCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("table: "+ Long.toString(id)));
    }

    @Override
    public GetTable updatedById(Long id, SaveTable saveDto) {
        
        DiningTable oldTable = this.findOneEntityById(id);
        TableMapper.updateEntity(oldTable, saveDto);

        return TableMapper.toGetDto(tableCrudRepository.save(oldTable));
    }
}
