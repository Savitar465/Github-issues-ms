package com.githubx.Github_issues_ms;

import com.githubx.Github_issues_ms.dao.IssueCommentDao;
import com.githubx.Github_issues_ms.dao.IssueDao;
import com.githubx.Github_issues_ms.dao.LabelDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
	}
)
@ActiveProfiles("test")
class GithubIssuesMsApplicationTests {

	@MockBean
	private JwtDecoder jwtDecoder;

	@MockBean private IssueDao issueDao;
	@MockBean private IssueCommentDao issueCommentDao;
	@MockBean private LabelDao labelDao;

	@Test
	void contextLoads() {
	}
}
