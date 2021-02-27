package dbflow.chat.service;

import dbflow.chat.service.dto.ChatDTO;
import dbflow.chat.service.dto.MessageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link dbflow.chat.domain.Message}.
 */
public interface MessageService {

    /**
     * Save a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    MessageDTO save(MessageDTO messageDTO);

    /**
     * Get all the messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageDTO> findAll(Pageable pageable);


    /**
     * Get the "id" message.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MessageDTO> findOne(Long id);

    /**
     * Get all the user chats
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageDTO> findAllByChat(Long chatId, Pageable pageable);
    
    /**
     * Delete the "id" message.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the message corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageDTO> search(String query, Pageable pageable);
}
