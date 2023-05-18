package pl.edu.pw;

import org.junit.jupiter.api.Test;
import pl.edu.pw.models.Friendship;
import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Jakie testy mamy:
 *  -klasyka z DebtorIsOwed i CreditorOwes
 *  -pętla stabilna
 *  -friendshipy!!!
 *  -split OK
 */

class GraphLogicTest {

    @Test
    void getActiveCreditors() {

    }

    @Test
    void debtTransfer() {
        DBConnector dbcT = new DBConnector();
        GraphLogic logicT = new GraphLogic(dbcT);
        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        User user4 = new User("d","daje");
        User user5 = new User("e","wisi/daje");
        User user6 = new User("f","f");
        Obligation obligation = new Obligation(user,user2,(double)100);
        Obligation obligation2 = new Obligation(user2,user3,(double)50);
        dbcT.addUser(user);
        dbcT.addUser(user2);
        dbcT.addUser(user3);
        dbcT.addUser(user4);
        dbcT.addUser(user5);
        dbcT.addUser(user6);
        dbcT.makeLove();

        dbcT.addObligation(obligation);
        dbcT.addObligation(obligation2);


        int size = dbcT.getAllObligations().size();
        logicT.debtTransfer(dbcT.findObligationBetweenUsers(user2,user3));
        assertEquals(size+1,dbcT.getAllObligations().size());
        assertEquals(Obligation.Status.AUTOPAID,dbcT.findObligationBetweenUsers(user2,user3).getStatus());




        Obligation obligation3  = new Obligation(user4,user5,(double)30);
        dbcT.addObligation(obligation3);
        Obligation obligation4 = new Obligation(user4,user,(double)25);
        dbcT.addObligation(obligation4);

        dbcT = null;
        logicT = null;

        DBConnector dbcT2 = new DBConnector();
        GraphLogic logicT2 = new GraphLogic(dbcT2);

        size = dbcT2.getAllObligations().size();
        logicT2.debtTransfer(dbcT2.findObligationBetweenUsers(user4,user));

        assertEquals(size+1,dbcT2.getAllObligations().size());
        assertEquals(Obligation.Status.AUTOPAID,dbcT2.findObligationBetweenUsers(user4,user).getStatus());

        Obligation obligation5 = new Obligation(dbcT2.findUserById(5L),dbcT2.findUserById(3L),(double)130);
        dbcT2.addObligation(obligation5);


        size = dbcT2.getAllObligations().size();
        logicT2.debtTransfer(dbcT2.findObligationBetweenUsers(dbcT2.findUserById(5L),dbcT2.findUserById(3L)));
        assertEquals(size+2,dbcT2.getAllObligations().size());
        assertEquals(75D, dbcT2.findObligationBetweenUsers(user6,user4).getAmount());
    }

    @Test
    void debtTransfer2() {      //test cyklu
        DBConnector dbcT = new DBConnector();
        GraphLogic gl = new GraphLogic(dbcT);

        User user = dbcT.findUserByName("adam");
        User user2 = dbcT.findUserByName("bartek");
        User user3 = dbcT.findUserByName("cyryl");

        dbcT.addObligation(new Obligation(user, user2, 100D, Obligation.Status.ACCEPTED));
        gl.debtTransfer(user.getIsOwed().get(0));
        dbcT.addObligation(new Obligation(user2, user3, 50D, Obligation.Status.ACCEPTED));
        gl.debtTransfer(user2.getIsOwed().get(0));
        dbcT.addObligation(new Obligation(user3, user, 50D, Obligation.Status.ACCEPTED));
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
        DBConnector dbcT = new DBConnector();

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


        dbcT.addUser(user);
        dbcT.addUser(user2);
        dbcT.addUser(user3);
        dbcT.addUser(user4);
        dbcT.addUser(user5);
        dbcT.addUser(user6);
        dbcT.addUser(user7);
        dbcT.addObligation(obligation);
        dbcT.addObligation(obligation2);
        dbcT.addObligation(obligation3);
        dbcT.addObligation(obligation4);
        dbcT.addObligation(obligation5);
        dbcT.addObligation(obligation6);
        dbcT.addObligation(obligation7);
        dbcT.addObligation(obligation8);
        dbcT =null;

        DBConnector dbcT2 = new DBConnector();
        dbcT2.makeLove();
        dbcT2 = null;
        DBConnector dbcT3 = new DBConnector();
        GraphLogic logicT = new GraphLogic(dbcT3);
        int size = dbcT3.getAllObligations().size();
        List<Friendship> friendships = dbcT3.getAllFriendships();
        logicT.debtTransfer(dbcT3.findObligationById(7L));
        assertEquals(size+3,dbcT3.getAllObligations().size());
        assertEquals(0,dbcT3.findUserByName("c").getActiveObligation().size());
    }
    @Test
    void debtTransfer4(){
//raczej zly test
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

    @Test
    void splitTest(){

        DBConnector dbcT = new DBConnector();
        GraphLogic logic = new GraphLogic(dbcT);

        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        User user4 = new User("d","wisi");
        dbcT.addUser(user);
        dbcT.addUser(user2);
        dbcT.addUser(user3);
        dbcT.addUser(user4);
        dbcT.makeLove();
        dbcT =null;

        DBConnector dbcT2 = new DBConnector();

        List<User> toSplit = new ArrayList<>();
        User userD = dbcT2.findUserById(0L);
        User user2D = dbcT2.findUserById(1L);
        User user3D = dbcT2.findUserById(2L);
        User user4D = dbcT2.findUserById(3L);
        ExpenseSplitter splitter = new ExpenseSplitter(userD,dbcT2);

        toSplit.add(user2D);
        toSplit.add(user3D);
        toSplit.add(user4D);
        splitter.split(300D,toSplit);
        Obligation obligation = new Obligation(dbcT2.findUserById(2L),dbcT2.findUserById(0L),150D);
        dbcT2.addObligation(obligation);

        dbcT2 = null;

        DBConnector dbcT3 = new DBConnector();
        GraphLogic logicT2 = new GraphLogic(dbcT3);

        Obligation obligation2 = dbcT3.findObligationById(12L);
        obligation2.setStatus(Obligation.Status.ACCEPTED);
        dbcT3.addObligation(obligation2);

        int size = dbcT3.getAllObligations().size();
        Obligation obligationToTest = dbcT3.findObligationBetweenUsers(dbcT3.findUserById(0L),dbcT3.findUserById(2L));
        logicT2.debtTransfer(obligationToTest);
        assertEquals(Obligation.Status.AUTOPAID,obligationToTest.getStatus());
        assertEquals(size+1,dbcT3.getAllObligations().size());
    }
}