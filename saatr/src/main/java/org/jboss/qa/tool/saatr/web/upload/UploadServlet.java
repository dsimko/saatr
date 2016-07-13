package org.jboss.qa.tool.saatr.web.upload;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UploadServlet.class);
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(10_000_000);

        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem fileItem : items) {
                LOG.debug("Uploaded item name = " + fileItem.getFieldName());
                LOG.debug("Uploaded item value = " + fileItem.getString("UTF-8"));
            }
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
    }

}