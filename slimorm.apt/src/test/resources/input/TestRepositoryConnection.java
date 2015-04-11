import com.slimgears.slimorm.interfaces.EntitySet;
import com.slimgears.slimorm.annotations.Repository;

@Repository
public interface TestRepositoryConnection extends com.slimgears.slimorm.interfaces.Repository.Connection {
    EntitySet<AbstractTestEntity> tests();
    EntitySet<AbstractUserEntity> users();
}