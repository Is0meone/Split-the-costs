package pl.edu.pw;

import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphLogic {
    public List<User> findShortest(User userOne,User userTwo){
        DBConnector dbc = new DBConnector();
        List<User> users = dbc.findShortestPath(userOne,userTwo);
        return users;
    }
    public static void main(String[] args){
       DBConnector dbc = new DBConnector();
        /*User user = new User("wewe","ttr");
        User user2 = new User("gcb","dda");
        Obligation obligation = new Obligation(user,user2,(double)3);
        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addObligation(obligation);
        */
        User one = dbc.findUserById(17L);
        User two = dbc.findUserById(16L);
        System.out.println(dbc.getAllObligations());
        //System.out.println(dbc.findShortestPath(one,two));
        Obligation obligation = dbc.findObligationBetweenUsers(two,one);
        System.out.println(obligation);
    }

}
