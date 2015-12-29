import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.prototype.CustomOrmServiceProvider;
import com.slimgears.slimrepo.core.prototype.CustomTypeMappingInstaller;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@Generated(value = "CustomOrmRepository", comments = "Repository service implementation generated from CustomOrmRepository")
public class GeneratedCustomOrmRepositoryService extends AbstractRepositoryService<CustomOrmRepository> implements CustomOrmRepositoryService {
    public GeneratedCustomOrmRepositoryService(String customStringParameter, int customIntParameter) {
        this(new CustomOrmServiceProvider(customStringParameter, customIntParameter));
    }

    private GeneratedCustomOrmRepositoryService(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, GeneratedCustomOrmRepository.Model.Instance,
                new CustomTypeMappingInstaller());
    }

    @Override
    protected CustomOrmRepository createRepository(SessionServiceProvider sessionServiceProvider) {
        return new GeneratedCustomOrmRepository(sessionServiceProvider);
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
