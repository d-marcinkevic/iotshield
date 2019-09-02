package com.java.iotshield.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class Profile {

    @JsonProperty("type")
    private String type;

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("default")
    private String defaultPolicy;

    @JsonProperty("whitelist")
    private List<String> whitelist;

    @JsonProperty("blacklist")
    private List<String> blacklist;

    @JsonProperty("timestamp")
    private Long timestamp;

    public Profile(String type, String modelName, String defaultPolicy, List<String> whitelist, List<String> blacklist, Long timestamp) {
        this.type = type;
        this.modelName = modelName;
        this.defaultPolicy = defaultPolicy;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.timestamp = timestamp;
    }

    public Profile(){
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(String defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
