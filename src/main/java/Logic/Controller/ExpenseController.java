package Logic.Controller;

import Logic.Entities.Transfer.ExpenseTransfer;
import Logic.Entities.ExpenseMember;
import Logic.Entities.Transfer.MemberTransfer;
import Logic.Entities.Transfer.Transfer;
import Logic.Entities.ExpenseLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Logic.Interfaces.IExpenseController;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.HashMap;

public class ExpenseController implements IExpenseController {

    private final ExpenseLog log;
    private final int id;
    private static int idCounter = 0;

    public ExpenseController(List<ExpenseMember> memberList) {
        this.id = ++idCounter;
        this.log = new ExpenseLog(memberList);
    }

    public ExpenseController(int id, ExpenseLog log) {
        this.id = id;
        this.log = log;
    }

    @Override
    public ExpenseTransfer addExpense(int id, double amount) throws IllegalArgumentException{
        return addExpense(findMemberOnID(id), amount);
    }

    @Override
    public ExpenseTransfer addExpense(ExpenseMember member, double amount) {
        ExpenseTransfer expense = new ExpenseTransfer(member, amount);
        processTransfer(expense);
        return expense;
    }

    @Override
    public List<ExpenseTransfer> getExpenses() {
        return log.getExpensesLogList();
    }

    @Override
    public String getExpensesAsString() {
        List<ExpenseTransfer> list = log.getExpensesLogList();
        StringBuilder sb = new StringBuilder();
        list.forEach((expense) -> {
            sb.append(expense.toString()).append("\n");
        });
        return sb.toString();
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return log.getTransferLogList();
    }
    
    @Override
    public String getAllTransfersAsString() {
        List<Transfer> list = log.getTransferLogList();
        StringBuilder sb = new StringBuilder();
        list.forEach((transfer) -> {
            sb.append(transfer.toString()).append("\n");
        });
        return sb.toString();
    }


    @Override
    public double getMemberBalance(int id) throws IllegalArgumentException {
        return this.getMemberBalance(findMemberOnID(id));
    }

    @Override
    public double getMemberBalance(ExpenseMember member) {
        return log.getMemberBalance(member);
    }

    @Override
    public String getAllMemberBalancesAsString() {
        StringBuilder sb = new StringBuilder();
        log.getMembers().forEach((ExpenseMember m) -> {
            String s = String.format("%s has spend %f", m.getName(), this.getMemberBalance(m));
            sb.append(s).append("\n");
        });
        return sb.toString();
    }

    @Override
    public MemberTransfer payMember(ExpenseMember payer, ExpenseMember recipient, double amount) {
        MemberTransfer t = new MemberTransfer(payer, amount, recipient);
        processTransfer(t);
        return t;
    }

    @Override
    public List<MemberTransfer> paymentsToSettleDebts() {
        List<MemberTransfer> result = new ArrayList();

        //Setting up variables to find ideal transfers from, without affecting data
        List<ExpenseMember> sortedList = log.sortMembersByExpenseBalance();
        Map<ExpenseMember, Double> newBalances = new HashMap();
        for (ExpenseMember m : sortedList) {
            newBalances.put(m, log.getMemberBalance(m));
        }
        int recipiantIdx = 0;
        int payerIdx = sortedList.size() - 1;

        double avarageExpense = Math.round(log.getAvarageExpenseBalance() * 100.0) / 100.0;

        while (recipiantIdx <= payerIdx) {
            ExpenseMember payer = sortedList.get(payerIdx);
            ExpenseMember recipiant = sortedList.get(recipiantIdx);

            double recipiantBalance = newBalances.get(recipiant);
            double payerBalance = newBalances.get(payer);

            // Checking if both members have a balance different from avarage, or moving to next member
            if (recipiantBalance <= avarageExpense) {
                recipiantIdx++;
            } else if (payerBalance >= avarageExpense) {
                payerIdx--;
            } else {

                // Finding the amount that will move one member to avarage balance
                double neededTransferAmount = Math.min(
                        (recipiantBalance - avarageExpense),
                        (avarageExpense - payerBalance)
                );

                //Creating the Transfer not, but not processing it
                result.add(new MemberTransfer(payer, neededTransferAmount, recipiant));

                //Adjusting the temp balances for the rest of the theoretical transfers
                double newPayerBalance = newBalances.get(payer) + neededTransferAmount;
                newBalances.put(payer, newPayerBalance);
                double newRecipientBalance = newBalances.get(recipiant) - neededTransferAmount;
                newBalances.put(recipiant, newRecipientBalance);

                // Go to next members
                if (neededTransferAmount == (recipiantBalance - avarageExpense)) {
                    recipiantIdx++;
                } else {
                    payerIdx--;
                }
            }
        }
        return result;
    }

    @Override
    public String printPaymentsToSettleDebts() {
        List<MemberTransfer> list = this.paymentsToSettleDebts();
        StringBuilder sb = new StringBuilder();
        for (MemberTransfer transfer : list) {
            sb.append(
                    String.format("%s should transfer %f to %s",
                            transfer.getMember().getName(),
                            transfer.getAmount(),
                            transfer.getRecipient().getName()
                    )
            ).append("\n");
        }
        return sb.toString();
    }

    private ExpenseMember findMemberOnID(int id) throws IllegalArgumentException {
        for (ExpenseMember m : log.getMembers()) {
            if (m.getId() == id) {
                return m;
            }
        }
        throw new IllegalArgumentException("No member in log with id:" + id);
    }

    @Override
    public boolean processTransfer(Transfer t) throws IllegalArgumentException {
        if (t.isProcessed()) {
            return false;
        }

        if (!log.getMembers().contains(t.getMember())) {
            throw new IllegalArgumentException("The members of the transfer is not part of the log");
        }

        log.payExpense(t.getMember(), t.getAmount());
        if (t instanceof MemberTransfer) {
            log.reimburseMember(((MemberTransfer) t).getRecipient(), t.getAmount());
        }

        log.addTransferToLog(t);
        t.setProcessed(true);
        return true;
    }

    public int getId() {
        return id;
    }
}
