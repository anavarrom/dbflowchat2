package dbflow.chat.service.impl;

import dbflow.chat.service.ChatService;
import dbflow.chat.domain.Chat;
import dbflow.chat.repository.ChatRepository;
import dbflow.chat.repository.search.ChatSearchRepository;
import dbflow.chat.service.dto.ChatDTO;
import dbflow.chat.service.mapper.ChatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Chat}.
 */
@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatRepository chatRepository;

    private final ChatMapper chatMapper;

    private final ChatSearchRepository chatSearchRepository;

    public ChatServiceImpl(ChatRepository chatRepository, ChatMapper chatMapper, ChatSearchRepository chatSearchRepository) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.chatSearchRepository = chatSearchRepository;
    }

    @Override
    public ChatDTO save(ChatDTO chatDTO) {
        log.debug("Request to save Chat : {}", chatDTO);
        Chat chat = chatMapper.toEntity(chatDTO);
        chat = chatRepository.save(chat);
        ChatDTO result = chatMapper.toDto(chat);
        chatSearchRepository.save(chat);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Chats");
        return chatRepository.findAll(pageable)
            .map(chatMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ChatDTO> findOne(Long id) {
        log.debug("Request to get Chat : {}", id);
        return chatRepository.findById(id)
            .map(chatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Chat : {}", id);
        chatRepository.deleteById(id);
        chatSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Chats for query {}", query);
        return chatSearchRepository.search(queryStringQuery(query), pageable)
            .map(chatMapper::toDto);
    }

	@Override
	public Page<ChatDTO> findAllByUser(String username, Pageable pageable) {
		// TODO Auto-generated method stub
        return chatRepository.findAllByOwnerOrTo(username, username, pageable).map(chatMapper::toDto);
	}
}
