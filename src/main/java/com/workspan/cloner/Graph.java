package com.workspan.cloner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workspan.cloner.model.Entity;
import com.workspan.cloner.model.GraphModel;
import com.workspan.cloner.model.Link;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: jeyanthan
 * Date: 2019-02-27
 * Add description here
 */
public class Graph implements EntityNetwork{
    private static EntityNetwork entityNetwork;
    private final Map<Entity, Set<Entity>> relatedEntity;
    private final Map<Entity, Entity> reverseRelations;
    private final Random random;


    private Graph(){
        relatedEntity = Collections.synchronizedMap(new HashMap<>());
        reverseRelations = Collections.synchronizedMap(new HashMap<>());
        random = new Random();
    }

    // Singleton design pattern for Graph object
    //  1. It can be extended to work in a multi-threaded environment for scalability
    //  2. The data-structures used are synchronized objects
    //  3. A singleton random object used to prevent collisions and quickly generate a unique number for the cloned entities
    //
    public static EntityNetwork getInstance() {
        if(entityNetwork == null){
            entityNetwork = new Graph();
        }

        return entityNetwork;
    }

    public static void intialize(String filename) throws IOException {

        // Get Gson object
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // read JSON file data as String
        try {
            String fileData = new String(Files.readAllBytes(Paths.get(filename)));
            // parse json string to object
            GraphModel graphModel = gson.fromJson(fileData, GraphModel.class);

            // create JSON String from Object
            String inputGraph = gson.toJson(graphModel);
            //System.out.println(inputGraph);

            // Stream the entities and link arrays to create the initial graph
            Arrays.asList(graphModel.getEntities()).stream().map(entity -> getInstance().addEntity(entity)).collect(Collectors.toList());
            Arrays.asList(graphModel.getLinks()).stream().map(link -> getInstance().addLink(link.getFrom(),link.getTo())).collect(Collectors.toList());

        } catch (IOException e){
            System.out.println(" File operation error. Aborting!!");
            throw e;
        }

    }

    @Override
    public Entity addEntity(Entity entity) {

        if(relatedEntity.containsKey(entity)){
            return entity;
        }
        relatedEntity.put(entity, new HashSet<>());
        //System.out.println(" adding entity  " + entity.getEntityId() + " " + entity.getName() + " " + entity.getDescription());
        return entity;
    }

    @Override
    public boolean removeEntity(Integer id) {
        Entity entity = new Entity(id);
        relatedEntity.values().stream().map(entities -> entities.remove(entity)).collect(Collectors.toList());
        relatedEntity.remove(entity);
        //System.out.println(" removing entity  " + entity.getEntityId() + " " + entity.getName() + " " + entity.getDescription());
        return true;
    }

    @Override
    public boolean addLink(Integer fromId, Integer toId) {
        Entity fromEntity = new Entity(fromId);
        Entity toEntity = new Entity(toId);
        //System.out.println(" adding link from  " + fromId + " to " + toId);
        if(relatedEntity.containsKey(fromEntity) && relatedEntity.containsKey(toEntity)){
            relatedEntity.get(fromEntity).add(getEntity(toEntity).get());

            // Reverse relationship map - To help establish a link from the ancestor of the root element to the cloned root element without having to
            // traverse the graph all over again
            reverseRelations.put(toEntity, fromEntity);

            return true;
        }
        return false;
    }

    @Override
    public boolean removeLink(Integer fromId, Integer toId) {
        //System.out.println(" removing link from  " + fromId + " to " + toId);
        Entity fromEntity = new Entity(fromId);

        if(relatedEntity.containsKey(fromEntity))
            return relatedEntity.get(fromEntity).remove(new Entity(toId));

        return false;

    }

    @Override
    public Optional<GraphModel> findAndClone(Integer id) {
        //System.out.println(" cloning " + id + " and its related entities");
        if(!relatedEntity.containsKey(new Entity(id)))
            return Optional.empty();

        Entity root = getEntity(new Entity(id)).get();

        // DFS traversal of the graph using a stack
        Set<Entity> toBeCloned = new LinkedHashSet<>();
        Stack<Entity> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Entity entity = stack.pop();
            if (!toBeCloned.contains(entity)) {
                toBeCloned.add(entity);
                for (Entity v : relatedEntity.get(entity)) {
                    stack.push(v);
                }
            }
        }

        Map<Entity, Entity> entityCloneMap = null;
        if(toBeCloned.size() > 0) {
            entityCloneMap = cloneEntities(toBeCloned);
            if(entityCloneMap != null)
                cloneLinks(entityCloneMap);
        }

        return buildGraphModel();
    }

    private Map<Entity, Entity> cloneEntities(Set<Entity> entitiesToBeCloned){
        Map<Entity, Entity> entityCloneMap = null;

        for(Entity entity: entitiesToBeCloned){
            Integer uniqueKey = null;

            // Though the random keys could be large it is the fastest way to generate a unique key without having to sort or use
            // Treemap implementation of the map which would be significantly slower than hashmap

            do{
                uniqueKey = random.nextInt();
            } while (relatedEntity.keySet().contains(new Entity(uniqueKey)));

            if(entityCloneMap == null)
                entityCloneMap = new HashMap<>();

            entityCloneMap.put(entity,addEntity(new Entity(uniqueKey, entity.getName(), Optional.ofNullable(entity.getDescription()))));
        }

        return entityCloneMap;

    }

    private void cloneLinks(Map<Entity, Entity> linksToBeCloned){
        Set<Map.Entry<Entity, Entity>> cloneEntries = linksToBeCloned.entrySet();

        for(Map.Entry<Entity, Entity> entry: cloneEntries){
            Entity existingEntity = entry.getKey();
            Entity clonedEntity = entry.getValue();

            Set<Entity> links = relatedEntity.get(existingEntity);
            Set<Entity> linkEntities = null;

            for(Entity linkEntity: links){
                 if(linkEntities == null)
                     linkEntities = new HashSet<>();

                 Entity linkToBeCloned = linksToBeCloned.get(linkEntity);
                 linkEntities.add(linkToBeCloned);

                 if (links == null){
                     links = new HashSet<>();
                 }
            }

            // Establish a link from the ancestor of the root element to the cloned root element without having to
            // traverse the graph all over again
            if(reverseRelations.containsKey(existingEntity) && !linksToBeCloned.containsKey(reverseRelations.get(existingEntity)) ){
                Entity reverseLink = reverseRelations.get(existingEntity);
                relatedEntity.get(reverseLink).add(linksToBeCloned.get(existingEntity));
            }

            if(linkEntities != null)
                relatedEntity.put(clonedEntity, linkEntities);
        }

    }

    private Optional<GraphModel> buildGraphModel(){
        //System.out.println(" traversing the graph building the graph model ");

        Set<Entity> entities = relatedEntity.keySet();
        Set<Link> links = new LinkedHashSet<>();

        if(entities.size()<1)
            return Optional.empty();

        for(Entity fromEntity: entities){
            for(Entity toEntity: relatedEntity.get(fromEntity)){
                Link link = new Link(fromEntity.getEntityId(), toEntity.getEntityId());
                links.add(link);
            }
        }

        Entity[] entityArray = new Entity[entities.size()];
        Link[] linkArray = new Link[links.size()];

        entities.toArray(entityArray);
        links.toArray(linkArray);

        GraphModel graphModel = new GraphModel(entityArray, linkArray);

        return Optional.of(graphModel);

    }
    private Optional<Entity> getEntity(Entity findEntity) {
        return relatedEntity.keySet().stream().filter(entity -> entity.equals(findEntity)).findFirst();
    }

}
