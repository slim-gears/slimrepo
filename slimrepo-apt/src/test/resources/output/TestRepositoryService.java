import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;

public interface TestRepositoryService extends RepositoryService<TestRepository> {
    EntitySet<RoleEntity> roles();
    EntitySet<UserEntity> users();
    EntitySet<ExistingEntity> existingEntities();
}
