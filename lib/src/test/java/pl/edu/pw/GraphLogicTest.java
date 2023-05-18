package pl.edu.pw;

import org.junit.jupiter.api.Test;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphLogicTest {
    DBConnector dbc = new DBConnector();

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
        GraphLogic logic = new GraphLogic(dbc);
        logic.debtTransfer(obligation2);
        List<Obligation> list = dbc.getAllObligations();
    }

    @Test
    void debtTransfer2() {      //test cyklu
        GraphLogic gl = new GraphLogic(dbc);

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

    /**
     * Test na złożony graf
     */
    @Test
    void debtTransfer3(){
        DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic(dbc);

        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        User user4 = new User("d","daje");
        User user5 = new User("e","wisi/daje");
        User user6 = new User("f","f");
        User user7 = new User("g","g");

        Obligation obligation  = new Obligation(user2,user,(double)100);
        Obligation obligation2  = new Obligation(user3,user,(double)50);
        Obligation obligation3  = new Obligation(user3,user4,(double)25);
        Obligation obligation4  = new Obligation(user6,user4,(double)100);
        Obligation obligation5  = new Obligation(user6,user7,(double)50);
        Obligation obligation6  = new Obligation(user6,user5,(double)100);
        Obligation obligation7  = new Obligation(user2,user5,(double)75);
        Obligation obligation8  = new Obligation(user5,user3,(double)75);


        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addUser(user3);
        dbc.addUser(user4);
        dbc.addUser(user5);
        dbc.addUser(user6);
        dbc.addUser(user7);
        dbc.addObligation(obligation);
        dbc.addObligation(obligation2);
        dbc.addObligation(obligation3);
        dbc.addObligation(obligation4);
        dbc.addObligation(obligation5);
        dbc.addObligation(obligation6);
        dbc.addObligation(obligation7);
        dbc.addObligation(obligation8);
        dbc =null;

        DBConnector dbc2 = new DBConnector();
        dbc2.makeLove();
        dbc2 = null;
        DBConnector dbc3 = new DBConnector();
        int size = dbc3.getAllObligations().size();
        logic.debtTransfer(dbc3.findObligationById(7L));
        assertEquals(size+2,dbc3.getAllObligations().size());
    }
    @Test
    void debtTransfer4(){

        DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic(dbc);
        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        User user4 = new User("g","daje");

        Obligation obligation  = new Obligation(user2,user,(double)25);
        Obligation obligation2  = new Obligation(user2,user3,(double)50);
        Obligation obligation3  = new Obligation(user4,user,(double)50);
        Obligation obligation4  = new Obligation(user3,user4,(double)100);




        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addUser(user3);
        dbc.addUser(user4);

        dbc.addObligation(obligation);
        dbc.addObligation(obligation2);
        dbc.addObligation(obligation3);
        dbc.addObligation(obligation4);

        dbc.createFriend(user2,user);
        dbc.createFriend(user2,user3);
        dbc.createFriend(user4,user);
        dbc.createFriend(user3,user4);

        dbc = null;
        DBConnector dbc2 = new DBConnector();
        int size = dbc2.getAllObligations().size();
        logic.debtTransfer(dbc.findObligationById(3L));
        assertEquals(size,dbc2.getAllObligations().size());

        dbc2.createFriend(dbc2.findUserById(2L),dbc2.findUserById(3L));
        size = dbc2.getAllObligations().size();
        logic.debtTransfer(dbc.findObligationById(3L));
        assertEquals(size+1,dbc2.getAllObligations().size());

    }
}