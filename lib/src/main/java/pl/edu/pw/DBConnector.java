package pl.edu.pw;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import pl.edu.pw.models.User;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public void addObligation(User user){
        Session session = sessionFactory.openSession();
        session.save(user);
    }

    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        Iterable<User> users = session.loadAll(User.class);
        return StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
    }

    public static void main(String[] args){
        DBConnector dbc = new DBConnector();
        dbc.addUser(new User((long)1, "kanusz", "lol", null, null,
                null));
        List<User> list = dbc.getAllUsers();
        System.out.println(list);
    }




}
