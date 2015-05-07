import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.DefaultRepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import java.lang.Override;
import java.lang.String;

public class GeneratedTestRepository extends AbstractRepository implements TestRepository {
    private final EntitySet.Provider<RoleEntity> rolesEntitySet;

    private final EntitySet.Provider<UserEntity> usersEntitySet;

    GeneratedTestRepository(SessionServiceProvider sessionServiceProvider) {
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

        private static final String Name = "TestRepository";

        private Model() {
            super(Name, Version, RoleEntity.EntityMetaType, UserEntity.EntityMetaType);
        }
    }
}
