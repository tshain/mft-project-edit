package tomer.mft.edit;

import com.linoma.dpa.DPAConstants;
import com.linoma.dpa.DPASettings;
import com.linoma.dpa.services.UserSession;
import com.linoma.ga.core.event.JobEventLogger;
import com.linoma.ga.data.db.auditlog.JobFileEventVO;
import com.linoma.ga.data.db.project.ProjectVO;
import com.linoma.ga.data.db.user.UserVO;
import com.linoma.ga.projects.DedupRepository;
import com.linoma.ga.projects.ProjectContainer;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by kveerapa on 10/11/2017.
 */
public class LocalAgentContainer extends ProjectContainer {

    private static Logger LOG = Logger.getLogger(ProjectContainer.class);

    public LocalAgentContainer(){

    }

    public static void init(){
        if(INSTANCE == null){
            INSTANCE = new LocalAgentContainer();
        }
        File homeDir = new File(System.getProperty("java.io.tmpdir"));
        System.setProperty(DPAConstants.SYS_PROP_GOANYWHERE_HOME, homeDir.getAbsolutePath());
    }

    @Override
    public JobEventLogger getJobLogger() {
        return null;
    }

    @Override
    public DPASettings getSettings() {
        return DPASettings.getCurrentSettings();
    }

    public void logEvent(UserSession userSession, JobFileEventVO event) {
        LOG.info("File Event: " + event.toString());
    }

    @Override
    public String getHomeDirectory(UserVO userVO) {
        return getSettings().getProjectsDirectory().getAbsolutePath();
    }

    @Override
    public ProjectVO resolveProject(String projectLocation) {
        return null;
    }

    @Override
    public File getUserDataHome() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    public DedupRepository getDedupRepository() {
        return null;
    }
}
