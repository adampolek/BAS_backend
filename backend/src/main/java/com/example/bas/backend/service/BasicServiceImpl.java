package com.example.bas.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public class BasicServiceImpl<CLASS, CLASS_REPO extends JpaRepository<CLASS, ID>, ID> implements BasicService<CLASS, ID> {

    protected final CLASS_REPO repo;

    @Override
    public boolean save(CLASS object) {
        repo.save(object);
        return true;
    }

    @Override
    public boolean saveAll(List<CLASS> objects) {
        repo.saveAll(objects);
        return true;
    }

    @Override
    public boolean deleteById(ID id) {
        repo.deleteById(id);
        return !repo.existsById(id);
    }

    @Override
    public CLASS findById(ID id) {
        return repo.findById(id).get();
    }

    @Override
    public List<CLASS> findAll() {
        return repo.findAll();
    }
}