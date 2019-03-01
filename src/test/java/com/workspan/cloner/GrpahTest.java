package com.workspan.cloner;

import com.workspan.cloner.model.Entity;
import com.workspan.cloner.model.GraphModel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * User: jeyanthan
 * Date: 2019-02-28
 * Time: 18:11
 * Add description here
 */

public class GrpahTest {

    @Test
    public void testAddAndCloneEntityOnly(){

        addTestEntities();
        Optional<GraphModel> graphModel = Graph.getInstance().findAndClone(1);

        List<Entity> names = Arrays.asList(graphModel.get().getEntities()).stream().filter(e -> "EntityA".equals(e.getName()))
                            .collect(Collectors.toList());

        // 1, 2, 3 and clone of 1 = 4
        assertEquals(4, graphModel.get().getEntities().length);
        // 2 EntityA's created (1 and Clone of 1)
        assertEquals(2, names.size());
    }

    @Test
    public void testAddandCloneEntityAndLink(){
        //
        addTestEntities();
        addTestLinks();

        Optional<GraphModel> graphModel = Graph.getInstance().findAndClone(1);

        // 4 entities from previous test + clone of 1, 2, 3 = 7
        assertEquals(7, graphModel.get().getEntities().length);

        // 3 links inserted + clones of ({1,2}, {1,3} and {2,3}) = 6
        assertEquals(6, graphModel.get().getLinks().length);
    }

    private void addTestEntities(){
        Entity expected1 = new Entity(1, "EntityA", Optional.empty());
        Entity expected2 = new Entity(2, "EntityB", Optional.empty());
        Entity expected3 = new Entity(3, "EntityC", Optional.empty());

        Graph.getInstance().addEntity(expected1);
        Graph.getInstance().addEntity(expected2);
        Graph.getInstance().addEntity(expected3);
    }

    private void addTestLinks(){
        Graph.getInstance().addLink(1,2);
        Graph.getInstance().addLink(1,3);
        Graph.getInstance().addLink(2,3);
    }

}

