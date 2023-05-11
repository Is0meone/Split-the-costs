package pl.edu.pw;

import org.junit.jupiter.api.Test;
import pl.edu.pw.models.User;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectorTest {

    DBConnector dbc = new DBConnector("test");

    @Test
    void addUser() {

    }

    @Test
    void updateUser() {
    }

    @Test
    void addObligation() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void getAllObligations() {
    }

    @Test
    void deleteObligation() {
    }

    @Test
    void findObligationById() {
    }

    @Test
    void findUserByName() {
        User user = new User("newUser2","dupa");
        dbc.addUser(user);
        assertEquals(dbc.findUserByName("newUser2"), user);
    }

    @Test
    void findUsersByPrefix() {
    }

    @Test
    void findUserById() {

    }

    @Test
    void findShortestPath() {
    }

    @Test
    void findObligationBetweenUsers() {
    }

    @Test
    void unNullifier() {
    }

    @Test
    void payObligation() {
    }

}