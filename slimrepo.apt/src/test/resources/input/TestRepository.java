import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import java.lang.Integer;

@GenerateRepository(version = 10, name = "TestRepository")
public interface TestRepository extends Repository {
    EntitySet<Integer, RoleEntity> roles();
    EntitySet<Integer, UserEntity> users();
}
