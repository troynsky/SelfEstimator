package core;

public class Skill {

    public static final Skill NONE = new Skill(0);
    public static final Skill HEARDABOUT = new Skill(10);
    public static final Skill BEGINNER = new Skill(20);
    public static final Skill MIDDLE = new Skill(30);
    public static final Skill MASTER = new Skill(50);
    public static final Skill GURU = new Skill(90);
    public static final Skill GOD = new Skill(100);

    private int value = 0;
    private Long id;

    protected Skill() {
    }

    public Skill(int value) {
        this.value = value;
    }

    public Skill(String value) {
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
