package selfestimator.corelogic.logic;

public class Skill {

    public static Skill getSkillByNumber(String numberString) {
        int num = Integer.parseInt(numberString);
        return new Skill(num);
    }

    public static Skill getSkillByNumber(int skill) {
        return new Skill(skill);
    }
    private int value = 0;
    
    private Skill()
    {
    }
    
    private Skill(int value)
    {
        this.value = value;
    }
    
    public static final Skill NONE = new Skill(0);   
    public static final Skill HEARDABOUT = new Skill(10);   
    public static final Skill BEGINNER = new Skill(20);   
    public static final Skill MIDDLE = new Skill(30); 
    public static final Skill MASTER = new Skill(50); 
    public static final Skill GURU = new Skill(90); 
    public static final Skill GOD = new Skill(100); 

    public int getValue() {
        return value;
    }
  
}
