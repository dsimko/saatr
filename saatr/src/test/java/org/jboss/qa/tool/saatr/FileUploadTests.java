package org.jboss.qa.tool.saatr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(secure = false)
@SpringBootTest({ "spring.data.mongodb.port=0" })
public class FileUploadTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private BuildRepository buildRepository;

	@Test
	public void shouldSaveUploadedFile() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("testsuite", "test.txt", "text/plain",
				"Spring Framework".getBytes());
		this.mvc.perform(fileUpload("/UploadServlet").file(multipartFile).header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("saatr:S44TR!".getBytes()))).andExpect(status().isOk());
		//then(this.buildRepository).should().save(new BuildDocument());
		assertThat(buildRepository.count()).isEqualTo(1);
	}
}