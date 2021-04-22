package com.example.bas.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassifierServiceImpl implements ClassifierService {

    //TODO Sciezke zmieniac trzeba
    private static final String PATH = "python E:\\Studia\\6_semestr\\BAS_backend\\backend\\src\\main\\resources\\classifiers\\BAS_classifier\\main.py";

    @Override
    public List<String> predict(String classifier, String input, String save) {
        List<String> lines = new ArrayList<>();
        String line;
        try {
            String command = String.format("%s --job test --classifier %s --input %s --save %s", PATH, classifier, input, save);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            while (processReader.ready()){
                line = processReader.readLine();
                lines.add(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return lines;
    }

    @Override
    public void train(String classifier) {
        try {
           Runtime.getRuntime().exec(String.format("%s --job train --classifier %s", PATH, classifier));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
