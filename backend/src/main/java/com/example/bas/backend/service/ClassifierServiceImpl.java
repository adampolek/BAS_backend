package com.example.bas.backend.service;


import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class ClassifierServiceImpl implements ClassifierService {


    private String classifier;

    //TODO Sciezka lokalna
    private static final String PATH = "python ..\\BAS_classifier\\main.py";

    //TODO Sciezka na serwerze
//    private static final String PATH = "python3 BAS_classifier/main.py";


    public ClassifierServiceImpl() {
        File f = new File("backend\\src\\main\\resources\\classifier.properties");
        try {
            InputStream in = new FileInputStream(f);
            Properties props = new Properties();
            props.load(in);
            this.classifier = (String) props.get("bas.classifier");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> predict(String input, String save) {
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
    public List<String> train() {
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

    @Override
    public void setClassifier(String clf) {
        this.classifier = clf;
        Properties props = new Properties();
        props.setProperty("bas.classifier", clf);
        File f = new File("src/main/resources/classifier.properties");
        try {
            OutputStream out = new FileOutputStream(f);
            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.store(props, out, "Classifier properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getClassifier() {
        return this.classifier;
    }
}