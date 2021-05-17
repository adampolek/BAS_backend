package com.example.bas.backend.service;

import java.util.List;

public interface ClassifierService {

    List<String> predict(String input, String save);
    List<String> train();

    void setClassifier(String clf);

    String getClassifier();
}
