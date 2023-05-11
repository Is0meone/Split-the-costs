package pl.edu.pw;

import org.junit.jupiter.api.Test;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphLogicTest {
    DBConnector dbc = new DBConnector("test");

    @Test
    void getActiveCreditors() {
        dbc.addUser(new User("adam","daje"));
        dbc.addUser(new User("bartek","daje"));
        dbc.addUser(new User("cyryl","daje"));
    }

    @Test
    void debtTransfer() {
        User user = new User("aTestowe","daje");
        User user2 = new User("bTestowe","wisi/daje");
        User user3 = new User("cTestowe","wisi");
        Obligation obligation = new Obligation(user,user2,(double)100);
        Obligation obligation2 = new Obligation(user2,user3,(double)50);
        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addUser(user3);
        dbc.addObligation(obligation);
        dbc.addObligation(obligation2);
        GraphLogic logic = new GraphLogic();
        logic.debtTransfer(obligation2);
        List<Obligation> list = dbc.getAllObligations();
    }

    @Test
    void debtTransfer2() {      //test cyklu
        GraphLogic gl = new GraphLogic();

        User user = dbc.findUserByName("adam");
        User user2 = dbc.findUserByName("bartek");
        User user3 = dbc.findUserByName("cyryl");

        dbc.addObligation(new Obligation(user, user2, 100D, Obligation.Status.ACCEPTED));
        gl.debtTransfer(user.getIsOwed().get(0));
        dbc.addObligation(new Obligation(user2, user3, 50D, Obligation.Status.ACCEPTED));
        gl.debtTransfer(user2.getIsOwed().get(0));
        dbc.addObligation(new Obligation(user3, user, 50D, Obligation.Status.ACCEPTED));
        gl.debtTransfer(user3.getIsOwed().get(0));





    //    assertEquals(80D, user.getIsOwed().get(0).getAmount());
        assertEquals(30D, user2.getIsOwed().get(0).getAmount());
        assertEquals(Obligation.Status.PAID,user3.getIsOwed().get(0).getStatus());
    }

}