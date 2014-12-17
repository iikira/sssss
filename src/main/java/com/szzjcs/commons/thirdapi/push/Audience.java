package com.szzjcs.commons.thirdapi.push;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Audience {
    private Set<String> andTags = new HashSet<>();
    private Set<String> orTags = new HashSet<>();
    private Set<String> alias = new HashSet<>();

    private Audience(){

    }

    public List<String> getAndTags(){
        return Collections.unmodifiableList(Lists.newArrayList(andTags));
    }

    public List<String> getOrTags(){
        return Collections.unmodifiableList(Lists.newArrayList(orTags));
    }

    public List<String> getAlias(){
        return Collections.unmodifiableList(Lists.newArrayList(alias));
    }

    public static class Builder{
        private Audience audience;
        public Builder(){
            this.audience = new Audience();
        }
        public Builder addAndTag(String tag){
            audience.andTags.add(tag);
            return this;
        };

        public Builder addOrTag(String tag){
            audience.orTags.add(tag);
            return this;
        };

        public Builder addAlias(String alias){
            audience.alias.add(alias);
            return this;
        };

        public Audience build(){
            return audience;
        }
    }
}
