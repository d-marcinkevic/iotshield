package com.java.iotshield;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.iotshield.profile.Profile;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class IotshieldApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(IotshieldApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap> inputRecords = objectMapper.readValue(new FileInputStream("input.json"), List.class);
        for(HashMap inputRecord : inputRecords){
            objectMapper.writeValueAsString(inputRecord);

            //Profile profile = objectMapper.readValue(objectMapper.writeValueAsString(inputRecord), Profile.class);
        }

    }
}
