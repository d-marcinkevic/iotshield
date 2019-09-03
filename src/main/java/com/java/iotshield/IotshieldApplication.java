package com.java.iotshield;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.iotshield.profile.Profile;
import org.json.simple.JSONArray;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
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
        Profile profile;
        Map<String, Profile> profilesMap = new HashMap<>();
        Map<String, Object> outputMap;
        List<Map<String, Object>> outputList = new ArrayList<>();
        List<Map> inputMessages = objectMapper.readValue(new FileInputStream("input.json"), List.class);
        JSONArray jsonArray = new JSONArray();
        for(Map message : inputMessages){
            outputMap = new HashMap<>();
            if("profile_create".equals(message.get("type"))){
                profilesMap.put((String) message.get("model_name"), objectMapper.readValue(objectMapper.writeValueAsString(message), Profile.class));
            } else if("profile_update".equals(message.get("type"))){
                if(!profilesMap.isEmpty() && profilesMap.containsKey(message.get("model_name"))){
                    profile = profilesMap.get(message.get("model_name"));
                    if(message.get("blacklist") != null){
                        profile.setBlacklist((List<String>) message.get("blacklist"));
                    }
                    if(message.get("whitelist") != null){
                        profile.setWhitelist((List<String>) message.get("whitelist"));
                    }
                    if(message.get("timestamp") != null){
                        profile.setTimestamp((Long) message.get("timestamp"));
                    }
                    if(message.get("type") != null){
                        profile.setType((String) message.get("type"));
                    }
                }
            } else if("request".equals(message.get("type"))){
                if(!profilesMap.isEmpty() && profilesMap.containsKey(message.get("model_name"))){
                    outputMap.put("request_id", message.get("request_id"));
                    profile = profilesMap.get(message.get("model_name"));
                    if(profile.getBlacklist() != null && profile.getBlacklist().contains(message.get("url"))){
                        outputMap.put("action", "block");
                    } else if(profile.getWhitelist() != null && profile.getWhitelist().contains(message.get("url")) && "block".equals(profile.getDefaultPolicy())){
                        outputMap.put("action", "allow");
                    } else if((profile.getBlacklist() != null && !profile.getBlacklist().contains(message.get("url")) || profile.getBlacklist() == null) && "allow".equals(profile.getDefaultPolicy())){
                        outputMap.put("action", "allow");
                    } else if((profile.getWhitelist() != null && !profile.getWhitelist().contains(message.get("url")) || profile.getWhitelist() == null) && "block".equals(profile.getDefaultPolicy())){
                        outputMap.remove("request_id");
                        outputMap.put("device_id", message.get("device_id"));
                        outputMap.put("action", "quarantine");
                    }
                }
            }
            if(!outputMap.isEmpty()){
                outputList.add(outputMap);
            }
        }
        jsonArray.addAll(outputList);
        //write to file
        try (FileWriter file = new FileWriter("output.json")) {
            file.write(jsonArray.toJSONString());
        }
    }
}