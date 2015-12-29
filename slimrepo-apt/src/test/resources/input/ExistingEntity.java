import com.slimgears.slimrepo.core.annotations.Entity;

@Entity
class ExistingEntity {
    private int id;
    private String name;
    private RelatedEntity related;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RelatedEntity getRelated() {
        return related;
    }

    public void setRelated(RelatedEntity related) {
        this.related = related;
    }
}
