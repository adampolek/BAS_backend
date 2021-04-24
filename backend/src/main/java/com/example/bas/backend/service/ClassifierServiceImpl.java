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

    //TODO Sciezka lokalna
//    private static final String PATH = "python ..\\BAS_classifier\\main.py";
    //TODO Sciezka na serwerze
    private static final String PATH = "python3 BAS_classifier/main.py";

    @Override
    public List<String> predict(String classifier, String input, String save) {
        List<String> lines = new ArrayList<>();
        String line;
        try {
            String command = String.format("%s --job test --classifier %s --input %s --save %s", PATH, classifier, input, save);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            while (processReader.ready()) {
                line = processReader.readLine();
                lines.add(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return lines;
    }

    @Override
    public List<String> train(String classifier) {
        List<String> lines = new ArrayList<>();
        String line;
        try {
            Process process = Runtime.getRuntime().exec(String.format("%s --job train --classifier %s", PATH, classifier));
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            process.waitFor();
            while (processReader.ready()) {
                line = processReader.readLine();
                lines.add(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
