package com.java.iotshield;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.iotshield.profile.Profile;
import org.json.simple.JSONArray;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

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
        Set<Object> protectedDevices = new HashSet<>();
        Set<Object> suspectedToBeHacked = new HashSet<>();
        Set<Object> missedBlocks = new HashSet<>();
        Set<Object> incorrectBlocks = new HashSet<>();

        for(Map message : inputMessages){
            outputMap = new HashMap<>();
            if("profile_create".equals(message.get("type"))){
                profilesMap.put((String) message.get("model_name"), objectMapper.readValue(objectMapper.writeValueAsString(message), Profile.class));
            } else if("profile_update".equals(message.get("type"))){
                Profile previousProfile = new Profile();
                if(!profilesMap.isEmpty() && profilesMap.containsKey(message.get("model_name"))){
                    profile = profilesMap.get(message.get("model_name"));
                    previousProfile.setModelName(profile.getModelName());
                    previousProfile.setTimestamp(profile.getTimestamp());
                    previousProfile.setBlacklist(profile.getBlacklist());
                    previousProfile.setWhitelist(profile.getWhitelist());
                    previousProfile.setDefaultPolicy(profile.getDefaultPolicy());
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
                    if(message.get("model_name") != null){
                        profile.setModelName((String) message.get("model_name"));
                    }
                    for(Map delayedMessage : inputMessages){
                        Long delayedTimestamp = (Long) delayedMessage.get("timestamp");
                        Long actualTimestamp = profile.getTimestamp();
                        if("request".equals(delayedMessage.get("type")) && delayedTimestamp <= actualTimestamp && profile.getModelName().equals(delayedMessage.get("model_name")) && previousProfile.getTimestamp() <= delayedTimestamp){
                            if(profile.getBlacklist() != previousProfile.getBlacklist() && profile.getBlacklist().contains(delayedMessage.get("url"))){
                                missedBlocks.add(delayedMessage.get("request_id"));
                            }
                            if(profile.getWhitelist() != previousProfile.getWhitelist() && !profile.getWhitelist().contains(delayedMessage.get("url")) && previousProfile.getWhitelist().contains(delayedMessage.get("url"))){
                                missedBlocks.add(delayedMessage.get("request_id"));
                            }
                            if(previousProfile.getBlacklist().contains(delayedMessage.get("url")) && !profile.getBlacklist().contains(delayedMessage.get("url"))){
                                incorrectBlocks.add(delayedMessage.get("request_id"));
                            }
                            if(!previousProfile.getWhitelist().contains(delayedMessage.get("url")) && profile.getWhitelist().contains(delayedMessage.get("url"))){
                                incorrectBlocks.add(delayedMessage.get("request_id"));
                            }
                        }
                    }
                }
            } else if("request".equals(message.get("type"))){
                if(!profilesMap.isEmpty() && profilesMap.containsKey(message.get("model_name"))){
                    outputMap.put("request_id", message.get("request_id"));
                    profile = profilesMap.get(message.get("model_name"));
                    if(profile.getBlacklist() != null && profile.getBlacklist().contains(message.get("url"))){
                        outputMap.put("action", "block");
                        protectedDevices.add(message.get("device_id"));
                    } else if(profile.getWhitelist() != null && profile.getWhitelist().contains(message.get("url")) && "block".equals(profile.getDefaultPolicy())){
                        outputMap.put("action", "allow");
                    } else if((profile.getBlacklist() != null && !profile.getBlacklist().contains(message.get("url")) || profile.getBlacklist() == null) && "allow".equals(profile.getDefaultPolicy())){
                        outputMap.put("action", "allow");
                    } else if((profile.getWhitelist() != null && !profile.getWhitelist().contains(message.get("url")) || profile.getWhitelist() == null) && "block".equals(profile.getDefaultPolicy())){
                        outputMap.remove("request_id");
                        outputMap.put("device_id", message.get("device_id"));
                        outputMap.put("action", "quarantine");
                        protectedDevices.add(message.get("device_id"));
                        suspectedToBeHacked.add(message.get("device_id"));
                    }
                }
            }
            if(!outputMap.isEmpty()){
                outputList.add(outputMap);
            }
        }
        
        jsonArray.addAll(outputList);

        try (FileWriter file = new FileWriter("output.json")) {
            file.write(jsonArray.toJSONString());
        }

        StringBuilder sb = new StringBuilder("\n\n");
        sb.append("Devices were protected: ").append(protectedDevices.size()).append("\n").append(protectedDevices).append("\n\n")
                .append("Devices suspected to be hacked: ").append(suspectedToBeHacked.size()).append("\n").append(suspectedToBeHacked).append("\n\n")
                .append("Blocks were missed due to delayed profile updates: ").append(missedBlocks.size()).append("\n").append(missedBlocks).append("\n\n")
                .append("Blocks were issued incorrectly due to delayed profile updates: ").append(incorrectBlocks.size()).append("\n").append(incorrectBlocks);

        System.out.println(sb.toString());
    }
}