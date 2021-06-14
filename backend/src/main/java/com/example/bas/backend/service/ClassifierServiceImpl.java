package com.example.bas.backend.service;


import com.example.bas.backend.model.forms.CSVRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class ClassifierServiceImpl implements ClassifierService {


    private String classifier = "tree";
    private static final Logger logger = Logger.getLogger(ClassifierServiceImpl.class.getName());
    //TODO Sciezka lokalna
//    private static final String PATH = "..\\BAS_classifier\\";
//    private static final String COMMAND = "python " + PATH + "main.py";

    //TODO Sciezka na serwerze
    private static final String PATH = "BAS_classifier/";
    private static final String COMMAND = "python3 " + PATH + "main.py";

    public ClassifierServiceImpl() {
        File f = new File("backend/src/main/resources/classifier.properties");
        try {
            InputStream in = new FileInputStream(f);
            Properties props = new Properties();
            props.load(in);
            this.classifier = (String) props.get("bas.classifier");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    @Override
    public List<String> predict(String input) {
        List<String> lines = new ArrayList<>();
        String line;
        try {
            String command = String.format("%s --job test --classifier %s --input %s --save ", COMMAND, classifier, input);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            process.waitFor();
            while (processReader.ready()) {
                line = processReader.readLine();
                lines.add(line);
            }
            while (errorReader.ready()) {
                logger.warning(errorReader.readLine());
            }
        } catch (IOException | InterruptedException e) {
            logger.warning(e.getMessage());
        }
        return lines;
    }

    @Override
    public List<String> train() {
        List<String> lines = new ArrayList<>();
        String line;
        try {
            Process process = Runtime.getRuntime().exec(String.format("%s --job train --classifier %s", COMMAND, classifier));
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            process.waitFor();
            while (processReader.ready()) {
                line = processReader.readLine();
                lines.add(line);
            }
        } catch (IOException | InterruptedException e) {
            logger.warning(e.getMessage());
        }
        return lines;
    }

    @Override
    public void setClassifier(String clf) {
        this.classifier = clf;
        Properties props = new Properties();
        props.setProperty("bas.classifier", clf);
        File f = new File("backend/src/main/resources/classifier.properties");
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

    @Override
    public List<CSVRow> getCSV() {
        List<CSVRow> result = new ArrayList<>();
        int index = 0;
        try (Reader reader = Files.newBufferedReader(Paths.get(PATH + "BAS.csv"))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            while (records.iterator().hasNext()) {
                CSVRecord record;
                record = records.iterator().next();
                if (index != 0) {
                    CSVRow row = CSVRow.builder()
                            .gender(record.get(0))
                            .glucose(Integer.valueOf(record.get(1)))
                            .bloodPressure(Integer.valueOf(record.get(2)))
                            .insulin(Integer.valueOf(record.get(3)))
                            .bmi(Double.valueOf(record.get(4)))
                            .age(Integer.valueOf(record.get(5)))
                            .outcome(Double.valueOf(record.get(6)))
                            .build();
                    result.add(row);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}