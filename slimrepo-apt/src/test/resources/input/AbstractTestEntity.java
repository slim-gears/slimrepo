import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.interfaces.conditions.PredicateType;

import java.util.Calendar;

enum TestEnum {
    VALUE_1,
    VALUE_2
}

@GenerateEntity
class AbstractTestEntity {
    protected int id;
    protected String name;
    protected AbstractRelatedEntity related;
    protected TestEnum enumValue;
}
