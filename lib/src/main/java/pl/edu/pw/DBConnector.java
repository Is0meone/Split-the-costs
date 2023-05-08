package pl.edu.pw;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import pl.edu.pw.models.Friendship;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DBConnector {

    Configuration configuration = new Configuration.Builder()
            .uri("neo4j+s://2be25d7b.databases.neo4j.io")
            .credentials("neo4j", "Ob45K7a1DfSQUFb6qI_WFh8edC_epUaAbGxkA7tb26Y")
            .build();
    private SessionFactory sessionFactory;


    public DBConnector() {
        this.sessionFactory = new SessionFactory(configuration, "pl.edu.pw.models");
    }

    public void addUser(User user) {
        Session session = sessionFactory.openSession();
        try (Transaction tx = session.beginTransaction()) {
            session.save(user);
            tx.commit();
        }
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

    public User findUserByName(String name) {
        Session session = sessionFactory.openSession();
        try{
            return session.queryForObject(User.class, "MATCH (u:User) WHERE u.name = $name RETURN u", Map.of("name", name));
        }
        catch(Error e){
            System.out.println("no user with name " + name);
        }
        return null;
    }


    public User findUserById(Long id) {
        Session session = sessionFactory.openSession();
        try{
            return session.queryForObject(User.class, "MATCH (u:User) WHERE u.id = $id RETURN u", Map.of("id", id));
        }
        catch(Error e){
            System.out.println("no user with id " + id);
        }
        return null;
    }

//    public static void main(String[] args){
//        DBConnector dbc = new DBConnector();
//  //    dbc.addUser(new User("gejusz", "lol"));
//  //     List<User> list = dbc.getAllUsers();
//
//   //     System.out.println(list);
//
////        dbc.addUser(new User((long)1, "janusz", "lol", null, null, null));
// //     dbc.addObligation(new Obligation());
//       System.out.println(dbc.findUserByName("dzbanusz"));
////        System.out.println(dbc.findUserById((long)1));
//    }





}
