package com.workspan.cloner;

import com.workspan.cloner.model.Entity;
import com.workspan.cloner.model.GraphModel;

import java.util.Optional;

/**
 * User: jeyanthan
 * Date: 2019-02-27
 * Add description here
 */
public interface EntityNetwork {

    Entity addEntity(Entity entity);

    boolean removeEntity(Integer id);

    boolean addLink(Integer fromId, Integer toId);

    boolean removeLink(Integer fromId, Integer toId);

    Optional<GraphModel> findAndClone(Integer id);

}
