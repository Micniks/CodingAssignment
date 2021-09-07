package Logic.Interfaces;

import Logic.Entities.Transfer.ExpenseTransfer;
import Logic.Entities.ExpenseMember;
import Logic.Entities.Transfer.MemberTransfer;
import Logic.Entities.Transfer.Transfer;
import java.util.List;

public interface IExpenseController {

    /**
     * Add an Expense to the log for Member with given id, and process the
     * ExpenseTransfer in the log before returning it.
     *
     *
     * @param id (int) value of a ExpenseMember ID in the ExpenseLog of the
     * ExpenseController, for the Member that the expense is tied to.
     * @param amount (double) value of the expense.
     * @return (ExpenseTransfer) object that is added to the Log.
     * @throws IllegalArgumentException if ID does not match id of any
     * ExpenseMember in the ExpenseLog of the ExpenseController.
     */
    public ExpenseTransfer addExpense(int id, double amount) throws IllegalArgumentException;

    /**
     * Add an Expense to the log for the given Member, and process the
     * ExpenseTransfer in the log before returning it.
     *
     * @param member (ExpenseMember) Member that the expense is tied to.
     * @param amount (double) value of the expense.
     * @return (ExpenseTransfer) transfer that has been added to the Log.
     */
    public ExpenseTransfer addExpense(ExpenseMember member, double amount);

    /**
     * Get a List of all ExpenseTransfers in the ExpenseLog.
     *
     * @return (List<ExpenseTransfer>) list of ExpenseTransfers in the
     * ExpenseLog.
     */
    public List<ExpenseTransfer> getExpenses();

    /**
     * Get a String with each ExpenseTranfer, build together from .toString() of
     * the expenses, seperated with newline (\n).
     *
     * @return (String) list of expenses as text.
     */
    public String getExpensesAsString();

    /**
     * Get a List of all Transfers in the ExpenseLog, including all
     * ExpenseTransfer and MemberTransfer objects.
     *
     * @return (List<Transfer>) list of all transfer objects in ExpenseLog
     */
    public List<Transfer> getAllTransfers();

    /**
     * Get a List of all Transfers in the ExpenseLog, including all
     * ExpenseTransfer and MemberTransfer objects.
     *
     * @return (String) list of all transfers as text.
     */
    public String getAllTransfersAsString();

    /**
     * Take a transfer and process it thought the ExpenseLog, changing the
     * balance of the relevent ExpenseMember(s) as needed in the
     * ExpenseController.
     *
     * @param t (Transfer) transfer object to the added and processed throght
     * the ExepnseLog.
     * @return (boolean) True if transfer was processed, otherwise returns
     * false.
     * @throws IllegalArgumentException if the transfer includes ExpenseMembers
     * not part of the ExpenseLog in the ExpenseController.
     */
    public boolean processTransfer(Transfer t) throws IllegalArgumentException;

    /**
     * Get the amount a member with ID has spend on expenses, after all
     * processed Transfers, including transfers between members.
     *
     * @param id (int) value of a ExpenseMember ID in the ExpenseLog of the
     * ExpenseController.
     * @return (double) the balance of the ExpenseMember
     * @throws IllegalArgumentException if ID does not match id of any
     * ExpenseMember in the ExpenseLog of the ExpenseController.
     */
    public double getMemberBalance(int id) throws IllegalArgumentException;

    /**
     * Get the amount a member has spend on expenses, after all processed
     * Transfers, including transfers between members.
     *
     * @param member (ExpenseMember) The member the balance is tied to in the
     * ExpenseLog.
     * @return (double) the balance of the ExpenseMember
     */
    public double getMemberBalance(ExpenseMember member);

    /**
     * Getting the balance of all members in the ExpenseLog, build together from
     * in the form "MemberName has spend Amount", seperated with newline (\n).
     *
     * @return (String) Text list of balances of all members.
     */
    public String getAllMemberBalancesAsString();

    /**
     * Create and Process a MemberTransfer between ExpenseMembers of a given
     * amount, and add it to the ExpenseLog.
     *
     * @param payer (ExpenseMember) The member to pay, who will have their
     * ExpenseBalance increased.
     * @param recipient (ExpenseMember) The member to be reimbursed, who will
     * have their ExpenseBalance decreased.
     * @param amount (double) the amount of the transfer
     * @return (MemberTransfer) the new and processed tranfer object.
     */
    public MemberTransfer payMember(ExpenseMember payer, ExpenseMember recipient, double amount);

    /**
     * Make a list of transfers that would make that, if processed, would make
     * the ExpenseBalance of every Member in the ExpenseLog become an equal
     * amount, with up to a 0.01 difference at the most at roundings.
     *
     * The transfers are NOT added to ExpenseLog, and are NOT processed.
     *
     * The amount of transfers should be between 0 and a maximum amount of n-1,
     * where n is the amount of Members in the ExpenseLog.
     *
     * @return (List<MemberTransfer>) unprocessed transfers that are NOT added
     * to the ExpenseLog, that would balance out each members ExpenseBalance if
     * processed.
     */
    public List<MemberTransfer> paymentsToSettleDebts();

    /**
     * Get a String that describe how to balance out each ExpenseMembers
     * ExpenseBalance within n-1 transfers, where n is the amount of members in
     * the ExpenseLog. All transfers in the String are seperated by a newline
     * (\n).
     *
     * @return (String) a text showing how the balance of all members and be
     * equalised.
     */
    public String printPaymentsToSettleDebts();

}
