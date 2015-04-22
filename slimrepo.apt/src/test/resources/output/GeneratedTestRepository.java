import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.DefaultRepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;

public class GeneratedTestRepository extends AbstractRepository implements TestRepository {
    private final EntitySet.Provider<Integer, com.slimgears.slimrepo.apt.prototype.generated.RoleEntity> rolesEntitySet;

    private final EntitySet.Provider<Integer, com.slimgears.slimrepo.apt.prototype.generated.UserEntity> usersEntitySet;

    GeneratedTestRepository(SessionServiceProvider sessionServiceProvider) {
        super(sessionServiceProvider);
        this.rolesEntitySet = sessionServiceProvider.getEntitySetProvider(com.slimgears.slimrepo.apt.prototype.generated.RoleEntity.EntityMetaType);
        this.usersEntitySet = sessionServiceProvider.getEntitySetProvider(com.slimgears.slimrepo.apt.prototype.generated.UserEntity.EntityMetaType);
    }

    @Override
    public final EntitySet<Integer, com.slimgears.slimrepo.apt.prototype.generated.RoleEntity> roles() {
        return this.rolesEntitySet.get();
    }

    @Override
    public final EntitySet<Integer, com.slimgears.slimrepo.apt.prototype.generated.UserEntity> users() {
        return this.usersEntitySet.get();
    }

    static class Model extends DefaultRepositoryModel {
        public static final Model Instance = new Model();

        private static final int Version = 10;

        private static final String Name = "TestRepository";

        private Model() {
            super(Name, Version, com.slimgears.slimrepo.apt.prototype.generated.RoleEntity.EntityMetaType, com.slimgears.slimrepo.apt.prototype.generated.UserEntity.EntityMetaType);
        }
    }
}
