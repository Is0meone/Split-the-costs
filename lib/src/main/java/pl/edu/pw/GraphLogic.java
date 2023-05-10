package pl.edu.pw;

import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphLogic {
    public List<User> findShortest(User userOne,User userTwo){
        DBConnector dbc = new DBConnector();
        List<User> users = dbc.findShortestPath(userOne,userTwo);
        return users;
    }
    public List<Obligation> getActiveCreditors(Obligation obligation) {
        return obligation.getCreditor().getOwes()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID)
                .collect(Collectors.toList());
    }
    /*
    *This func should be used after every accepted obligation to keep the graph as simpe as possible
    * Trzeba dodać:
    * rozważanie statusu obligacji/ znajomych/ "archiwizowanie" obligacji(Latwe)
    * sprawdzenie dzialania dla dużych struktur i ogarnięcie splitu
     */
    public void debtTransfer(User creditor, User debtor, Obligation obligation){
        DBConnector dbc = new DBConnector();
        List<Obligation> CredList = getActiveCreditors(obligation); //lista komu wisi
        int i =0;
        while (obligation.getAmount()>0&&i<CredList.size()){
            double DebtToPay = CredList.get(i).getAmount();
            double margin = DebtToPay - obligation.getAmount();
            if(margin>=0){ //wszystko splaci w jednym
                CredList.get(i).setAmount(DebtToPay-obligation.getAmount());
                Obligation transferedDebt = new Obligation(CredList.get(i).getCreditor(),obligation.getDebtor(),obligation.getAmount());
                obligation.pay();
                dbc.addObligation(CredList.get(i));
                dbc.addObligation(obligation);
                dbc.addObligation(transferedDebt);
            }
            else{
                CredList.get(i).pay();
                obligation.setAmount(obligation.getAmount()-DebtToPay);
                Obligation transferedDebt = new Obligation(CredList.get(i).getCreditor(),obligation.getDebtor(),DebtToPay);
                dbc.addObligation(CredList.get(i));
                dbc.addObligation(obligation);
                dbc.addObligation(transferedDebt);
            }
            i++;
        }
    }
    public static void main(String[] args){
       DBConnector dbc = new DBConnector();
        /*User user = new User("wewe","ttr");
        User user2 = new User("gcb","dda");
        Obligation obligation = new Obligation(user,user2,(double)3);
        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addObligation(obligation);

        User one = dbc.findUserById(17L);
        User two = dbc.findUserById(16L);
        System.out.println(dbc.getAllObligations());
        //System.out.println(dbc.findShortestPath(one,two));
        Obligation obligation = dbc.findObligationBetweenUsers(two,one);
        System.out.println(obligation);


        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        Obligation obligation = new Obligation(user,user2,(double)100);
        Obligation obligation2 = new Obligation(user2,user3,(double)50);
        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addUser(user3);
        dbc.addObligation(obligation);
        dbc.addObligation(obligation2);
        */

/*
        Obligation ott = dbc.findObligationById(1L);
        GraphLogic logic = new GraphLogic();
        logic.debtTransfer(null,null,ott);
        List<Obligation> list = dbc.getAllObligations();
        System.out.println(list);
 */
        User user3 = new User("d","kolejny");
        User user2 = dbc.findUserById(2L);
        Obligation obligation3 = new Obligation(user2,user3,(double)25);
        dbc.addUser(user3);
        dbc.addObligation(obligation3);
        GraphLogic logic = new GraphLogic();
        logic.debtTransfer(null,null,obligation3);
        List<Obligation> list = dbc.getAllObligations();
        System.out.println(list);

    }

}
