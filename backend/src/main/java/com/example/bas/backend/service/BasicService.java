package com.example.bas.backend.service;

import java.util.List;

public interface BasicService<CLASS, ID> {

    boolean save(CLASS object);

    boolean saveAll(List<CLASS> objects);

    boolean deleteById(ID id);

    CLASS findById(ID id);

    List<CLASS> findAll();
}