package com.java.iotshield;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

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
            //Profile profile = objectMapper.readValue(objectMapper.writeValueAsString(inputRecord), Profile.class);
        }
    }
}
