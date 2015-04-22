package selfestimator.corelogic.logic;

import java.util.ArrayList;
import java.util.List;

public class Term {
    private String name;
    private List<Tag> tags;

    public Term(String name) {
        this.name = name;
        tags = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getAllTagNames() {
        List<String> list = new ArrayList<>();
        for (Tag tag : tags)
            list.add(tag.getName());
        return list;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void deleteTag(Tag tag) {
        tags.remove(tag);
    }

    public void deleteAllTags() {
        tags = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        if (name != null ? !name.equals(term.name) : term.name != null) return false;
        if (tags != null ? !tags.equals(term.tags) : term.tags != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }
}
