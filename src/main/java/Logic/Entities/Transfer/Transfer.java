package Logic.Entities.Transfer;

import Logic.Entities.ExpenseMember;

public abstract class Transfer {

    private static int idCounter = 0;

    private final int id;
    private ExpenseMember member;
    private double amount;
    private boolean processed;

    public Transfer(ExpenseMember member, double amount) {
        this.id = ++idCounter;
        this.member = member;
        this.amount = amount;
        this.processed = false;
    }

    public ExpenseMember getMember() {
        return member;
    }

    public void setMember(ExpenseMember member) {
        this.member = member;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

}
