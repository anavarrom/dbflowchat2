package dbflow.chat.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ChatSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ChatSearchRepositoryMockConfiguration {

    @MockBean
    private ChatSearchRepository mockChatSearchRepository;

}
