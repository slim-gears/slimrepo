import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;

@GenerateRepository
public interface TestRepositoryService extends RepositoryService {
    EntitySet<AbstractTestEntity> tests();
    EntitySet<AbstractUserEntity> users();
}
