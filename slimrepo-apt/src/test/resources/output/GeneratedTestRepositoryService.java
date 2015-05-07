import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;

import java.lang.Override;

public class GeneratedTestRepositoryService extends AbstractRepositoryService<TestRepository> implements TestRepositoryService {
    public GeneratedTestRepositoryService(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, GeneratedTestRepository.Model.Instance);
    }

    @Override
    protected TestRepository createRepository(SessionServiceProvider sessionServiceProvider) {
        return new GeneratedTestRepository(sessionServiceProvider);
    }

    @Override
    public final EntitySet<RoleEntity> roles() {
        return getEntitySet(RoleEntity.EntityMetaType);
    }

    @Override
    public final EntitySet<UserEntity> users() {
        return getEntitySet(UserEntity.EntityMetaType)
    }
}
