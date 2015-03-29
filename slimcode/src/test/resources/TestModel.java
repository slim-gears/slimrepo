import java.lang.String;

public class TestModel extends AbstractTestModel {
    private TestModel() {
    }

    public TestModel(String text, int number) {
        text = text;
        number = number;
    }

    public static Builder create() {
        return new Builder();
    }

    public TestModel setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public TestModel setNumber(int number) {
        this.number = number;
        return this;
    }

    public int getNumber() {
        return this.number;
    }

    public static class Builder {
        private TestModel model = new TestModel();

        public TestModel build() {
            return model;
        }

        public Builder text(String text) {
            model.setText(text);
            return this;
        }

        public Builder number(int number) {
            model.setNumber(number);
            return this;
        }
    }
}
