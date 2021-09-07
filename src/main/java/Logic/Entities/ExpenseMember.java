package Logic.Entities;

public class ExpenseMember {

    private final int id;
    private String name;
    private static Integer idCounter = 0;

    public ExpenseMember(String name) {
        this.id = ++idCounter;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
