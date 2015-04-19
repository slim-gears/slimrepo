import com.slimgears.slimorm.core.interfaces.entities.EntitySet;
import com.slimgears.slimorm.core.annotations.RepositoryTemplate;

@RepositoryTemplate
public interface TestRepositoryConnection extends com.slimgears.slimorm.core.interfaces.Repository.Connection {
    EntitySet<AbstractTestEntity> tests();
    EntitySet<AbstractUserEntity> users();
}