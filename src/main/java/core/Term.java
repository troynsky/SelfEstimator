package core;

import java.util.ArrayList;
import java.util.List;

public class Term implements Comparable<Term> {
    private Long id;
    private String name;
    private List<Tag> tags;

    protected Term() {
    }

    public Term(String name) {
        this.name = name;
        tags = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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

    @Override
    public int compareTo(Term o) {
        return this.getName().compareTo(o.getName());
    }
}
