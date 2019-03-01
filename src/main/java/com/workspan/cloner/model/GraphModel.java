package com.workspan.cloner.model;

/**
 * User: jeyanthan
 * Date: 2019-02-27
 * Add description here
 */
public class GraphModel {
    Entity[] entities;
    Link[] links;

    public GraphModel(Entity[] entities, Link[] links) {
        this.entities = entities;
        this.links = links;
    }

    public Entity[] getEntities() {
        return entities;
    }

    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }

    public Link[] getLinks() {
        return links;
    }

    public void setLinks(Link[] links) {
        this.links = links;
    }
}
