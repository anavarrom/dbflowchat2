package dbflow.chat.repository.search;

import dbflow.chat.domain.Chat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Chat} entity.
 */
public interface ChatSearchRepository extends ElasticsearchRepository<Chat, Long> {
}
