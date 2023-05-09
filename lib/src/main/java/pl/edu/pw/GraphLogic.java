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
        User user = new User("a","ttr");
        User user2 = new User("b","dda");
        Obligation obligation = new Obligation(user,user2,(double)1);
        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addObligation(obligation);
    }

}
