package pl.edu.pw;

import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphLogic {
    private DBConnector dbc;
    public GraphLogic(DBConnector dbc){
        this.dbc=dbc;
    }

    public List<User> findShortest(User userOne,User userTwo){
        List<User> users = dbc.findShortestPath(userOne,userTwo);
        return users;
    }
    public List<Obligation> getActiveCreditorOwes(Obligation obligation) {
        if(obligation.getCreditor().getOwes() != null){
            //TODO jeśli chcemy uwzględniać Friendship to odkomentować i zmienić liste podawaną do strumienia
            List<Obligation> list = obligation.getCreditor().getOwes();
            List<Obligation> justFriends = new ArrayList<>();
            for (Obligation obl: list) {
                if(obl.getCreditor().isFriend(obligation.getDebtor())){
                    justFriends.add(obl);
                }
            }
            return obligation.getCreditor().getOwes()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID&&o.getStatus() != Obligation.Status.AUTOPAID)
                .collect(Collectors.toList());
    }
    else return null;
    }
    public List<Obligation> getActiveDebtorisOwned(Obligation obligation){
        //TODO jeśli chcemy uwzględniać Friendship to odkomentować i zmienić liste podawaną do strumienia
        List<Obligation> list = obligation.getDebtor().getIsOwed();
        List<Obligation> justFriends = new ArrayList<>();
        for (Obligation obl: list) {
            if(obl.getDebtor().isFriend(obligation.getCreditor())){
                justFriends.add(obl);
            }
        }
        if(obligation.getDebtor().getIsOwed() != null){
        return obligation.getDebtor().getIsOwed()
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID&&o.getStatus() != Obligation.Status.AUTOPAID)
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
    public boolean debtTransfer(Obligation obligation){
        if(transferLogicCreditor(obligation,getActiveCreditorOwes(obligation))==true) return true;
        if(transferLogicDebtor(obligation,getActiveDebtorisOwned(obligation))==true) return true;
    return false;
    }
    private boolean transferLogicCreditor(Obligation obligation,List<Obligation> ListToCheck){
        double restToPay = obligation.getAmount();
        int i =0;
        if(ListToCheck == null) return false;
        while (i<ListToCheck.size()&&!ListToCheck.get(i).equals(obligation)){//kurczaki trzeba dodac zeby sie zatrzymywalo jak dlug zejdzie caly
            double DebtToPay = ListToCheck.get(i).getAmount();
            double margin = DebtToPay - obligation.getAmount();
            if(margin>=0){ //wszystko splaci w jednym
                ListToCheck.get(i).setAmount(DebtToPay-obligation.getAmount());
                obligation.autopay();
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);

                if(!ListToCheck.get(i).getCreditor().equals(obligation.getDebtor())){
                    Obligation transferedDebt = new Obligation(ListToCheck.get(i).getCreditor(),obligation.getDebtor(),obligation.getAmount(),Obligation.Status.AUTOGEN);
                    dbc.addObligation(transferedDebt);
                }
                return true;
            }
            else{
                ListToCheck.get(i).autopay();
                obligation.setAmount(obligation.getAmount()-DebtToPay);
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);
                if(!ListToCheck.get(i).getCreditor().equals(obligation.getDebtor())) {
                    Obligation transferedDebt = new Obligation(ListToCheck.get(i).getCreditor(),obligation.getDebtor(),DebtToPay,Obligation.Status.AUTOGEN);
                    dbc.addObligation(transferedDebt);
                }
                restToPay = restToPay - DebtToPay;
            }
            i++;
        }
        return false;
    }
    private boolean transferLogicDebtor(Obligation obligation,List<Obligation> ListToCheck){
        int i =0;
        if(ListToCheck == null) return false;
        while (i<ListToCheck.size()&&!ListToCheck.get(i).equals(obligation)){//kurczaki trzeba dodac zeby sie zatrzymywalo jak dlug zejdzie caly
            double DebtToPay = ListToCheck.get(i).getAmount();
            double margin = DebtToPay - obligation.getAmount();
            if(margin>=0){ //wszystko splaci w jednym
                ListToCheck.get(i).setAmount(DebtToPay-obligation.getAmount());
                obligation.autopay();

                if(!ListToCheck.get(i).getDebtor().equals(obligation.getCreditor())){
                    Obligation transferedDebt = new Obligation(obligation.getCreditor(),ListToCheck.get(i).getDebtor(),obligation.getAmount(),Obligation.Status.AUTOGEN);
                    dbc.addObligation(transferedDebt);
                }
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);


                return true;
            }
            else{
                ListToCheck.get(i).autopay();
                obligation.setAmount(obligation.getAmount()-DebtToPay);
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);
                if(!ListToCheck.get(i).getDebtor().equals(obligation.getCreditor())) {
                    Obligation transferedDebt = new Obligation(obligation.getCreditor(),ListToCheck.get(i).getDebtor(),DebtToPay,Obligation.Status.AUTOGEN);
                    dbc.addObligation(transferedDebt);
                }
            }
            i++;
        }
        return false;
    }
    public static List<Obligation> getCleanObl(List<Obligation> obligationsList){
        List<Obligation> cleanList = new ArrayList<Obligation>();
        for (Obligation o: obligationsList) {
            if(o.getStatus()==null){
                System.out.println("Obligacja nr: "+o.getId() +" " +o.getCreditor().getName()+" <- "+ o.getDebtor().getName()+" value "+o.getAmount());
                cleanList.add(o);
            }
        }
        return cleanList;
    }
    public static void main(String[] args){
        DBConnector dbc = new DBConnector("test");
        GraphLogic logic = new GraphLogic(dbc);

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
        logic.debtTransfer(obligation2);


        dbc.getAllObligations();
        logic.getCleanObl(dbc.getAllObligations());

    }

}//TODO
/*
        DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic();

       // dbc.dropDatabase();
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
        List<Obligation> list = logic.getCleanObl(dbc.getAllObligations());
        User user4 = new User("d","daje");
        User user5 = new User("e","wisi/daje");
        User user6 = new User("f","f");
        dbc.addUser(user4);
        dbc.addUser(user5);
        dbc.addUser(user6);

        Obligation obligation3  = new Obligation(user4,user5,(double)30);
        dbc.addObligation(obligation3);
        Obligation obligation4 = new Obligation(user4,user,(double)25);
        dbc.addObligation(obligation4);
        Obligation obligation5 = new Obligation(user6,user4,(double)130);


        logic.debtTransfer(null,null,dbc.findObligationById(4L));
        list = logic.getCleanObl(dbc.getAllObligations());
        dbc.addObligation(obligation5);
        logic.debtTransfer(null,null,obligation5);
        logic.getCleanObl(dbc.getAllObligations());
 */

/*        dbc.deleteObligation(4L);
        dbc.deleteObligation(5L);

         Obligation obligation4 = new Obligation(dbc.findUserByName("a"),dbc.findUserByName("d"),(double)25);

        */