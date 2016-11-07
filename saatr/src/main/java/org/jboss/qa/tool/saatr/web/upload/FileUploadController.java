
package org.jboss.qa.tool.saatr.web.upload;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.domain.build.Group;
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FileUploadController {

    private static final String JOB_NAME_PARAM_NAME = "jobName";

    private static final String BUILD_NUMBER_NAME_PARAM_NAME = "buildNumber";

    private static final String DURATION_NAME_PARAM_NAME = "duration";

    private static final String GROUP_NAME_PARAM_NAME = "group";

    private final BuildRepository buildRepository;

    private final UserRepository userRepository;

    @Autowired
    public FileUploadController(BuildRepository buildRepository, UserRepository userRepository) {
        this.buildRepository = buildRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/UploadServlet")
    public ResponseEntity<?> handleFileUpload(@RequestParam Map<String, String> allRequestParams,
            @RequestParam(name = "testsuite", required = false) MultipartFile file) {
        try {
            buildRepository.save(createBuild(allRequestParams, file));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private Build createBuild(Map<String, String> allRequestParams, MultipartFile file) throws Exception {
        Build build = new Build();
        if (file != null) {
            buildRepository.fillBuildByTestsuites(IOUtils.unzipAndUnmarshalTestsuite(file.getInputStream()), build);
        }
        allRequestParams.entrySet().forEach(entry -> {
            final String name = entry.getKey();
            final String value = entry.getValue();
            log.debug("Uploaded fileItem with name = {} and value = {}", name, value);
            switch (name) {
                case JOB_NAME_PARAM_NAME: {
                    build.setFullName(value);
                    break;
                }
                case BUILD_NUMBER_NAME_PARAM_NAME: {
                    build.setBuildNumber(toLong(value));
                    break;
                }
                case DURATION_NAME_PARAM_NAME: {
                    build.setDuration(toLong(value));
                    break;
                }
                case GROUP_NAME_PARAM_NAME: {
                    List<Group> groups = userRepository.getCurrentUser().getGroups().stream().filter(g -> value.equals(g.getName())).collect(
                            Collectors.toList());
                    if (groups.isEmpty()) {
                        throw new IllegalStateException("Uploader " + userRepository.getCurrentUserName() + " has no group " + value);
                    }
                    build.setGroupId(groups.get(0).getId());
                    break;
                }
                default:
                    buildRepository.addIfAbsent(new BuildProperty(name, value), build.getBuildProperties());
            }
        });
        return build;
    }

    private Long toLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}