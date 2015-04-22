import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;

@GenerateEntity
public class AbstractTestEntity {
    @Key protected int id;
    protected String name;
}
