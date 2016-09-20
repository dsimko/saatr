package org.jboss.qa.tool.saatr.web;

import org.jboss.qa.tool.saatr.SaatrApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
* This class is needed for deployment on an application server.
* It is the counterpart of the main method in WicketWebApplication.
*/
public class WarInitializer extends SpringBootServletInitializer {

   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
       return application.sources(SaatrApplication.class);
   }

}