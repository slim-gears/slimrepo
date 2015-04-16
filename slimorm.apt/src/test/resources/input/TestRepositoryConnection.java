import com.slimgears.slimorm.interfaces.entities.EntitySet;
import com.slimgears.slimorm.annotations.RepositoryTemplate;

@RepositoryTemplate
public interface TestRepositoryConnection extends com.slimgears.slimorm.interfaces.Repository.Connection {
    EntitySet<AbstractTestEntity> tests();
    EntitySet<AbstractUserEntity> users();
}