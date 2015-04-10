import com.slimgears.slimorm.interfaces.EntitySet;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.annotations.RepositoryConnection;

@RepositoryConnection
public interface TestRepositoryConnection extends Repository.Connection {
    EntitySet<AbstractTestEntity> tests();
    EntitySet<AbstractUserEntity> users();
}