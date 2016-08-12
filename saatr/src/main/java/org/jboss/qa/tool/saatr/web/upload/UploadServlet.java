package org.jboss.qa.tool.saatr.web.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.util.DocumentUtils;
import org.jboss.qa.tool.saatr.util.MongoDBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

    private static final String JOB_NAME_PARAM_NAME = "jobName";
    private static final String BUILD_NUMBER_NAME_PARAM_NAME = "buildNumber";
    private static final String TIMESTAMP_NAME_PARAM_NAME = "timestamp";
    private static final String DURATION_NAME_PARAM_NAME = "duration";
    private static final String TESTSUITE_NAME_PARAM_NAME = "testsuite";

    private static final Logger LOG = LoggerFactory.getLogger(UploadServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(10_000_000);

        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            MongoDBUtils.save(createBuild(upload.parseRequest(request)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Build createBuild(List<FileItem> items) throws Exception {
        Build build = new Build();
        for (FileItem fileItem : items) {
            final String name = fileItem.getFieldName();
            if (TESTSUITE_NAME_PARAM_NAME.equals(name)) {
                try (InputStream zipStream = fileItem.getInputStream()) {
                    DocumentUtils.fillBuildByTestsuites(DocumentUtils.unzipAndUnmarshalTestsuite(zipStream), build);
                }
            } else {
                final String value = fileItem.getString(StandardCharsets.UTF_8.name());
                LOG.debug("Uploaded fileItem with name = {} and value = {}", name, value);
                switch (name) {
                case JOB_NAME_PARAM_NAME: {
                    build.setJobName(value);
                    break;
                }
                case BUILD_NUMBER_NAME_PARAM_NAME: {
                    build.setBuildNumber(Long.valueOf(value));
                    break;
                }
                case TIMESTAMP_NAME_PARAM_NAME: {
                    build.setTimestamp(Long.valueOf(value));
                    break;
                }
                case DURATION_NAME_PARAM_NAME: {
                    build.setDuration(Long.valueOf(value));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown param " + name);
                }
            }
        }
        return build;
    }

}