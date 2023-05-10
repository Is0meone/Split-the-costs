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
    public List<Obligation> getActiveCreditorOwes(Obligation obligation) {//to dziala tylko na cykle
       if(obligation.getCreditor().getOwes() != null){
        return obligation.getCreditor().getOwes()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID)
                .collect(Collectors.toList());
    }
    else return null;
    }

    public List<Obligation> getActiveDebtorOwes(Obligation obligation) {
        if(obligation.getCreditor().getOwes() != null){
        return obligation.getDebtor().getOwes()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID)
                .collect(Collectors.toList());
        }
        else return null;
    }
    public List<Obligation> getActiveCreditorisOwned(Obligation obligation){
        if(obligation.getCreditor().getIsOwed() != null){
        return obligation.getCreditor().getIsOwed()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID)
                .collect(Collectors.toList());
        }
        else return null;
    }
    public List<Obligation> getActiveDebtorisOwned(Obligation obligation){
        if(obligation.getCreditor().getIsOwed() != null){
        return obligation.getDebtor().getIsOwed()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID)
                .collect(Collectors.toList());
        }
        else return null;
    }

    /*
    *This func should be used after every accepted obligation to keep the graph as simpe as possible
    * Trzeba dodać:
    * rozważanie statusu obligacji/ znajomych/ "archiwizowanie" obligacji(Latwe)
    * sprawdzenie dzialania dla dużych struktur i ogarnięcie splitu
     */
    public void debtTransfer(User creditor, User debtor, Obligation obligation){
        DBConnector dbc = new DBConnector();

        if(transferLogic(obligation,getActiveCreditorOwes(obligation))==true) return;
        if(transferLogic(obligation,getActiveDebtorOwes(obligation))==true) return;
        if(transferLogic(obligation,getActiveCreditorisOwned(obligation))==true) return;
        if(transferLogic(obligation,getActiveDebtorisOwned(obligation))==true) return;

    }
    public boolean transferLogic(Obligation obligation,List<Obligation> ListToCheck){
        DBConnector dbc = new DBConnector();
        int i =0;
        if(ListToCheck == null) return false;
        while (i<ListToCheck.size()&&!ListToCheck.get(i).equals(obligation)){//kurczaki trzeba dodac zeby sie zatrzymywalo jak dlug zejdzie caly
            double DebtToPay = ListToCheck.get(i).getAmount();
            double margin = DebtToPay - obligation.getAmount();
            if(margin>=0){ //wszystko splaci w jednym
                ListToCheck.get(i).setAmount(DebtToPay-obligation.getAmount());
                obligation.pay();
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);

                if(!ListToCheck.get(i).getCreditor().equals(obligation.getDebtor())){
                    Obligation transferedDebt = new Obligation(ListToCheck.get(i).getCreditor(),obligation.getDebtor(),obligation.getAmount());
                    dbc.addObligation(transferedDebt);
                }
                return true;
            }
            else{
                ListToCheck.get(i).pay();
                obligation.setAmount(obligation.getAmount()-DebtToPay);
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);
                if(!ListToCheck.get(i).getCreditor().equals(obligation.getDebtor())) {
                    Obligation transferedDebt = new Obligation(ListToCheck.get(i).getCreditor(),obligation.getDebtor(),DebtToPay);
                    dbc.addObligation(transferedDebt);
                }
            }
            i++;
        }
        return false;
    }
    public static void getCleanObl(List<Obligation> obligationsList){
        for (Obligation o: obligationsList) {
            if(o.getStatus()==null){
                System.out.println("Obligacja nr: "+o.getId() +" " +o.getCreditor().getName()+" <- "+ o.getDebtor().getName()+" value "+o.getAmount());
            }
        }
    }
    public static void main(String[] args){
        DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic();

        logic.debtTransfer(null,null,dbc.findObligationById(4L));

        logic.getCleanObl(dbc.getAllObligations());
    }

}
/*
              DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic();
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
        logic.debtTransfer(null,null,obligation2);

        User user4 = new User("d","daje");
        User user5 = new User("e","wisi/daje");
        dbc.addUser(user4);
        dbc.addUser(user5);

        Obligation obligation3  = new Obligation(user4,user5,(double)30);
        dbc.addObligation(obligation3);
        Obligation obligation4 = new Obligation(user,user4,(double)25);
        dbc.addObligation(obligation4);

        logic.getCleanObl(dbc.getAllObligations());
 */

/*        dbc.deleteObligation(4L);
        dbc.deleteObligation(5L);

         Obligation obligation4 = new Obligation(dbc.findUserByName("a"),dbc.findUserByName("d"),(double)25);

        */