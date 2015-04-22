import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import java.lang.Override;

public class GeneratedTestRepositoryService extends AbstractRepositoryService<TestRepository> implements TestRepositoryService {
    public GeneratedTestRepositoryService(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, GeneratedTestRepository.Model.Instance);
    }

    @Override
    protected TestRepository createRepository(SessionServiceProvider sessionServiceProvider) {
        return new GeneratedTestRepository(sessionServiceProvider);
    }
}
