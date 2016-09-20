package org.jboss.qa.tool.saatr.web.upload;

import java.util.Map;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
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
	private static final String TIMESTAMP_NAME_PARAM_NAME = "timestamp";
	private static final String DURATION_NAME_PARAM_NAME = "duration";

	private final BuildRepository buildRepository;

	@Autowired
	public FileUploadController(BuildRepository buildRepository) {
		this.buildRepository = buildRepository;
	}

	@PostMapping("/UploadServlet")
	public ResponseEntity<?> handleFileUpload(@RequestParam Map<String, String> allRequestParams,
			@RequestParam("testsuite") MultipartFile file) {
		try {
			buildRepository.save(createBuild(allRequestParams, file));
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private BuildDocument createBuild(Map<String, String> allRequestParams, MultipartFile file) throws Exception {
		BuildDocument build = new BuildDocument();
		buildRepository.fillBuildByTestsuites(IOUtils.unzipAndUnmarshalTestsuite(file.getInputStream()), build);
		allRequestParams.entrySet().forEach(entry -> {
			final String name = entry.getKey();
			final String value = entry.getValue();
			log.debug("Uploaded fileItem with name = {} and value = {}", name, value);
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
				buildRepository.addIfAbsent(new PropertyData(name, value), build.getVariables());
			}
		});
		return build;
	}

}