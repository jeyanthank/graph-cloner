package com.workspan.cloner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workspan.cloner.model.GraphModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Cloner {

    public static void main(String[] args) throws IOException {

        if(args.length < 2) {
            System.out.println(" Please supply input file name. Ensure the input file is in the current folder ");
            System.out.println(" Please supply root element to be cloned ");
            return;
        }

        // Get a handle of the Graph object and initialize
        Graph.intialize(args[0]);

        // Clone
        try {
            Optional<GraphModel> outputGraph = Graph.getInstance().findAndClone(Integer.parseInt(args[1]));

            // Write to the output file
            if (outputGraph.isPresent()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String outputJson = gson.toJson(outputGraph.get(), GraphModel.class);
                Files.write(Paths.get("output.txt"), outputJson.getBytes());
            }
        } catch (NumberFormatException e){
            System.out.println(" Invalid number ");
        }
        //System.out.println(outputJson);

    }

}
