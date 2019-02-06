package tomer.mft.edit;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.informatica.mi.runtime.ProjectExecutionRequest;
import com.informatica.mi.runtime.ProjectRegistry;
import com.linoma.ga.projects.ProjectLocation;

class LocalProjectRegistry implements ProjectRegistry {

	static final Logger logger = LoggerFactory.getLogger(LocalProjectRegistry.class); 
	
    @Override
    public ProjectLocation getProjectLocation(ProjectExecutionRequest executionRequest) {
            String projectPath = new StringBuffer()
//            		.append(System.getenv("APP_DIR"))
//            		.append("/").append(System.getenv("APP_CURRENT_VER"))
//            		.append("/userdata/projects/")
            		.append("/").append(executionRequest.getProjectVO().getName())
//            		.append(".xml")
            		.toString();
            
            ProjectLocation projectLocation = new ProjectLocation(projectPath);
            logger.info("project location: " + projectLocation.getFile().getAbsolutePath());
            File projectFile = projectLocation.getFile();
            if(!projectFile.exists()) {
            	throw new RuntimeException("project file does not exists: " + projectFile.getAbsolutePath());
            }
            
            return projectLocation;
    }

}

