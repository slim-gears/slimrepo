import com.slimgears.slimrepo.core.annotations.ComparableSemantics;
import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.ValueSemantics;
import com.slimgears.slimrepo.core.interfaces.conditions.PredicateType;

import java.util.Calendar;

enum TestEnum {
    VALUE_1,
    VALUE_2
}

class CustomType {
}

@GenerateEntity
@ValueSemantics({CustomType.class})
class AbstractTestEntity {
    protected int id;
    protected String name;
    protected AbstractRelatedEntity related;
    protected TestEnum enumValue;
    protected CustomType customTypeValue;
}
