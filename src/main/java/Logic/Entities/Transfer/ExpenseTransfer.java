package Logic.Entities.Transfer;

import Logic.Entities.ExpenseMember;

public class ExpenseTransfer extends Transfer {
    
    private String description;
    
    public ExpenseTransfer(ExpenseMember member, double amount) {
        super(member, amount);
        this.description = "an expense";
    }
    
    public ExpenseTransfer(ExpenseMember member, double amount, String description) {
        super(member, amount);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s used %f,- on %s", this.getMember().getName(), this.getAmount(), this.getDescription());
    }
}
