package Logic.Entities;

import Logic.Entities.Transfer.ExpenseTransfer;
import Logic.Entities.Transfer.Transfer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseLog {

    private List<ExpenseMember> members;
    private List<Transfer> transferLogList;
    private Map<ExpenseMember, Double> expenseBalances;
    
    public ExpenseLog(List<ExpenseMember> members) {
        this.members = members;
        this.transferLogList = new ArrayList<>();
        this.expenseBalances = new HashMap();
        setAllMemberBalanaces(0.0);
    }

    public boolean addTransferToLog(Transfer t) {
        return transferLogList.add(t);
    }

    public void reimburseMember(ExpenseMember m, double amount) {
        double oldValue = this.expenseBalances.get(m);
        double newValue = oldValue - amount;
        this.expenseBalances.put(m, newValue);
    }

    public void payExpense(ExpenseMember m, double amount) {
        double oldValue = this.expenseBalances.get(m);
        double newValue = oldValue + amount;
        this.expenseBalances.put(m, newValue);
    }

    public List<ExpenseMember> sortMembersByExpenseBalance() {
        List<ExpenseMember> result = new ArrayList(this.members);
        ExpenseLog instance = this;
        Collections.sort(result, new Comparator<Object>() {
            @Override
            public int compare(Object a1, Object a2) {
                ExpenseMember b1 = (ExpenseMember) a1;
                ExpenseMember b2 = (ExpenseMember) a2;
                return compareMembersByBalance(instance, b1, b2);
            }
        });
        return result;
    }

    public double getAvarageExpenseBalance() {
        double total = 0;
        for (ExpenseMember m : this.members) {
            total += this.getMemberBalance(m);
        }
        return total / this.members.size();
    }

    public List<ExpenseMember> getMembers() {
        return members;
    }

    public void setMembers(List<ExpenseMember> members) {
        this.members = members;
    }

    public List<Transfer> getTransferLogList() {
        return transferLogList;
    }

    public void setTransferLogList(List<Transfer> transferLogList) {
        this.transferLogList = transferLogList;
    }

    public double getMemberBalance(ExpenseMember m) {
        return expenseBalances.get(m);
    }

    public void setMemberBalance(ExpenseMember m, double value) {
        expenseBalances.put(m, value);
    }
    
    public void setAllMemberBalanaces(double amount){
        for(ExpenseMember m : members){
            expenseBalances.put(m, amount);
        }
    }
    
    public List<ExpenseTransfer> getExpensesLogList(){
        List<ExpenseTransfer> expenses = new ArrayList();
        this.getTransferLogList().forEach((transfer) -> {
            if(transfer instanceof ExpenseTransfer){
                expenses.add((ExpenseTransfer) transfer);
            }
        });
        return expenses;
    }

    private static int compareMembersByBalance(ExpenseLog log, ExpenseMember m1, ExpenseMember m2) {
        double balance1 = log.getMemberBalance(m1);
        double balance2 = log.getMemberBalance(m2);
        if (balance1 < balance2) {
            return 1;
        } else if (balance1 > balance2) {
            return -1;
        } else {
            return 0;
        }
    }

}
