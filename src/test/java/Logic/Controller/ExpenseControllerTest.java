package Logic.Controller;

import Logic.Entities.Transfer.ExpenseTransfer;
import Logic.Entities.Transfer.MemberTransfer;
import Logic.Entities.Transfer.Transfer;
import Logic.Entities.ExpenseLog;
import Logic.Entities.ExpenseMember;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ExpenseControllerTest {

    private static List<ExpenseMember> memberList;
    private static ExpenseMember m1, m2, m3;
    private static ExpenseController instance;
    private final int memberListSize = 3;

    public ExpenseControllerTest() {
    }

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Starting up test");
        memberList = new ArrayList();
        m1 = new ExpenseMember("John");
        memberList.add(m1);
        m2 = new ExpenseMember("Peter");
        memberList.add(m2);
        m3 = new ExpenseMember("Mary");
        memberList.add(m3);
    }

    @BeforeEach
    public void beforeEach() {
        instance = new ExpenseController(memberList);
    }

    /*
      Testing for primary functionalities
      -  an expense for the group can be added.
      -  a list of expenses can be obtained, so that everyone can know what has been spent and who put up money for it.
      -  the balance for each group member can be obtained.
      -  a payment from one member to another member of the group can be added.
      -  a list of payments that would settle all debts should be obtainable.
     */
    @Test
    public void testConstructor() {
        ExpenseController instance = new ExpenseController(memberList);

        double expectedStartingBalance = 0;
        assertEquals(memberListSize, memberList.size());
        for (ExpenseMember m : memberList) {
            double memberBalance = instance.getMemberBalance(m.getId());
            assertEquals(expectedStartingBalance, memberBalance);
        }
    }

    @Test
    public void testConstructor_WithVariables() {
        int id = Integer.MIN_VALUE;
        ExpenseLog log = new ExpenseLog(memberList);

        ExpenseController instance = new ExpenseController(id, log);

        double expectedStartingBalance = 0;
        assertEquals(memberListSize, memberList.size());
        for (ExpenseMember m : memberList) {
            double memberBalance = instance.getMemberBalance(m.getId());
            assertEquals(expectedStartingBalance, memberBalance);
        }
        assertEquals(id, instance.getId());
    }

    @Test
    public void testProcessTransfer_ExpenseTransfer() {
        double expectedBalance = 200.0;

        Transfer transfer = new ExpenseTransfer(m1, expectedBalance);

        //Action
        boolean result = instance.processTransfer(transfer);

        assertTrue(result);
        int expectedNumberOfExpenses = 1;
        int expectedNumberOfTransfers = 1;
        assertEquals(expectedNumberOfExpenses, instance.getExpenses().size());
        assertEquals(expectedNumberOfTransfers, instance.getAllTransfers().size());
        assertEquals(expectedBalance, instance.getMemberBalance(m1));
    }

    @Test
    public void testProcessTransfer_MemberTransfer() {
        double expectedBalance = 200.0;

        Transfer transfer = new MemberTransfer(m1, expectedBalance, m2);

        //Action
        boolean result = instance.processTransfer(transfer);

        assertTrue(result);
        int expectedNumberOfExpenses = 0;
        int expectedNumberOfTransfers = 1;
        assertEquals(expectedNumberOfExpenses, instance.getExpenses().size());
        assertEquals(expectedNumberOfTransfers, instance.getAllTransfers().size());
        assertEquals(expectedBalance, instance.getMemberBalance(m1));
        assertEquals(-expectedBalance, instance.getMemberBalance(m2));
    }

    @Test
    public void testProcessTransfer_Negative_AlreadyProcessedTransfer() {
        double expectedBalance = 200.0;

        Transfer transfer = new ExpenseTransfer(m1, expectedBalance);

        //Action
        boolean firstResult = instance.processTransfer(transfer);
        boolean result = instance.processTransfer(transfer);

        assertTrue(firstResult);
        assertFalse(result);
        int expectedNumberOfExpenses = 1;
        int expectedNumberOfTransfers = 1;
        assertEquals(expectedNumberOfExpenses, instance.getExpenses().size());
        assertEquals(expectedNumberOfTransfers, instance.getAllTransfers().size());
        assertEquals(expectedBalance, instance.getMemberBalance(m1));
    }

    @Test
    public void testProcessTransfer_Negative_MemberNotInLog() {
        ExpenseMember newMember = new ExpenseMember("Tim");
        double expectedBalance = 200.0;
        Transfer transfer = new ExpenseTransfer(newMember, expectedBalance);

        assertThrows(IllegalArgumentException.class, () -> {
            boolean result = instance.processTransfer(transfer);
        });
    }

    /*
    Test for functionality:
    -  an expense for the group can be added.
     */
    @Test
    public void testAddExpense() {
        ExpenseMember m = m1;
        double expenseValue = 95.50;

        //Action
        Transfer result = instance.addExpense(m, expenseValue);

        //Check Correct Type
        assertTrue(result instanceof ExpenseTransfer);

        //Check expense is correctly logged
        int expectedAmountOfLoggedTransfers = 1;
        assertTrue(result.isProcessed());
        assertEquals(expectedAmountOfLoggedTransfers, instance.getExpenses().size());

        //Check value is correct in different entities
        assertEquals(expenseValue, result.getAmount());
        assertEquals(expenseValue, instance.getMemberBalance(m));
    }

    @Test
    public void testAddExpense_Multiple() {
        ExpenseMember m = memberList.get(0);
        double amount1 = 10.25;
        double amount2 = 30.50;
        double expectedTotalAmount = amount1 + amount2;

        //Action
        Transfer result1 = instance.addExpense(m, amount1);
        Transfer result2 = instance.addExpense(m.getId(), amount2);

        //Check Correct Type
        assertTrue(result1 instanceof ExpenseTransfer);
        assertTrue(result2 instanceof ExpenseTransfer);

        //Check expense is correctly logged
        int expectedAmountOfLoggedTransfers = 2;
        assertTrue(result1.isProcessed());
        assertTrue(result2.isProcessed());
        assertEquals(expectedAmountOfLoggedTransfers, instance.getExpenses().size());

        //Check value is correct in different entities
        assertEquals(amount1, result1.getAmount());
        assertEquals(amount2, result2.getAmount());
        assertEquals(expectedTotalAmount, instance.getMemberBalance(m));
    }

    @Test
    public void testAddExpense_Negative_NoMemberWithID() {
        int id = Integer.MIN_VALUE;
        double amount = 99.99;

        for (ExpenseMember m : memberList) {
            assertNotEquals(id, m.getId());
        }

        //Action
        assertThrows(IllegalArgumentException.class, () -> {
            Transfer result = instance.addExpense(id, amount);
        });
    }

    /*
    Test for functionality:
    -  a list of expenses can be obtained, so that everyone can know what has been spent and who put up money for it.
     */
    @Test
    public void testGetAllTransfersAsString() {
        List<Transfer> transfers = new ArrayList();
        transfers.add(new ExpenseTransfer(m1, 45.5));
        transfers.add(new ExpenseTransfer(m1, 120.0, "movie tickets"));
        transfers.add(new MemberTransfer(m2, 60.0, m1));
        for (Transfer t : transfers) {
            instance.processTransfer(t);
        }

        //Action
        String result = instance.getAllTransfersAsString();
        List<String> resultList = new ArrayList(Arrays.asList(result.split("\n")));

        //Assert amount of transfer lines
        assertEquals(transfers.size(), resultList.size());

        //Assert lines in returned string
        for (Transfer transfer : transfers) {
            assertTrue(resultList.contains(transfer.toString()));
        }
    }

    @Test
    public void testGetAllTransfers() {
        List<Transfer> transfers = new ArrayList();
        transfers.add(new ExpenseTransfer(m1, 45.5));
        transfers.add(new ExpenseTransfer(m1, 120.0, "movie tickets"));
        transfers.add(new MemberTransfer(m2, 60.0, m1));
        for (Transfer t : transfers) {
            instance.processTransfer(t);
        }

        //Action
        List<Transfer> result = instance.getAllTransfers();

        //Assert amount of transfer lines
        int expectedAmountOfTransferLines = 3;
        assertEquals(expectedAmountOfTransferLines, result.size());

        //Assert expenses are the same
        for (Transfer transfer : transfers) {
            assertTrue(result.contains(transfer));
        }
    }

    @Test
    public void testGetExpensesAsString() {
        List<Transfer> transfers = new ArrayList();
        transfers.add(new ExpenseTransfer(m1, 45.5));
        transfers.add(new ExpenseTransfer(m1, 120.0, "movie tickets"));
        transfers.add(new MemberTransfer(m2, 60.0, m1));
        for (Transfer t : transfers) {
            instance.processTransfer(t);
        }

        //Action
        String result = instance.getExpensesAsString();
        List<String> resultList = new ArrayList(Arrays.asList(result.split("\n")));

        //Assert amount of transfer lines
        int expectedAmountOfTransferLines = 2;
        assertEquals(expectedAmountOfTransferLines, resultList.size());

        //Assert lines in returned string
        for (Transfer transfer : transfers) {
            if (transfer instanceof ExpenseTransfer) {
                assertTrue(resultList.contains(transfer.toString()));
            } else {
                assertFalse(resultList.contains(transfer.toString()));
            }
        }
    }

    @Test
    public void testGetExpenses() {
        List<Transfer> transfers = new ArrayList();
        transfers.add(new ExpenseTransfer(m1, 45.5));
        transfers.add(new ExpenseTransfer(m1, 120.0, "movie tickets"));
        transfers.add(new MemberTransfer(m2, 60.0, m1));
        for (Transfer t : transfers) {
            instance.processTransfer(t);
        }

        //Action
        List<ExpenseTransfer> result = instance.getExpenses();

        //Assert amount of transfer lines
        int expectedAmountOfTransferLines = 2;
        assertEquals(expectedAmountOfTransferLines, result.size());

        //Assert expenses are the same
        for (Transfer transfer : transfers) {
            if (transfer instanceof ExpenseTransfer) {
                assertTrue(result.contains(transfer));
            } else {
                assertFalse(result.contains(transfer));
            }
        }
    }

    /*
    Test for functionality:
    -  the balance for each group member can be obtained.
     */
    @Test
    public void testGetMemberBalance() {
        instance.processTransfer(new ExpenseTransfer(m1, 30));
        instance.processTransfer(new ExpenseTransfer(m1, 80));

        //Action
        double result = instance.getMemberBalance(m1.getId());
        double expectedResult = 110;

        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetAllMemberBalancesAsString() {
        instance.processTransfer(new ExpenseTransfer(m1, 30));
        instance.processTransfer(new ExpenseTransfer(m2, 80));

        //Action
        String result = instance.getAllMemberBalancesAsString();
        List<String> resultList = new ArrayList(Arrays.asList(result.split("\n")));

        String expectedStringForMember1 = String.format("%s has spend %f", m1.getName(), instance.getMemberBalance(m1));
        assertTrue(resultList.contains(expectedStringForMember1));
        String expectedStringForMember2 = String.format("%s has spend %f", m2.getName(), instance.getMemberBalance(m2));
        assertTrue(resultList.contains(expectedStringForMember2));
        String expectedStringForMember3 = String.format("%s has spend %f", m3.getName(), instance.getMemberBalance(m3));
        assertTrue(resultList.contains(expectedStringForMember3));

    }

    /*
    Test for functionality:
    -  a payment from one member to another member of the group can be added.
     */
    @Test
    public void testPayMember() {
        double startingValueForMember1 = 80;
        double expectedBalanceForMember2 = 50;

        instance.processTransfer(new ExpenseTransfer(m1, startingValueForMember1));

        //Action
        MemberTransfer result = instance.payMember(m2, m1, expectedBalanceForMember2);

        //Assert balance to have changed for both members
        double expectedBalanceForMember1 = startingValueForMember1 - expectedBalanceForMember2;
        assertEquals(expectedBalanceForMember1, instance.getMemberBalance(m1));
        assertEquals(expectedBalanceForMember2, instance.getMemberBalance(m2));

        //Assert Transfer to be looged correctly
        assertTrue(instance.getAllTransfers().contains(result));
        assertFalse(instance.getExpenses().contains(result));
    }

    /*
    Test for functionality:
    -  a list of payments that would settle all debts should be obtainable.
     */
    @Test
    public void testPaymentsToSettleDebts_TwoPayers() {
        instance.processTransfer(new ExpenseTransfer(m1, 90));

        //Action
        List<MemberTransfer> results = instance.paymentsToSettleDebts();

        //Asserting amount of transfers
        int expectTransferAmounts = 2;
        assertEquals(expectTransferAmounts, results.size());
        int expectedProcessedTransfersTotal = 1;
        assertEquals(expectedProcessedTransfersTotal, instance.getAllTransfers().size());

        //Asserts size and recipiant for each Transfer
        ExpenseMember expectedRecipiant = m1;
        double expectedAmount = 30;
        for (MemberTransfer transfer : results) {
            assertEquals(expectedRecipiant, transfer.getRecipient());
            assertEquals(expectedAmount, transfer.getAmount());

            //tranfers have NOT been processed
            assertFalse(transfer.isProcessed());
        };
    }

    @Test
    public void testPaymentsToSettleDebts_OnePayer() {
        instance.processTransfer(new ExpenseTransfer(m1, 30));
        instance.processTransfer(new ExpenseTransfer(m2, 60));

        //Action
        List<MemberTransfer> results = instance.paymentsToSettleDebts();
        MemberTransfer result = results.get(0);

        //Asserting amount of transfers
        int expectTransferAmounts = 1;
        assertEquals(expectTransferAmounts, results.size());
        int expectedProcessedTransfersTotal = 2;
        assertEquals(expectedProcessedTransfersTotal, instance.getAllTransfers().size());

        //Asserts size and recipiant for each Transfer
        ExpenseMember expectedRecipiant = m2;
        ExpenseMember expectedPayer = m3;
        double expectedAmount = 30;
        assertEquals(expectedRecipiant, result.getRecipient());
        assertEquals(expectedPayer, result.getMember());
        assertEquals(expectedAmount, result.getAmount());

        //tranfers have NOT been processed
        assertFalse(result.isProcessed());
    }

    @Test
    public void testPaymentsToSettleDebts_UnendingDecimalDivision() {
        instance.processTransfer(new ExpenseTransfer(m1, 100));

        //Action
        List<MemberTransfer> results = instance.paymentsToSettleDebts();

        //Asserting amount of transfers
        int expectTransferAmounts = 2;
        assertEquals(expectTransferAmounts, results.size());
        int expectedProcessedTransfersTotal = 1;
        assertEquals(expectedProcessedTransfersTotal, instance.getAllTransfers().size());

        //Asserts size and recipiant for each Transfer
        ExpenseMember expectedRecipiant = m1;
        double expectedAmount = 33.33;
        for (MemberTransfer transfer : results) {
            assertEquals(expectedRecipiant, transfer.getRecipient());
            assertEquals(expectedAmount, transfer.getAmount());

            //tranfers have NOT been processed
            assertFalse(transfer.isProcessed());
        };
    }

    @Test
    public void testPrintPaymentsToSettleDebts() {
        instance.processTransfer(new ExpenseTransfer(m1, 100));
        instance.processTransfer(new ExpenseTransfer(m2, 20));

        //Action
        String result = instance.printPaymentsToSettleDebts();
        List<String> results = new ArrayList(Arrays.asList(result.split("\n")));

        //Asserting amount of transfers
        int expectTransferAmounts = 2;
        assertEquals(expectTransferAmounts, results.size());
        int expectedProcessedTransfersTotal = 2;
        assertEquals(expectedProcessedTransfersTotal, instance.getAllTransfers().size());

        //Asserts each Transfer result should include
        double expectedTransferAmountFromMember2 = 20;
        double expectedTransferAmountFromMember3 = 40;
        String expectTransferStringFromMember2 = String.format("%s should transfer %f to %s",
                m2.getName(),
                expectedTransferAmountFromMember2,
                m1.getName()
        );
        String expectTransferStringFromMember3 = String.format("%s should transfer %f to %s",
                m3.getName(),
                expectedTransferAmountFromMember3,
                m1.getName()
        );

        assertTrue(result.contains(expectTransferStringFromMember2));
        assertTrue(result.contains(expectTransferStringFromMember3));
    }
}
