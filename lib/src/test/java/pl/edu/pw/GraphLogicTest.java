package pl.edu.pw;

import org.junit.jupiter.api.Test;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphLogicTest {

    @Test
    void getActiveCreditors() {
    }

    @Test
    void debtTransfer() {
        DBConnector dbc = new DBConnector();
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
        logic.debtTransfer(null,null,obligation2);
        List<Obligation> list = dbc.getAllObligations();

    }
}