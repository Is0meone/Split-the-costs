package pl.edu.pw;

import org.neo4j.driver.Values;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.query.CypherQuery;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import pl.edu.pw.models.Friendship;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DBConnector {


    private static SessionFactory sessionFactory;


    public DBConnector() {
        Configuration configuration = new Configuration.Builder()
                .uri("neo4j+s://2be25d7b.databases.neo4j.io")
                .credentials("neo4j", "Ob45K7a1DfSQUFb6qI_WFh8edC_epUaAbGxkA7tb26Y")
                .build();

        this.sessionFactory = new SessionFactory(configuration, "pl.edu.pw.models");
    }
    public DBConnector(String test) {
        Configuration configuration = new Configuration.Builder()
                .uri("neo4j+s://ea165f78.databases.neo4j.io")
                .credentials("neo4j", "KrSTu_-Wz_9HKTAhbdLuqvEHOhjXqTwB7I7_JVAQvuw")
                .build();

        this.sessionFactory = new SessionFactory(configuration, "pl.edu.pw.models");
    }

    public void addUser(User user) {
        Session session = sessionFactory.openSession();
        try (Transaction tx = session.beginTransaction()) {
            session.save(user);
            tx.commit();
        }
    }
    public void updateUser(User user){
        Session session = sessionFactory.openSession();
        try (Transaction tx = session.beginTransaction()) {
            session.save(user);
            tx.commit();
        }
    }
    public void addObligation(Obligation obligation){
        Session session = sessionFactory.openSession();
        unNullifier(obligation.getDebtor());
        unNullifier(obligation.getCreditor());
        obligation.getDebtor().addOwes(obligation);
        obligation.getCreditor().addOwed(obligation);

        session.save(obligation.getCreditor(),10);
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

    public void deleteObligation(Long id) { //this func is just for having a better view of large database
        Session session = sessionFactory.openSession();
        try (Transaction tx = session.beginTransaction()) {
            Obligation obligation = session.load(Obligation.class, id);
            session.delete(obligation);
            tx.commit();
        }
    }

    public Obligation findObligationById(Long id){
        Session session = sessionFactory.openSession();
        try{
            Obligation obligation = session.load(Obligation.class,id);
            return obligation;
        }
        catch(Error e){
            System.out.println("no user with id " + id);
        }
        return null;
    }

    public User findUserByName(String name) {
        Session session = sessionFactory.openSession();
        try{
            User user = session.queryForObject(User.class, "MATCH (u:User) WHERE u.name = $name RETURN u", Map.of("name", name));
            user = session.load(User.class, user.getId());
            unNullifier(user);
            return user;
        }
        catch(Error e){
            System.out.println("no user with name " + name);
        }
        return null;
    }


    public List<User> findUsersByPrefix(String name) {
        Session session = sessionFactory.openSession();
        try {
            Result result = session.query("MATCH (u:User) WHERE u.name STARTS WITH $name RETURN u",
                    Collections.singletonMap("name", name));

            List<User> users = new ArrayList<>();
            for (Map<String, Object> row : result) {
                User user = (User) row.get("u");
                Long id = user.getId();
                User loadedUser = session.load(User.class, id);
                unNullifier(loadedUser);
                users.add(loadedUser);
            }
            return users;
        } catch (Exception e) {
            System.out.println("No users with name prefix: " + name);
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public User findUserById(Long id) {
        Session session = sessionFactory.openSession();
        try{
            User user = session.load(User.class,id);

            if(user!=null) {
                unNullifier(user);
                return user;
            }
            else throw new NoSuchElementException();
        }
        catch(Error e){
            System.out.println("no user with id " + id);
        }
        return null;
    }

     public List<User> findShortestPath(User user1, User user2) {
        String user1Name = user1.getName();
        String user2Name = user2.getName();
        Session session = sessionFactory.openSession();
        String q = "MATCH (u1:User {name: $user1Name}), (u2:User {name: $user2Name}), " +
                "p = shortestPath((u1)-[*]-(u2)) " +
                "RETURN nodes(p) as nodes";
        Map<String, String> map = new HashMap<>();
        map.put("user1Name", user1Name);
        map.put("user2Name", user2Name);
        Result result = session.query(q, map);
        List<User> users = new ArrayList<>();
        for (Map<String, Object> x : result.queryResults()) {
            List<User> nodes = (List<User>) x.get("nodes");
            users.addAll(nodes);
        }
        if (users.isEmpty()) {
            System.out.println("No path found between users " + user1Name + " and " + user2Name);
        }
        return users;
    }

    public Obligation findObligationBetweenUsers(User user1, User user2) {
       Session session = sessionFactory.openSession();
       List<Obligation> list = getAllObligations();
        for (Obligation o : list ) {
            if(o.getCreditor().equals(user1) && o.getDebtor().equals(user2)){
                return o;
            }
        }
        return null;
    }

    public void unNullifier(User user){
        if(user.getIsOwed() == null) user.setIsOwed(new ArrayList<>());
        if(user.getOwes() == null) user.setOwes(new ArrayList<>());
        if(user.getFriendsWith() == null) user.setFriendsWith(new ArrayList<>());
    }
    public void payObligation(Obligation obligation) {
        Session session = sessionFactory.openSession();
        obligation.pay();
        try (Transaction tx = session.beginTransaction()) {
            session.save(obligation);
            tx.commit();
        }
    }


    public void dropDatabase() { //nie dotykac
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String query = "MATCH (n) DETACH DELETE n";
        session.query(query, Collections.emptyMap());
        transaction.commit();
    }



    public static void main(String[] args) {
        DBConnector dbc = new DBConnector();
      dbc.addObligation(new Obligation(dbc.findUserByName("pugalak"), dbc.findUserByName("janusz"), 50D));
//  //     List<User> list = dbc.getAllUsers();
 //           dbc.addUser(new User("pugalak", "bajojao"));
//   //     System.out.println(list);
//            User user = dbc.findUserByName("pudlak");
//            user.payObligationTo(dbc.findUserByName("WLADCATYCHNAP"));
//            System.out.println(user.getOwes());
//    dbc.addUser(new User("pejusz", "gimp"));
   //     ExpenseSplitter es = new ExpenseSplitter(dbc.findUserByName("WLADCATYCHNAP"));
   //     es.split(2137420D, dbc.findUsersByPrefix("pu"));
    //    System.out.println(dbc.findUserById(7L).getOwes());
//       System.out.println(dbc.findUserByName("dzbanusz"));
///      System.out.println(dbc.findUserById((long)1));
 //     dbc.findUserById(4L).payObligationTo(dbc.findUserById(2L));
 //      System.out.println(dbc.findUserById(4L));
    }}
/*
TODO:
-friendship
    -wyslij zapro / zaakceptuj gdy z drugiej strony jest juz wyslane
    -odrzuc zapro
    -zrobic autoaccept -> akceptuj automatycznie wszystkie obligacje
-logika
    -wszyscy posredni dluznicy (najdluzsza sciezka w grafie)
    -wszyscy posredni kredytodawcy (najdluzsza sciezka w grafie)
-
 */


