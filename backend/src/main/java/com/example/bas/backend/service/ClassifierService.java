package com.example.bas.backend.service;

import java.util.List;

public interface ClassifierService {

    List<String> predict(String classifier, String input, String save);
    void train(String classifier);
}
