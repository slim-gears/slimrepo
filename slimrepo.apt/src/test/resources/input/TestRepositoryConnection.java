import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;

@GenerateRepository
public interface TestRepository extends Repository {
    EntitySet<AbstractTestEntity> tests();
    EntitySet<AbstractUserEntity> users();
}
