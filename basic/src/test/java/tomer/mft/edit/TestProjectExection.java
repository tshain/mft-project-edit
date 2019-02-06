package tomer.mft.edit; /**
 * Created by jrajan on 5/23/2017.
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.informatica.mi.main.ICSEnv;
import com.informatica.mi.runtime.AgentRuntime;
import com.informatica.mi.runtime.ProjectExecutionRequest;
import com.informatica.mi.runtime.ProjectExecutionResponse;
import com.informatica.mi.runtime.ProjectRegistry;
import com.linoma.ga.data.db.project.ProjectVO;
import com.linoma.ga.projects.ProjectLocation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TestProjectExection {

    private static final String RAND_STR = "Some Random String";
    private AgentRuntime runtime;

    @Before
    public void setUp() throws IOException {
        //initialize agent project container
        //initialize agent runtime for project execution
        String logDir = Files.createTempDirectory("TestLogs").toString();
        System.setProperty(ICSEnv.APP_DIR, logDir);
    }

    @Test
    public void shouldNotFindJobInfo() throws IOException {
    	ProjectService srv = newProjectService();
    	ProjectExecutionRequest rq = newExecutionRequest();
    	Call<ResponseBody> call = srv.getJobInfo(1000L);
    	Response<ResponseBody> rsp = call.execute();
    	System.out.println(rsp.toString());
    }
    
    private ProjectExecutionRequest newExecutionRequest() throws IOException {
        final File from = File.createTempFile("from",".txt");
        final Path toDirectory = Files.createTempDirectory("copied");
        final File  to = new File(toDirectory.toFile(), from.getName());
        Files.write(from.toPath(), RAND_STR.getBytes());

        ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
        projectExecutionRequest.setProjectVO(newProjectVO());
        projectExecutionRequest.setJobNumber(1000L);
        projectExecutionRequest.setVariables(
                new HashMap(){{
                    put("SRC_FILE_PATH", from.getAbsolutePath());
                    put("TGT_FILE_PATH", to.getAbsolutePath());
                }});
        return projectExecutionRequest;
    }

    private ProjectVO newProjectVO() {
        ProjectVO projectVO = new ProjectVO();
        projectVO.setName("copy");
        projectVO.setId(100);
        projectVO.setCategoryId(0);
        projectVO.setName("TEST_RUN_PROJECT");
        return projectVO;
    }

    private ProjectService newProjectService() {

    	Retrofit retrofit = new Retrofit.Builder()
			.baseUrl("https://localhost:14288/api/agent/v1/")
			.client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
			.build();

    	ProjectService srv = retrofit.create(ProjectService.class);
    	return srv;
    }
    
    
    @Test
    public void testRunProject() throws IOException {
        final File from = File.createTempFile("from",".txt");
        final Path toDirectory = Files.createTempDirectory("copied");
        final File  to = new File(toDirectory.toFile(), from.getName());
        try {
            Files.write(from.toPath(), RAND_STR.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
            ProjectVO projectVO = new ProjectVO();
            projectVO.setName("copy");
            projectVO.setId(100);
            projectVO.setCategoryId(0);
            projectVO.setName("TEST_RUN_PROJECT");
            projectExecutionRequest.setProjectVO(projectVO);
            projectExecutionRequest.setJobNumber(1000L);
            projectExecutionRequest.setVariables(
                    new HashMap(){{
                        put("SRC_FILE_PATH", from.getAbsolutePath());
                        put("TGT_FILE_PATH", to.getAbsolutePath());
                    }});
            runtime.execute(projectExecutionRequest);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        String destFileContent = null;
        try {
            //make sure the file is copied.
            destFileContent = new String( Files.readAllBytes(to.toPath()), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("Unexpected", destFileContent , RAND_STR);
        Files.delete(from.toPath());
        Files.delete(to.toPath());

        //clean up.
        FileUtils.deleteDirectory(toDirectory.toFile());
        FileUtils.deleteDirectory(new File("userdata"));

    }


    private class TestProjectRegistry implements ProjectRegistry {

        @Override
        public ProjectLocation getProjectLocation(ProjectExecutionRequest executionRequest) {
            try {
                String projectPath = executionRequest.getProjectVO().getName();
                ProjectLocation projectLocation = new ProjectLocation("/" + projectPath);

                File projectFile = projectLocation.getFile();

                if(projectFile.exists()) {
                    return projectLocation;
                }

                String projectXML = IOUtils.toString(
                        getClass().getResourceAsStream("/" + projectPath + ".xml"));

                projectFile.getParentFile().mkdirs();
                Files.write(projectFile.toPath(), projectXML.getBytes("UTF-8"));

                return projectLocation;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }

}