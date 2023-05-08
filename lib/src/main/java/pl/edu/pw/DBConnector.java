package pl.edu.pw;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.ArrayList;
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

    public void addObligation(Obligation obligation){
        Session session = sessionFactory.openSession();
        session.save(obligation);
    }

    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        Iterable<User> users = session.loadAll(User.class);
        return StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Obligation> getAllObligations() {
        Session session = sessionFactory.openSession();
        Iterable<Obligation> obligations = session.loadAll(Obligation.class);
        return StreamSupport.stream(obligations.spliterator(), false)
                .collect(Collectors.toList());
    }
    public static void main(String[] args){
        DBConnector dbc = new DBConnector();

        User Annoz =  new User((long)0, "Annoz", "ol",null, null, null);
        User Dzanusz = new User((long)1, "Dzanusz", "ol",null, null, null);
        Obligation kamilToJacek = new Obligation(Annoz, Dzanusz, 120.0);
         List<Obligation> oblList = new ArrayList<>();
        oblList.add(kamilToJacek);
        Annoz.setOwes(oblList);
        dbc.addObligation(kamilToJacek);
        dbc.addUser(Annoz);
        dbc.addUser(new User((long)1, "Dzanusz", "ol",null, null,
                null));
        dbc.addUser(new User((long)2, "Debil", "ola",null, null,
                null));


      /*  List<Obligation> obligations = new ArrayList<>();

        User kamil = new User((long) 2, "Kamil", "kamil@example.com", null, null, null);
        User jacek = new User((long) 3, "Jacek", "jacek@example.com", null, null, null);
        User pawel = new User((long) 4, "Paweł", "pawel@example.com", null, null, null);

        Obligation kamilToJacek = new Obligation(kamil, jacek, 100.0);
        obligations.add(kamilToJacek);

        Obligation jacekToPawel = new Obligation(jacek, pawel, 50.0);
        obligations.add(jacekToPawel);

        Obligation pawelToKamil = new Obligation(pawel, kamil, 75.0);
        obligations.add(pawelToKamil);

        dbc.addUser(pawel);
        dbc.addUser(jacek);
        dbc.addUser(kamil);

        System.out.println(kamil);*/


        List<User> list = dbc.getAllUsers();
        System.out.println(list);

        List<Obligation> obligations = dbc.getAllObligations();
        System.out.println(obligations);
    }




}
