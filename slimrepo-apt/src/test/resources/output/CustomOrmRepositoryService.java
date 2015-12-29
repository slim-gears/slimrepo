import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import javax.annotation.Generated;

@Generated(value = "CustomOrmRepository", comments = "Repository service interface generated from CustomOrmRepository")
public interface CustomOrmRepositoryService extends RepositoryService<CustomOrmRepository> {
    EntitySet<RoleEntity> roles();
    EntitySet<UserEntity> users();
}
