package Logic.Entities.Transfer;

import Logic.Entities.ExpenseMember;

public class MemberTransfer extends Transfer {

    private ExpenseMember recipient;

    public MemberTransfer(ExpenseMember payer, double amount, ExpenseMember recipient) {
        super(payer, amount);
        this.recipient = recipient;
    }

    public ExpenseMember getRecipient() {
        return recipient;
    }

    public void setRecipient(ExpenseMember recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return String.format("%s payed %s an amount of %f", this.getMember().getName(), this.recipient.getName(), this.getAmount());
    }

}
