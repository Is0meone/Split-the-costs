package pl.edu.pw.models;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnector implements AutoCloseable {

    Configuration configuration = new Configuration.Builder()
            .uri("neo4j+s://2be25d7b.databases.neo4j.io")
            .credentials("neo4j", "Ob45K7a1DfSQUFb6qI_WFh8edC_epUaAbGxkA7tb26Y")
            .build();

    private static final Logger LOGGER = Logger.getLogger(pl.edu.pw.testyBZ.class.getName());
    private SessionFactory sessionFactory;


    public DBConnector() {
        this.sessionFactory = new SessionFactory(configuration, "pl.edu.pw.models");
    }
    @Override
    public void close() {

    }
    public void addUser(User user){
        Session session = sessionFactory.openSession();
        session.save(user);
    }

    public static void main(String[] args){
        DBConnector dbc = new DBConnector();
        dbc.addUser(new User((long)0, "kanusz", "lol", null, null,
                null));
    }




}
