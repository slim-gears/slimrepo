import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import javax.annotation.Generated;

@Generated(value = "RepositoryContainer.InnerRepository", comments = "Repository service interface generated from RepositoryContainer.InnerRepository")
public interface RepositoryContainer_InnerRepositoryService extends RepositoryService<RepositoryContainer.InnerRepository> {
    EntitySet<RoleEntity> roles();
    EntitySet<UserEntity> users();
}
