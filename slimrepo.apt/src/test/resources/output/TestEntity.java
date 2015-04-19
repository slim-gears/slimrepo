import java.lang.String;

public class TestEntity extends AbstractTestEntity {
    private TestEntity() {
    }

    public TestEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Builder create() {
        return new Builder();
    }

    public TestEntity setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public TestEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public static class Builder {
        private TestEntity model = new TestEntity();

        public TestEntity build() {
            return model;
        }

        public Builder id(int id) {
            model.setId(id);
            return this;
        }

        public Builder name(String name) {
            model.setName(name);
            return this;
        }
    }
}
