package pl.edu.pw;

import pl.edu.pw.models.User;

import java.util.List;

public class GraphLogic {
    public List<User> findShortest(User userOne,User userTwo){
        DBConnector dbc = new DBConnector();
        List<User> users = dbc.findShortestPath(userOne,userTwo);
        return users;
    }

}
