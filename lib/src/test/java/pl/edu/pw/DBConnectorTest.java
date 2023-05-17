package pl.edu.pw;

import org.junit.jupiter.api.Test;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectorTest {

    DBConnector dbc = new DBConnector(1);

    @Test
    void addUser() {
        User user  = new User("user1","jkl");
        List<User> users1 = dbc.getAllUsers();
        dbc.addUser(user);
        List<User> users2 = dbc.getAllUsers();
        assertEquals(users1.size()+1,users2.size());
    }
    @Test
    void addObligation() {
        User user1 = new User("asd","jkl");
        User user2 = new User("fgh","jkl");
        User user3 = new User("jkl","jkl");
        dbc.addObligation(new Obligation(user3, user2, 100D, Obligation.Status.ACCEPTED));
        dbc.addObligation(new Obligation(user2, user1, 200D, Obligation.Status.PAID));
        dbc.addObligation(new Obligation(user1, user3, 300D, Obligation.Status.PENDING));

        assertEquals(Obligation.Status.PENDING,user1.getIsOwed().get(0).getStatus());
        assertNotEquals(Obligation.Status.DECLINED,user2.getIsOwed().get(0).getStatus());
        assertEquals(Obligation.Status.PAID,user2.getIsOwed().get(0).getStatus());
        assertEquals(Obligation.Status.ACCEPTED,user3.getIsOwed().get(0).getStatus());
        assertEquals(200D,user2.getIsOwed().get(0).getAmount());
        assertEquals(300D,user1.getIsOwed().get(0).getAmount());
        assertNotEquals(50D,user3.getIsOwed().get(0).getAmount());
        assertEquals(100D,user3.getIsOwed().get(0).getAmount());
    }

    @Test
    void getAllUsers() {
        List<User> users = dbc.getAllUsers();
        dbc.addUser(new User("a","jkl"));
        dbc.addUser(new User("b","jkl"));
        dbc.addUser(new User("c","jkl"));
        assertEquals(dbc.getAllUsers().size(),users.size() + 3);
    }

    @Test
    void getAllObligations() {
        List<Obligation> obligationList = dbc.getAllObligations();
        dbc.addObligation(new Obligation(new User("q","jkl"), new User("w","jkl"), 50D, Obligation.Status.ACCEPTED));
        dbc.addObligation(new Obligation(new User("e","jkl"), new User("r","jkl"), 100D, Obligation.Status.ACCEPTED));
        dbc.addObligation(new Obligation(new User("t","jkl"), new User("y","jkl"), 150D, Obligation.Status.ACCEPTED));
        List<Obligation> obligationList2 = dbc.getAllObligations();
        assertEquals(obligationList.size()+3,obligationList2.size());
    }

    @Test
    void deleteObligation() {
        User user1 = new User("u","jkl");
        User user2 = new User("i","jkl");
        dbc.addObligation(new Obligation(user1,user2, 30D, Obligation.Status.ACCEPTED));
        List<Obligation> obligationList = dbc.getAllObligations();
        dbc.deleteObligation(dbc.findObligationBetweenUsers(user1,user2).getId());
        List<Obligation> obligationList2 = dbc.getAllObligations();
        assertEquals(obligationList.size(),obligationList2.size()+1);
        assertNotEquals(obligationList.size(),obligationList2.size());
    }

    @Test
    void findUsersByPrefix() {
        User user = dbc.findUserByName("adam");
        User user2 = new User("ada","jkl");
        //dbc.addUser(user2); dodano 2 razy
        // lista użytkowników z prefiksem "ad" = {adam, ada, ada}
        assertEquals(dbc.findUsersByPrefix("ad").size(),3);
        assertNotEquals(dbc.findUsersByPrefix("ad").size(),2);
    }

    @Test
    void findUserById() {
        User user = dbc.findUserByName("bartek");
        System.out.println(dbc.findUserByName("bartek").getId()); //user id = 2, w bazie danych też faktycznie ma wartość 2
        assertEquals(user.getId(),2);
    }

    @Test
    void findShortestPath() {
        User user = dbc.findUserByName("usertestowy");
        User user1 = dbc.findUserByName("bartek");
        // ścieżka od usertestowy do bartek według grafu z bazy danych: usertestowy -> ktos -> adam -> bartek
        List<User> users = new ArrayList<>();
        users.add(dbc.findUserByName("usertestowy"));
        users.add(dbc.findUserByName("ktos"));
        users.add(dbc.findUserByName("adam"));
        users.add(dbc.findUserByName("bartek"));
        assertEquals(dbc.findShortestPath(user,user1),users);
    }

    @Test
    void findObligationBetweenUsers() {
        User user1 = new User("zxc","jkl");
        User user2 = new User("vbn","jkl");
        Obligation obl = new Obligation(user1, user2, 350D, Obligation.Status.ACCEPTED);
        dbc.addObligation(obl);// za pierwszym razem została dodana, wykomentowane bo kolejne by zepsuły
        assertEquals(dbc.findObligationBetweenUsers(user1,user2),obl);
    }

    @Test
    void unNullifier() {
        User user = new User("bob","jkl");
        dbc.unNullifier(user);
        assertNotNull(user.getIsOwed());
        assertNotNull(user.getFriendsWith());
        assertNotNull(user.getOwes());
    }

    @Test
    void payObligation() {
        User user1 = new User("qwe","jkl");
        User user2 = new User("rty","jkl");
        Obligation obl = new Obligation(user1,user2,100D, Obligation.Status.ACCEPTED);
        dbc.addObligation(obl); // dodajemy obligację obl
        dbc.payObligation(obl); // spłacamy obligację obl
        assertEquals(obl.getStatus(), Obligation.Status.PAID);
    }

}