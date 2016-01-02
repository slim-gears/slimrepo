import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import java.lang.Override;
import javax.annotation.Generated;

@Generated(value = "RepositoryContainer.InnerRepository", comments = "Repository service implementation generated from RepositoryContainer.InnerRepository")
public class GeneratedRepositoryContainer_InnerRepositoryService extends AbstractRepositoryService<RepositoryContainer.InnerRepository> implements RepositoryContainer_InnerRepositoryService {
    public GeneratedRepositoryContainer_InnerRepositoryService(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, GeneratedRepositoryContainer_InnerRepository.Model.Instance);
    }

    @Override
    protected RepositoryContainer.InnerRepository createRepository(SessionServiceProvider sessionServiceProvider) {
        return new GeneratedRepositoryContainer_InnerRepository(sessionServiceProvider);
    }

    @Override
    public final EntitySet<RoleEntity> roles() {
        return getEntitySet(RoleEntity.EntityMetaType);
    }

    @Override
    public final EntitySet<UserEntity> users() {
        return getEntitySet(UserEntity.EntityMetaType);
    }
}
