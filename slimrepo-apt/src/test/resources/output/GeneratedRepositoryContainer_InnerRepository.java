import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.DefaultRepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@Generated(value = "RepositoryContainer.InnerRepository", comments = "Repository generated from RepositoryContainer.InnerRepository")
public class GeneratedRepositoryContainer_InnerRepository extends AbstractRepository implements RepositoryContainer.InnerRepository {
    private final EntitySet.Provider<RoleEntity> rolesEntitySet;

    private final EntitySet.Provider<UserEntity> usersEntitySet;

    GeneratedRepositoryContainer_InnerRepository(SessionServiceProvider sessionServiceProvider) {
        super(sessionServiceProvider);
        this.rolesEntitySet = sessionServiceProvider.getEntitySetProvider(RoleEntity.EntityMetaType);
        this.usersEntitySet = sessionServiceProvider.getEntitySetProvider(UserEntity.EntityMetaType);
    }

    @Override
    public final EntitySet<RoleEntity> roles() {
        return this.rolesEntitySet.get();
    }

    @Override
    public final EntitySet<UserEntity> users() {
        return this.usersEntitySet.get();
    }

    static class Model extends DefaultRepositoryModel {
        public static final Model Instance = new Model();

        private static final int Version = 10;

        private static final String Name = "InnerRepository";

        private Model() {
            super(Name, Version, RoleEntity.EntityMetaType, UserEntity.EntityMetaType);
        }
    }
}
