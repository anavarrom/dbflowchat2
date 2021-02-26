package dbflow.chat.repository;

import dbflow.chat.domain.Chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Chat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	Page<Chat> findAllByOwnerOrTo(String from, String to, Pageable pageable);

}
