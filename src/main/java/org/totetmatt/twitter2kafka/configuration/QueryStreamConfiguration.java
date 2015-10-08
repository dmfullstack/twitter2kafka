package org.totetmatt.twitter2kafka.configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import utils.Place;
import utils.User;
import utils.UserIdResolver;

@Component

@Configuration
@ConfigurationProperties(locations = { "file:./streamquery.yml" })
public class QueryStreamConfiguration {
    List<User> users = new ArrayList<User>();
    List<Place> locations = new ArrayList<Place>();
    List<String> words = new ArrayList<String>();

    boolean useSampleStream;

    public String[] filterQueryWords() {
        return words.stream().toArray(String[]::new);
    }
    
    public Long[] filterQueryUsers(){
        return users.stream().map(u -> u.getId()).toArray(Long[]::new);
    }

    public List<Place> getLocations() {
        return locations;
    }

    public void setLocations(List<Place> locations) {
        this.locations = locations;
    }

    public boolean isUseSampleStream() {
        return useSampleStream;
    }

    public void setUseSampleStream(boolean useSampleStream) {
        this.useSampleStream = useSampleStream;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<User> getUsers() {
        return users;
    }

    public ArrayList<String> getScreenNames() {
        ArrayList<String> screenNames = new ArrayList<String>();
        users.stream()
                .forEach(u -> screenNames.add(u.getScreenName().toLowerCase()));
        return screenNames;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void sloveUser() {
        this.users.stream().parallel().forEach((u) -> {
            if (u.getId() == null) {
                try {
                    u.setId(UserIdResolver.resolve(u.getScreenName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save() {
        Representer representer = new Representer();

        representer.addClassTag(QueryStreamConfiguration.class, Tag.MAP);
        Yaml yaml = new Yaml(representer, new DumperOptions());
        try {
            yaml.dump(this, new FileWriter("./streamquery.yml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}