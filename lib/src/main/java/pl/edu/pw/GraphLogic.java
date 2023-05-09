package pl.edu.pw;

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


}
