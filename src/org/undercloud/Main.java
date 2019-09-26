package org.undercloud;
import java.util.ArrayList;

// librerias de mongo
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

// libreria para manejar el JSON
import org.bson.Document;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        // palabras claves de búsqueda
        String topic = "(gun control) OR (guns) OR (weapons) OR (NRA) OR (Second Amendment) OR (Asociación Nacional del Rifle) OR (mass shooting) OR (ndamendmen) OR (el paso shooting) OR (mass shooing) OR (march for our lives)";

        // extraemos los tweets utilizando la API Search de Twitter
        ArrayList<HashMap<String, String>> tweets = tweetManager.getTweets(topic);

        // Comenzamos la librería del NPL
        NLP.init();

        // Comenzamos las conecciones de Mongo a nuestra maquina local, base de datos que se llame twitter y modelo o tabla que se llama npl
        MongoClient mongoClient = new MongoClient( "localhost" );
        MongoDatabase database = mongoClient.getDatabase("twitter");
        MongoCollection<Document> collection = database.getCollection("npl");

        // iniciamos un documento de mongo
        Document doc;

        // hacemos un for por todos los tweets extraidos por la API Search de twitter
        for(HashMap<String, String> tweet : tweets) {
            // por cada resultado hacemos un nuevo documento de MongoDB
            doc = new Document();
            Document finalDoc = doc;

            // Cada tweet lo insertamos en un Hasmap por lo que necesitamos meter todos los Hash dentro de nuestro documento de MongoDB
            tweet.forEach((key, value) -> {
                System.out.println("key: " + key + " value:" + value);
                finalDoc.append(key, value);
            });

            // Añadimos a nuestro documento el score del NPL
            finalDoc.append("score", NLP.findSentiment(finalDoc.get("tweet").toString()));

            // Se inserta el documento a la base de datos de MongoDB
            collection.insertOne(finalDoc);
        }
    }
}
