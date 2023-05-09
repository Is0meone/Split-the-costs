package pl.edu.pw;

import org.neo4j.driver.Values;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.LoadStrategy;
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

    Configuration configuration = new Configuration.Builder()
            .uri("neo4j+s://2be25d7b.databases.neo4j.io")
            .credentials("neo4j", "Ob45K7a1DfSQUFb6qI_WFh8edC_epUaAbGxkA7tb26Y")
            .build();
    private static SessionFactory sessionFactory;



    public DBConnector() {
        this.sessionFactory = new SessionFactory(configuration, "pl.edu.pw.models");
        sessionFactory.setLoadStrategy(LoadStrategy.PATH_LOAD_STRATEGY);
    }

    public void addUser(User user) {
        Session session = sessionFactory.openSession();
        try (Transaction tx = session.beginTransaction()) {
            session.save(user,100);
            tx.commit();
        }
    }

    public void addObligation(Obligation obligation){
        Session session = sessionFactory.openSession();
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

    public List<User> findUsersByPrefix(String name) {
        Session session = sessionFactory.openSession();
        try {
            Result result = session.query("MATCH (u:User) WHERE u.name STARTS WITH $name RETURN u",
                    Collections.singletonMap("name", name));

            return StreamSupport.stream(result.spliterator(), false)
                    .map(m -> (User) m.get("u"))
                    .collect(Collectors.toList());
        } catch (Error e) {
            System.out.println("No users with name prefix: " + name);
        }
        return Collections.emptyList();
    }

    public User findUserById(Long id) {
        Session session = sessionFactory.openSession();
        try{
            User user = session.load(User.class,id);
            if(user!=null) return user;
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

    public static void main(String[] args) {
        DBConnector dbc = new DBConnector();
 //       List<User> users = dbc.findShortestPath(dbc.findUserByName("dzbanusz"),dbc.findUserByName("nowyuserek"));
   //     System.out.println(users);

//  //     List<User> list = dbc.getAllUsers();
            dbc.addUser(new User("hujusz", "bajojao"));
//   //     System.out.println(list);
//
//    dbc.addUser(new User("pejusz", "gimp"));
      dbc.addObligation(new Obligation(dbc.findUserById(0L), dbc.findUserById(2L), 420D));
//       System.out.println(dbc.findUserByName("dzbanusz"));
///      System.out.println(dbc.findUserById((long)1));
 //     dbc.findUserById(4L).payObligationTo(dbc.findUserById(2L));
 //      System.out.println(dbc.findUserById(4L));
    }}





