package selfestimator.corelogic.logic;

import java.util.ArrayList;
import java.util.List;

public class Term {
    private String name;
    private List<Tag> tags;    

    public String getName()
    {
        return name;
    }
    
    public Term(String name) {
        this.name = name;
        tags = new ArrayList<>();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }
    
    public void deleteTag(Tag tag)
    {
        tags.remove(tag);
    }

    public List<String> getAllTagNames() {
        List<String> list = new ArrayList<>();
        for (Tag tag : tags)
            list.add(tag.getName());
        return list;
    }

    public void deleteAllTags() {
        tags = new ArrayList<>();
    }

}
