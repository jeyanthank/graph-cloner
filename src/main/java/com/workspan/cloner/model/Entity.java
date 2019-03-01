package com.workspan.cloner.model;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;

/**
 * User: jeyanthan
 * Date: 2019-02-27
 * Add description here
 */
public class Entity {

    @SerializedName("entity_id")
    private Integer entityId;
    private String name;
    private String description;

    public Entity(Integer entityId){
        this.entityId = entityId;
    }

    public Entity(Integer entityId, String name, Optional<String> description){
        this.entityId = entityId;
        this.name = name;
        this.description = description.orElse("");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + entityId.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Entity other = (Entity) obj;
        if (this.entityId == null) {
            if (other.entityId != null)
                return false;
        } else if (!this.entityId.equals(other.entityId))
            return false;
        return true;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
