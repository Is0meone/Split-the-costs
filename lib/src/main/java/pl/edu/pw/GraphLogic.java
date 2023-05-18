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
            return justFriends
                .stream()
                .filter(o -> o.getStatus() != Obligation.Status.PAID&&o.getStatus() != Obligation.Status.AUTOPAID)
                .collect(Collectors.toList());
    }
    else return null;
    }
    public List<Obligation> getActiveDebtorisOwned(Obligation obligation){
        //TODO jeśli chcemy uwzględniać Friendship to odkomentować i zmienić liste podawaną do strumienia
      List<Obligation> list = obligation.getDebtor().getIsOwed();
      if(list== null){list= new ArrayList<>();}
        List<Obligation> justFriends = new ArrayList<>();
        for (Obligation obl: list) {
            if(obl.getDebtor().isFriend(obligation.getCreditor())){
                justFriends.add(obl);
            }
        }


        if(obligation.getDebtor().getIsOwed() != null){
        return justFriends
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
    public void debtTransfer(Obligation obligation){
        //+obsługa null
        List<Obligation> transfedDebt = new ArrayList<Obligation>();
        List listCred = new ArrayList<Obligation>();
        List listDebt = new ArrayList<Obligation>();
            listCred = getActiveCreditorOwes(obligation);
            if(listCred == null){listCred = new ArrayList<>();}
            listDebt = getActiveDebtorisOwned(obligation);
            if(listDebt == null){listDebt = new ArrayList<>();}
             if(listDebt.size() != 0) {
                transfedDebt = transferLogicDebtor(obligation, getActiveDebtorisOwned(obligation));
            } else if (listCred.size() != 0) {
                 transfedDebt = transferLogicCreditor(obligation, getActiveCreditorOwes(obligation));
             }

            for (Obligation o : transfedDebt) {
                if (!isStable(o)) {
                    boolean bol = debtTransferHelper(o);
                }
            }
    }
    private boolean debtTransferHelper(Obligation obligation){
        List<Obligation> transfedDebt = new ArrayList<Obligation>();

        List listCred = new ArrayList<Obligation>();
        List listDebt = new ArrayList<Obligation>();
        listCred = getActiveCreditorOwes(obligation);
        listDebt = getActiveDebtorisOwned(obligation);
        if (listCred.size() != 0) {
            transfedDebt = transferLogicCreditor(obligation, getActiveCreditorOwes(obligation));
        } else if (listDebt.size() != 0) {
            transfedDebt = transferLogicDebtor(obligation, getActiveDebtorisOwned(obligation));
        }

            for (Obligation o : transfedDebt) {
                if (!isStable(o)) return debtTransferHelper(o);
            }
            return true;
    }
    private List<Obligation> transferLogicCreditor(Obligation obligation,List<Obligation> ListToCheck){
        double restToPay = obligation.getAmount();
        int i =0;
        if(ListToCheck == null) return null;
        List<Obligation> transferedDebt = new ArrayList<>();

        while (i<ListToCheck.size()&&!ListToCheck.get(i).equals(obligation)){//kurczaki trzeba dodac zeby sie zatrzymywalo jak dlug zejdzie caly
            double DebtToPay = ListToCheck.get(i).getAmount();
            double margin = DebtToPay - obligation.getAmount();
            if(margin>=0){ //wszystko splaci w jednym
                ListToCheck.get(i).setAmount(DebtToPay-obligation.getAmount());
                obligation.autopay();
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);

                if(ListToCheck.get(i).getAmount()==0){
                    ListToCheck.get(i).autopay();
                }

                if(!ListToCheck.get(i).getCreditor().equals(obligation.getDebtor())){
                    Obligation newDebt = new Obligation(ListToCheck.get(i).getCreditor(),obligation.getDebtor(),obligation.getAmount(),Obligation.Status.AUTOGEN);
                    transferedDebt.add(newDebt);
                    dbc.addObligation(newDebt);
                }
                return transferedDebt;
            }
            else{
                ListToCheck.get(i).autopay();
                obligation.setAmount(obligation.getAmount()-DebtToPay);
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);
                if(!ListToCheck.get(i).getCreditor().equals(obligation.getDebtor())) {
                    Obligation newDebt = new Obligation(ListToCheck.get(i).getCreditor(),obligation.getDebtor(),DebtToPay,Obligation.Status.AUTOGEN);
                    transferedDebt.add(newDebt);
                    dbc.addObligation(newDebt);
                }
                restToPay = restToPay - DebtToPay;
            }
            i++;
        }
        return transferedDebt;
    }
    private List<Obligation> transferLogicDebtor(Obligation obligation,List<Obligation> ListToCheck){
        int i =0;
        if(ListToCheck == null) return null;
        List<Obligation> transferedDebt = new ArrayList<>();

        while (i<ListToCheck.size()&&!ListToCheck.get(i).equals(obligation)){//kurczaki trzeba dodac zeby sie zatrzymywalo jak dlug zejdzie caly
            double DebtToPay = ListToCheck.get(i).getAmount();
            double margin = DebtToPay - obligation.getAmount();
            if(margin>=0){ //wszystko splaci w jednym
                ListToCheck.get(i).setAmount(DebtToPay-obligation.getAmount());
                if(ListToCheck.get(i).getAmount()==0){
                    ListToCheck.get(i).autopay();
                }
                obligation.autopay();
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);

                if(!ListToCheck.get(i).getDebtor().equals(obligation.getCreditor())){
                   Obligation newDebt= new Obligation(obligation.getCreditor(),ListToCheck.get(i).getDebtor(),obligation.getAmount(),Obligation.Status.AUTOGEN);
                   transferedDebt.add(newDebt);
                   dbc.addObligation(newDebt);
                }
                return transferedDebt;
            }
            else{
                ListToCheck.get(i).autopay();
                obligation.setAmount(obligation.getAmount()-DebtToPay);
                dbc.addObligation(ListToCheck.get(i));
                dbc.addObligation(obligation);
                if(!ListToCheck.get(i).getDebtor().equals(obligation.getCreditor())) {
                    Obligation newDebt = new Obligation(obligation.getCreditor(),ListToCheck.get(i).getDebtor(),DebtToPay,Obligation.Status.AUTOGEN);
                    transferedDebt.add(newDebt);
                    dbc.addObligation(newDebt);
                }
            }
            i++;
        }
        return transferedDebt;
    }
    public static List<Obligation> getCleanObl(List<Obligation> obligationsList){
        List<Obligation> cleanList = new ArrayList<Obligation>();
        for (Obligation o: obligationsList) {
            if(o.getStatus()==null||o.getStatus()== Obligation.Status.AUTOGEN){
                System.out.println("Obligacja nr: "+o.getId() +" " +o.getCreditor().getName()+" <- "+ o.getDebtor().getName()+" value "+o.getAmount());
                cleanList.add(o);
            }
        }
        return cleanList;
    }
    public boolean isStable(Obligation obligation){
        List listCred;
        List listDebt;
        listCred = getActiveCreditorOwes(obligation);
        if(listCred==null){listCred = new ArrayList<>();}
        listDebt = getActiveDebtorisOwned(obligation);
        if(listDebt==null){listDebt = new ArrayList<>();}
        if (listCred.size() != 0) {
            return false;
        } else if (listDebt.size() != 0) {
            return false;
        }

        return true;
    }
    public void balanceGraph(){
        List<Obligation> list = dbc.getAllObligations();
        for (Obligation o : list) {
            if(!isStable(o)){
                debtTransfer(o);
            }
        }
    }
    public boolean findBestPath(Obligation obligation){
        return false;
    }
    public static void main(String[] args){
        //problem friendship ale raczej z baza
        DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic(dbc);

        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        Obligation obligation = new Obligation(user,user2,(double)100);
        Obligation obligation2 = new Obligation(user2,user3,(double)50);
        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addUser(user3);
        dbc.makeLove();
        dbc.addObligation(obligation);
        dbc.addObligation(obligation2);
        logic.debtTransfer(obligation2);
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
        /*
        logic.debtTransfer(dbc.findObligationById(4L));


        User user6 = new User("f","f");
        Obligation obligation5 = new Obligation(user6,dbc.findUserById(3L),(double)130);
        dbc.addObligation(obligation5);

        logic.debtTransfer(dbc.findObligationById(6L));
        logic.getCleanObl(dbc.getAllObligations());

*/
    }

}//TODO
/*

                DBConnector dbc = new DBConnector();
        GraphLogic logic = new GraphLogic(dbc);

        User user = new User("a","daje");
        User user2 = new User("b","wisi/daje");
        User user3 = new User("c","wisi");
        Obligation obligation = new Obligation(user,user2,(double)100);
        Obligation obligation2 = new Obligation(user2,user3,(double)50);
        Friendship friendship = new Friendship(user,user2, Friendship.Status.ACCEPTED);
        Friendship friendship1 = new Friendship(user2,user3, Friendship.Status.ACCEPTED);
        Friendship friendship2 = new Friendship(user,user3, Friendship.Status.ACCEPTED);

        dbc.addUser(user);
        dbc.addUser(user2);
        dbc.addUser(user3);
        dbc.addObligation(obligation);
        dbc.addObligation(obligation2);
        dbc.addFriendship(friendship);
        dbc.addFriendship(friendship1);
        dbc.addFriendship(friendship2);






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