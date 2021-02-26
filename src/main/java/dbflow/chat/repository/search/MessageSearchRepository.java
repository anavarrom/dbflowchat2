package dbflow.chat.repository.search;

import dbflow.chat.domain.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Message} entity.
 */
public interface MessageSearchRepository extends ElasticsearchRepository<Message, Long> {
}
